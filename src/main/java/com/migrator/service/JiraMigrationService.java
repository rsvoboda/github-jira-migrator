package com.migrator.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.httpclient.api.Request;
import com.migrator.model.analysis.AnalysisResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JiraMigrationService {

    private static final Logger LOG = Logger.getLogger(JiraMigrationService.class);

    @ConfigProperty(name = "jira.base-url")
    String jiraBaseUrl;

    @ConfigProperty(name = "jira.username")
    String username;

    @ConfigProperty(name = "jira.api-token")
    String apiToken;

    @ConfigProperty(name = "jira.default-project-key")
    String defaultProjectKey;

    public BasicIssue createIssueFromAnalysis(AnalysisResult analysisResult) {
        return createIssueFromAnalysis(analysisResult, defaultProjectKey);
    }

    public BasicIssue createIssueFromAnalysis(AnalysisResult analysisResult, String projectKey) {
        LOG.infof("Creating JIRA issue in project %s: %s", projectKey, analysisResult.getIssueTitle());

        try (JiraRestClient restClient = createClient()) {
            Project project = restClient.getProjectClient().getProject(projectKey).claim();
            
            Optional<IssueType> issueType = findIssueTypeByName(project, 
                analysisResult.getSuggestedType() != null ? analysisResult.getSuggestedType().name() : "Task");
            
            Long issueTypeId = issueType.map(IssueType::getId).orElse(1L);

            IssueInputBuilder builder = new IssueInputBuilder(projectKey, issueTypeId);
            builder.setSummary(analysisResult.getIssueTitle());
            builder.setDescription(createDescription(analysisResult));

            IssueInput issueInput = builder.build();
            BasicIssue issue = restClient.getIssueClient().createIssue(issueInput).claim();
            
            LOG.infof("Created JIRA issue: %s", issue.getKey());
            return issue;
        } catch (Exception e) {
            LOG.errorf(e, "Failed to create JIRA issue: %s", analysisResult.getIssueTitle());
            throw new RuntimeException("Failed to create JIRA issue: " + e.getMessage(), e);
        }
    }

    public BasicIssue createIssue(String projectKey, String summary, String description, String issueTypeName, 
                              java.util.List<String> components, java.util.List<String> labels, String priority) {
        LOG.infof("Creating JIRA issue in project %s: %s", projectKey, summary);

        try (JiraRestClient restClient = createClient()) {
            Project project = restClient.getProjectClient().getProject(projectKey).claim();
            
            Optional<IssueType> issueType = findIssueTypeByName(project, issueTypeName != null ? issueTypeName : "Task");
            Long issueTypeId = issueType.map(IssueType::getId).orElse(1L);

            IssueInputBuilder builder = new IssueInputBuilder(projectKey, issueTypeId);
            builder.setSummary(summary);
            
            if (description != null) {
                builder.setDescription(description);
            }

            if (components != null && !components.isEmpty()) {
                builder.setComponentsNames(components);
            }

            if (labels != null && !labels.isEmpty()) {
                builder.setFieldValue("labels", labels);
            }

            if (priority != null && !priority.isEmpty()) {
                Optional<Priority> priorityObj = findPriorityByName(restClient, priority);
                priorityObj.ifPresent(builder::setPriority);
            }

            IssueInput issueInput = builder.build();
            BasicIssue issue = restClient.getIssueClient().createIssue(issueInput).claim();
            
            LOG.infof("Created JIRA issue: %s", issue.getKey());
            return issue;
        } catch (Exception e) {
            LOG.errorf(e, "Failed to create JIRA issue: %s", summary);
            throw new RuntimeException("Failed to create JIRA issue: " + e.getMessage(), e);
        }
    }

    private Optional<IssueType> findIssueTypeByName(Project project, String name) {
        for (IssueType it : project.getIssueTypes()) {
            if (it.getName().equalsIgnoreCase(name)) {
                return Optional.of(it);
            }
        }
        return Optional.empty();
    }

    private Optional<Priority> findPriorityByName(JiraRestClient restClient, String name) {
        try {
            Iterable<Priority> priorities = restClient.getMetadataClient().getPriorities().claim();
            for (Priority p : priorities) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return Optional.of(p);
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to get priorities: " + e.getMessage());
        }
        return Optional.empty();
    }

    private JiraRestClient createClient() throws URISyntaxException {
        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        return factory.create(new URI(jiraBaseUrl), new AuthenticationHandler() {
            @Override
            public void configure(Request.Builder builder) {
                String credentials = username + ":" + apiToken;
                String encoded = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
                builder.setHeader("Authorization", "Basic " + encoded);
            }
        });
    }

    private String createDescription(AnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("h3. GitHub Issue\n");
        sb.append("* URL: ").append(result.getIssueUrl()).append("\n");
        sb.append("* Number: #").append(result.getIssueNumber()).append("\n");
        sb.append("* Author: ").append(result.getAuthor()).append("\n");
        sb.append("* State: ").append(result.getIssueState()).append("\n");
        sb.append("* Created: ").append(result.getCreatedAt()).append("\n\n");

        if (result.getOriginalLabels() != null && !result.getOriginalLabels().isEmpty()) {
            sb.append("h3. Original GitHub Labels\n");
            for (String label : result.getOriginalLabels()) {
                sb.append("* ").append(label).append("\n");
            }
            sb.append("\n");
        }

        sb.append("h3. Migration Analysis\n");
        sb.append("* Suggested Type: ").append(result.getSuggestedType()).append("\n");
        sb.append("* Confidence Score: ").append(String.format("%.2f", result.getConfidenceScore())).append("\n");
        if (result.getReasoning() != null) {
            sb.append("* Reasoning: ").append(result.getReasoning()).append("\n");
        }

        return sb.toString();
    }
}
