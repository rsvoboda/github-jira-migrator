package com.migrator.model.analysis;

import com.migrator.model.github.GitHubIssue;

import java.time.Instant;
import java.util.List;

public class AnalysisResult {

    private Integer issueNumber;
    private String issueTitle;
    private String issueUrl;
    private String issueState;
    private String author;
    private Instant createdAt;
    private List<String> originalLabels;
    
    private JiraIssueType suggestedType;
    private List<String> suggestedLabels;
    private List<String> suggestedComponents;
    private Priority suggestedPriority;
    private double confidenceScore;
    private String reasoning;

    private List<String> warnings;
    private boolean analyzedSuccessfully;
    private String errorMessage;

    public AnalysisResult() {
        this.analyzedSuccessfully = true;
    }

    public static AnalysisResult fromIssue(GitHubIssue issue) {
        AnalysisResult result = new AnalysisResult();
        result.setIssueNumber(issue.getNumber());
        result.setIssueTitle(issue.getTitle());
        result.setIssueUrl(issue.getHtmlUrl());
        result.setIssueState(issue.getState());
        result.setAuthor(issue.getUser() != null ? issue.getUser().getLogin() : null);
        result.setCreatedAt(issue.getCreatedAt());
        if (issue.getLabels() != null) {
            result.setOriginalLabels(issue.getLabels().stream()
                    .map(l -> l.getName())
                    .toList());
        }
        return result;
    }

    public static AnalysisResult errorResult(Integer issueNumber, String errorMessage) {
        AnalysisResult result = new AnalysisResult();
        result.setIssueNumber(issueNumber);
        result.setAnalyzedSuccessfully(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public Integer getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(Integer issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getIssueUrl() {
        return issueUrl;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String issueState) {
        this.issueState = issueState;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getOriginalLabels() {
        return originalLabels;
    }

    public void setOriginalLabels(List<String> originalLabels) {
        this.originalLabels = originalLabels;
    }

    public JiraIssueType getSuggestedType() {
        return suggestedType;
    }

    public void setSuggestedType(JiraIssueType suggestedType) {
        this.suggestedType = suggestedType;
    }

    public List<String> getSuggestedLabels() {
        return suggestedLabels;
    }

    public void setSuggestedLabels(List<String> suggestedLabels) {
        this.suggestedLabels = suggestedLabels;
    }

    public List<String> getSuggestedComponents() {
        return suggestedComponents;
    }

    public void setSuggestedComponents(List<String> suggestedComponents) {
        this.suggestedComponents = suggestedComponents;
    }

    public Priority getSuggestedPriority() {
        return suggestedPriority;
    }

    public void setSuggestedPriority(Priority suggestedPriority) {
        this.suggestedPriority = suggestedPriority;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public boolean isAnalyzedSuccessfully() {
        return analyzedSuccessfully;
    }

    public void setAnalyzedSuccessfully(boolean analyzedSuccessfully) {
        this.analyzedSuccessfully = analyzedSuccessfully;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void applyConfiguration(JiraConfiguration config) {
        this.suggestedType = config.getSuggestedType();
        this.suggestedLabels = config.getSuggestedLabelsAsList();
        this.suggestedComponents = config.getSuggestedComponentsAsList();
        this.suggestedPriority = config.getSuggestedPriority();
        this.confidenceScore = config.getConfidenceScore();
        this.reasoning = config.getReasoning();
    }
}
