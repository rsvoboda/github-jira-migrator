package com.migrator.service;

import com.migrator.analyzer.IssueAnalyzer;
import com.migrator.analyzer.LabelMapper;
import com.migrator.analyzer.TypeDetector;
import com.migrator.model.analysis.AnalysisResult;
import com.migrator.model.analysis.JiraIssueType;
import com.migrator.model.analysis.Priority;
import com.migrator.model.analysis.RepositoryAnalysisRequest;
import com.migrator.model.github.GitHubIssue;
import com.migrator.model.github.GitHubLabel;
import com.migrator.model.github.GitHubUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IssueMigrationServiceTest {

    private IssueAnalyzer issueAnalyzer;

    @BeforeEach
    void setUp() {
        TypeDetector typeDetector = new TypeDetector();
        LabelMapper labelMapper = new LabelMapper();
        issueAnalyzer = new IssueAnalyzer(typeDetector, labelMapper, "TASK", 0.5);
    }

    @Test
    void analyzeIssue_withBugLabels_shouldDetectBugType() {
        GitHubIssue bugIssue = new GitHubIssue();
        bugIssue.setNumber(1);
        bugIssue.setTitle("Bug: Application crashes");
        bugIssue.setBody("The app crashes on startup");
        bugIssue.setState("open");
        bugIssue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        bugIssue.setUser(new GitHubUser("testuser"));
        bugIssue.setLabels(Arrays.asList(
                new GitHubLabel("bug"),
                new GitHubLabel("priority/high")
        ));
        bugIssue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(bugIssue);

        assertTrue(result.isAnalyzedSuccessfully());
        assertEquals(JiraIssueType.BUG, result.getSuggestedType());
        assertTrue(result.getSuggestedLabels().contains("bug"));
        assertTrue(result.getSuggestedComponents().contains("Bug Fixes"));
    }

    @Test
    void analyzeIssue_withFeatureLabels_shouldDetectStoryType() {
        GitHubIssue featureIssue = new GitHubIssue();
        featureIssue.setNumber(2);
        featureIssue.setTitle("Feature: Add dark mode");
        featureIssue.setBody("As a user, I want dark mode");
        featureIssue.setState("open");
        featureIssue.setHtmlUrl("https://github.com/owner/repo/issues/2");
        featureIssue.setUser(new GitHubUser("testuser"));
        featureIssue.setLabels(Arrays.asList(
                new GitHubLabel("enhancement"),
                new GitHubLabel("area/ui")
        ));
        featureIssue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(featureIssue);

        assertTrue(result.isAnalyzedSuccessfully());
        assertEquals(JiraIssueType.STORY, result.getSuggestedType());
        assertTrue(result.getSuggestedComponents().contains("UI/UX"));
    }

    @Test
    void analyzeIssue_preservesOriginalLabels() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Sample Bug Issue");
        issue.setBody("This is a sample bug issue for testing");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(
                new GitHubLabel("bug"),
                new GitHubLabel("area/frontend")
        ));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertNotNull(result.getOriginalLabels());
        assertEquals(2, result.getOriginalLabels().size());
        assertTrue(result.getOriginalLabels().contains("bug"));
        assertTrue(result.getOriginalLabels().contains("area/frontend"));
    }

    @Test
    void analyzeIssue_includesPriorityRecommendation() {
        GitHubIssue priorityIssue = new GitHubIssue();
        priorityIssue.setNumber(5);
        priorityIssue.setTitle("Critical issue");
        priorityIssue.setBody("This is critical");
        priorityIssue.setState("open");
        priorityIssue.setHtmlUrl("https://github.com/owner/repo/issues/5");
        priorityIssue.setUser(new GitHubUser("testuser"));
        priorityIssue.setLabels(Arrays.asList(new GitHubLabel("priority/high")));
        priorityIssue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(priorityIssue);

        assertEquals(Priority.HIGH, result.getSuggestedPriority());
    }

    @Test
    void analyzeIssue_withMultipleLabels_shouldMapAll() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Complex issue");
        issue.setBody("This is a complex issue");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(
                new GitHubLabel("bug"),
                new GitHubLabel("area/ui"),
                new GitHubLabel("priority/medium"),
                new GitHubLabel("security")
        ));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertTrue(result.getSuggestedLabels().contains("bug"));
        assertTrue(result.getSuggestedLabels().contains("security"));
        assertTrue(result.getSuggestedLabels().contains("priority-medium"));
        assertTrue(result.getSuggestedComponents().contains("Bug Fixes"));
        assertTrue(result.getSuggestedComponents().contains("UI/UX"));
        assertTrue(result.getSuggestedComponents().contains("Security"));
        assertEquals(Priority.MEDIUM, result.getSuggestedPriority());
    }

    @Test
    void analyzeIssue_withExcludedLabel_shouldMarkAsExcluded() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Won't fix this");
        issue.setBody("This issue won't be fixed");
        issue.setState("closed");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(new GitHubLabel("wontfix")));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertTrue(result.isAnalyzedSuccessfully());
        assertTrue(result.getSuggestedLabels().contains("excluded"));
        assertEquals(1.0, result.getConfidenceScore());
    }

    @Test
    void analyzeIssue_withNoLabels_shouldUseDefaultType() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Simple task");
        issue.setBody("Just a simple task");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList());
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertNotNull(result.getSuggestedType());
        assertNotNull(result.getSuggestedPriority());
        assertNotNull(result.getReasoning());
    }

    @Test
    void analyzeIssue_withGoodFirstIssue_shouldMapCorrectly() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Good first issue");
        issue.setBody("This is suitable for new contributors");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(new GitHubLabel("good-first-issue")));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertTrue(result.getSuggestedLabels().contains("good-first-issue"));
    }

    @Test
    void analyzeIssue_withBreakingChange_shouldMapCorrectly() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Breaking change");
        issue.setBody("This change breaks backward compatibility");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(new GitHubLabel("breaking-change")));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertTrue(result.getSuggestedLabels().contains("breaking-change"));
        assertTrue(result.getSuggestedComponents().contains("Breaking Changes"));
    }

    @Test
    void analyzeIssue_includesReasoning() {
        GitHubIssue issue = new GitHubIssue();
        issue.setNumber(1);
        issue.setTitle("Bug report");
        issue.setBody("This is a bug");
        issue.setState("open");
        issue.setHtmlUrl("https://github.com/owner/repo/issues/1");
        issue.setUser(new GitHubUser("testuser"));
        issue.setLabels(Arrays.asList(new GitHubLabel("bug")));
        issue.setCreatedAt(Instant.now());

        AnalysisResult result = issueAnalyzer.analyze(issue);

        assertNotNull(result.getReasoning());
        assertFalse(result.getReasoning().isEmpty());
    }
}
