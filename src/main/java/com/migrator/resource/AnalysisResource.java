package com.migrator.resource;

import com.migrator.model.analysis.AnalysisResult;
import com.migrator.model.analysis.RepositoryAnalysisRequest;
import com.migrator.service.IssueMigrationService;
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
}
