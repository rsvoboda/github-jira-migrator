package com.migrator.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubIssue {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @JsonProperty("state")
    private String state;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("user")
    private GitHubUser user;

    @JsonProperty("labels")
    private List<GitHubLabel> labels;

    @JsonProperty("assignee")
    private GitHubUser assignee;

    @JsonProperty("assignees")
    private List<GitHubUser> assignees;

    @JsonProperty("comments")
    private Integer comments;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonProperty("closed_at")
    private Instant closedAt;

    @JsonProperty("repository_url")
    private String repositoryUrl;

    public GitHubIssue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public GitHubUser getUser() {
        return user;
    }

    public void setUser(GitHubUser user) {
        this.user = user;
    }

    public List<GitHubLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<GitHubLabel> labels) {
        this.labels = labels;
    }

    public GitHubUser getAssignee() {
        return assignee;
    }

    public void setAssignee(GitHubUser assignee) {
        this.assignee = assignee;
    }

    public List<GitHubUser> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<GitHubUser> assignees) {
        this.assignees = assignees;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryFullName() {
        if (repositoryUrl != null && repositoryUrl.contains("repos/")) {
            String[] parts = repositoryUrl.replace("https://api.github.com/repos/", "").split("/");
            if (parts.length >= 2) {
                return parts[0] + "/" + parts[1];
            }
        }
        return null;
    }

    public String getCombinedText() {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title).append(" ");
        }
        if (body != null) {
            sb.append(body);
        }
        return sb.toString().toLowerCase();
    }

    public int getWordCount() {
        if (body == null || body.isEmpty()) {
            return 0;
        }
        return body.trim().split("\\s+").length;
    }
}
