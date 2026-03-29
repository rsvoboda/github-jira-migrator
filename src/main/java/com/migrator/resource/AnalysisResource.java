package com.migrator.resource;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.migrator.model.analysis.AnalysisResult;
import com.migrator.model.analysis.RepositoryAnalysisRequest;
import com.migrator.service.IssueMigrationService;
import com.migrator.service.JiraMigrationService;
import com.migrator.service.MigrationHistoryService;
import com.migrator.service.MigrationHistoryService.MigrationRecord;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@Path("/api/v1/analyze")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalysisResource {

    private static final Logger LOG = Logger.getLogger(AnalysisResource.class);

    @Inject
    IssueMigrationService migrationService;

    @Inject
    JiraMigrationService jiraMigrationService;

    @Inject
    MigrationHistoryService historyService;

    @GET
    @Path("/issue")
    public Response analyzeIssue(
            @QueryParam("owner") String owner,
            @QueryParam("repo") String repo,
            @QueryParam("issueNumber") Integer issueNumber,
            @QueryParam("url") String issueUrl) {

        if (issueUrl != null && !issueUrl.isEmpty()) {
            return analyzeByUrl(issueUrl);
        }

        if (owner == null || repo == null || issueNumber == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "owner, repo, and issueNumber are required"))
                    .build();
        }

        LOG.infof("Analyzing issue: %s/%s#%d", owner, repo, issueNumber);
        AnalysisResult result = migrationService.analyzeIssue(owner, repo, issueNumber);

        if (!result.isAnalyzedSuccessfully()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(result)
                    .build();
        }

        return Response.ok(result).build();
    }

    @POST
    @Path("/repository")
    public Response analyzeRepository(RepositoryAnalysisRequest request) {
        if (request.getOwner() == null || request.getRepo() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "owner and repo are required"))
                    .build();
        }

        LOG.infof("Analyzing repository: %s/%s", request.getOwner(), request.getRepo());
        List<AnalysisResult> results = migrationService.analyzeRepository(request);

        Map<String, Object> response = Map.of(
                "repository", request.getOwner() + "/" + request.getRepo(),
                "totalAnalyzed", results.size(),
                "successful", results.stream().filter(AnalysisResult::isAnalyzedSuccessfully).count(),
                "failed", results.stream().filter(r -> !r.isAnalyzedSuccessfully()).count(),
                "results", results
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of(
                "status", "UP",
                "service", "GitHub-JIRA Migration Analyzer"
        )).build();
    }

    private Response analyzeByUrl(String issueUrl) {
        return migrationService.analyzeIssueByUrl(issueUrl)
                .map(result -> {
                    if (!result.isAnalyzedSuccessfully()) {
                        return Response.status(Response.Status.NOT_FOUND).entity(result).build();
                    }
                    return Response.ok(result).build();
                })
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid GitHub issue URL"))
                        .build());
    }

    @POST
    @Path("/issue/{issueNumber}/migrate")
    public Response migrateIssueToJira(
            @PathParam("issueNumber") Integer issueNumber,
            @QueryParam("owner") String owner,
            @QueryParam("repo") String repo,
            @QueryParam("projectKey") String projectKey) {

        if (owner == null || repo == null || issueNumber == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "owner, repo, and issueNumber are required"))
                    .build();
        }

        if (projectKey == null || projectKey.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "projectKey is required"))
                    .build();
        }

        LOG.infof("Migrating issue %s/%s#%d to JIRA project %s", owner, repo, issueNumber, projectKey);

        AnalysisResult analysisResult = migrationService.analyzeIssue(owner, repo, issueNumber);

        MigrationRecord record = new MigrationRecord();
        record.setGithubIssueUrl("https://github.com/" + owner + "/" + repo + "/issues/" + issueNumber);
        record.setGithubIssueNumber(String.valueOf(issueNumber));
        record.setGithubRepo(owner + "/" + repo);
        record.setJiraProjectKey(projectKey);
        record.setSuggestedType(analysisResult.getSuggestedType() != null ? analysisResult.getSuggestedType().name() : "Task");
        record.setSuggestedComponents(analysisResult.getSuggestedComponents());
        if (!analysisResult.isAnalyzedSuccessfully()) {
            record.setStatus("FAILED");
            record.setErrorMessage("Analysis failed: " + analysisResult.getErrorMessage());
            historyService.recordMigration(record);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to analyze GitHub issue", "details", analysisResult.getErrorMessage()))
                    .build();
        }

        try {
            BasicIssue jiraIssue = jiraMigrationService.createIssueFromAnalysis(analysisResult, projectKey);
            record.setStatus("SUCCESS");
            record.setJiraIssueKey(jiraIssue.getKey());
            record.setJiraIssueUrl(jiraMigrationService.getBrowseUrl(jiraIssue.getKey()));
            historyService.recordMigration(record);
            return Response.ok(Map.of(
                    "githubIssue", analysisResult.getIssueNumber(),
                    "jiraIssueKey", jiraIssue.getKey(),
                    "jiraIssueUrl", jiraMigrationService.getBrowseUrl(jiraIssue.getKey()),
                    "analysis", analysisResult
            )).build();
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage(e.getMessage());
            historyService.recordMigration(record);
            LOG.errorf(e, "Failed to migrate issue to JIRA");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create JIRA issue", "details", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/issue/migrate")
    public Response migrateIssueToJira(
            @QueryParam("projectKey") String projectKey,
            @QueryParam("summary") String summary,
            @QueryParam("description") String description,
            @QueryParam("issueType") String issueType,
            @QueryParam("components") String components,
            @QueryParam("labels") String labels,
            @QueryParam("priority") String priority) {

        if (projectKey == null || projectKey.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "projectKey is required"))
                    .build();
        }

        if (summary == null || summary.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "summary is required"))
                    .build();
        }

        try {
            List<String> componentList = components != null ? List.of(components.split(",")) : null;
            List<String> labelList = labels != null ? List.of(labels.split(",")) : null;

            BasicIssue jiraIssue = jiraMigrationService.createIssue(
                    projectKey, summary, description, issueType, 
                    componentList, labelList, priority);

            return Response.ok(Map.of(
                    "jiraIssueKey", jiraIssue.getKey(),
                    "jiraIssueUrl", jiraMigrationService.getBrowseUrl(jiraIssue.getKey())
            )).build();
        } catch (Exception e) {
            LOG.errorf(e, "Failed to create JIRA issue");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create JIRA issue", "details", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/history")
    public Response getMigrationHistory(@QueryParam("projectKey") String projectKey) {
        List<MigrationRecord> history;
        if (projectKey != null && !projectKey.isEmpty()) {
            history = historyService.getHistoryByProject(projectKey);
        } else {
            history = historyService.getHistory();
        }
        return Response.ok(Map.of(
                "total", history.size(),
                "migrations", history
        )).build();
    }
}
