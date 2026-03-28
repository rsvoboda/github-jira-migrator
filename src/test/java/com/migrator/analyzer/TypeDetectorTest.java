package com.migrator.analyzer;

import com.migrator.model.analysis.JiraIssueType;
import com.migrator.model.github.GitHubIssue;
import com.migrator.model.github.GitHubUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeDetectorTest {

    private TypeDetector typeDetector;

    @BeforeEach
    void setUp() {
        typeDetector = new TypeDetector();
    }

    @Test
    void detectType_withBugInTitle_shouldReturnBug() {
        GitHubIssue issue = createIssue("Bug in user authentication", "The login fails");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.BUG, result);
    }

    @Test
    void detectType_withCrashesInBody_shouldReturnBug() {
        GitHubIssue issue = createIssue("Application crashes", "The app crashes when clicking the button");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.BUG, result);
    }

    @Test
    void detectType_withFeatureRequest_shouldReturnStory() {
        GitHubIssue issue = createIssue("Feature request: Dark mode", 
                "As a user, I want dark mode so that I can use the app at night");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.STORY, result);
    }

    @Test
    void detectType_withEnhancement_shouldReturnStory() {
        GitHubIssue issue = createIssue("Enhancement: Add search functionality", 
                "Please add a search feature to the dashboard");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.STORY, result);
    }

    @Test
    void detectType_withCleanupTask_shouldReturnTask() {
        GitHubIssue issue = createIssue("Cleanup old code", 
                "Remove deprecated utility methods and refactor the codebase");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.TASK, result);
    }

    @Test
    void detectType_withLargeContent_shouldSuggestEpic() {
        String largeBody = "This is a large epic that spans across multiple areas:\n" +
                "Section 1 details and requirements\n" +
                "Section 2 details and requirements\n".repeat(100);
        GitHubIssue issue = createIssue("Epic: System overhaul", largeBody);
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.EPIC, result);
    }

    @Test
    void detectType_withNoKeywords_shouldReturnTask() {
        GitHubIssue issue = createIssue("Update configuration", "Update the config file");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.TASK, result);
    }

    @Test
    void detectType_withNullIssue_shouldReturnTask() {
        JiraIssueType result = typeDetector.detectType(null);
        
        assertEquals(JiraIssueType.TASK, result);
    }

    @Test
    void detectType_withEmptyBody_shouldReturnTask() {
        GitHubIssue issue = createIssue("Title only", null);
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.TASK, result);
    }

    @Test
    void calculateTypeConfidence_withMatchingKeywords_shouldReturnHighConfidence() {
        GitHubIssue issue = createIssue("Bug: Authentication broken", 
                "The authentication bug causes users to be logged out unexpectedly. " +
                "This is a critical bug that crashes the system.");
        
        JiraIssueType type = typeDetector.detectType(issue);
        double confidence = typeDetector.calculateTypeConfidence(type, issue);
        
        assertTrue(confidence > 0.5, "Confidence should be above 0.5");
    }

    @Test
    void calculateTypeConfidence_withNoKeywords_shouldReturnLowerConfidence() {
        GitHubIssue issue = createIssue("Title for testing", "Body content without matching keywords");
        
        JiraIssueType type = typeDetector.detectType(issue);
        double confidence = typeDetector.calculateTypeConfidence(type, issue);
        
        assertTrue(confidence >= 0.3 && confidence <= 0.7, 
                "Confidence should be between 0.3 and 0.7 but was " + confidence);
    }

    @Test
    void detectType_withRegressionKeyword_shouldReturnBug() {
        GitHubIssue issue = createIssue("Regression in v2.0", 
                "This is a regression from the previous version");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.BUG, result);
    }

    @Test
    void detectType_withEpicKeyword_shouldReturnEpic() {
        GitHubIssue issue = createIssue("Epic: New payment system", 
                "This epic covers the entire payment system overhaul");
        
        JiraIssueType result = typeDetector.detectType(issue);
        
        assertEquals(JiraIssueType.EPIC, result);
    }

    private GitHubIssue createIssue(String title, String body) {
        GitHubIssue issue = new GitHubIssue();
        issue.setTitle(title);
        issue.setBody(body);
        issue.setUser(new GitHubUser("testuser"));
        return issue;
    }
}
