package com.migrator.service;

import com.migrator.analyzer.IssueAnalyzer;
import com.migrator.client.GitHubClient;
import com.migrator.model.analysis.AnalysisResult;
import com.migrator.model.analysis.RepositoryAnalysisRequest;
import com.migrator.model.github.GitHubIssue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class IssueMigrationService {

    private static final Logger LOG = Logger.getLogger(IssueMigrationService.class);

    @Inject
    @RestClient
    GitHubClient githubClient;

    @Inject
    IssueAnalyzer issueAnalyzer;

    public AnalysisResult analyzeIssue(String owner, String repo, int issueNumber) {
        LOG.infof("Analyzing issue %s/%s#%d", owner, repo, issueNumber);
        
        try {
            GitHubIssue issue = githubClient.getIssue(owner, repo, issueNumber);
            return issueAnalyzer.analyze(issue);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to fetch issue %s/%s#%d", owner, repo, issueNumber);
            return AnalysisResult.errorResult(issueNumber, 
                    "Failed to fetch issue: " + e.getMessage());
        }
    }

    public List<AnalysisResult> analyzeRepository(RepositoryAnalysisRequest request) {
        LOG.infof("Analyzing repository %s/%s with state=%s, limit=%d", 
                request.getOwner(), request.getRepo(), request.getState(), request.getLimit());

        List<AnalysisResult> results = new ArrayList<>();
        
        try {
            String labelsParam = request.getLabels() != null && !request.getLabels().isEmpty()
                    ? String.join(",", request.getLabels())
                    : null;

            int perPage = Math.min(request.getLimit(), 100);
            int page = 1;
            int fetchedCount = 0;

            while (fetchedCount < request.getLimit()) {
                List<GitHubIssue> issues = githubClient.getIssues(
                        request.getOwner(),
                        request.getRepo(),
                        request.getState(),
                        labelsParam,
                        perPage,
                        page
                );

                if (issues == null || issues.isEmpty()) {
                    break;
                }

                for (GitHubIssue issue : issues) {
                    if (fetchedCount >= request.getLimit()) {
                        break;
                    }

                    try {
                        AnalysisResult result = issueAnalyzer.analyze(issue);
                        results.add(result);
                        fetchedCount++;
                    } catch (Exception e) {
                        LOG.warnf("Failed to analyze issue #%d: %s", 
                                issue.getNumber(), e.getMessage());
                        results.add(AnalysisResult.errorResult(
                                issue.getNumber(), 
                                "Analysis failed: " + e.getMessage()));
                    }
                }

                if (issues.size() < perPage) {
                    break;
                }
                page++;
            }

        } catch (Exception e) {
            LOG.errorf(e, "Failed to fetch issues from repository %s/%s", 
                    request.getOwner(), request.getRepo());
        }

        return results;
    }

    public Optional<AnalysisResult> analyzeIssueByUrl(String issueUrl) {
        if (issueUrl == null || !issueUrl.contains("github.com")) {
            return Optional.empty();
        }

        String[] parts = parseGitHubUrl(issueUrl);
        if (parts == null) {
            return Optional.empty();
        }

        try {
            int issueNumber = Integer.parseInt(parts[3]);
            return Optional.of(analyzeIssue(parts[1], parts[2], issueNumber));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String[] parseGitHubUrl(String url) {
        String cleaned = url.replace("https://github.com/", "")
                            .replace("http://github.com/", "")
                            .replace(".git", "");

        String[] parts = cleaned.split("/");
        if (parts.length >= 4 && "issues".equals(parts[3])) {
            return parts;
        }
        return null;
    }
}
