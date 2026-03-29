package com.migrator.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class MigrationHistoryService {

    private static final Logger LOG = Logger.getLogger(MigrationHistoryService.class);
    private static final int MAX_HISTORY_SIZE = 1000;

    private final ConcurrentLinkedQueue<MigrationRecord> history = new ConcurrentLinkedQueue<>();

    public void recordMigration(MigrationRecord record) {
        history.add(record);
        
        while (history.size() > MAX_HISTORY_SIZE) {
            history.poll();
        }
        
        LOG.infof("Recorded migration: %s -> %s", record.getGithubIssueUrl(), record.getJiraIssueKey());
    }

    public List<MigrationRecord> getHistory() {
        List<MigrationRecord> list = new ArrayList<>(history);
        Collections.reverse(list);
        return list;
    }

    public List<MigrationRecord> getHistoryByProject(String projectKey) {
        return getHistory().stream()
                .filter(r -> projectKey.equalsIgnoreCase(r.getJiraProjectKey()))
                .toList();
    }

    public MigrationRecord getByGithubIssue(String githubIssueUrl) {
        return getHistory().stream()
                .filter(r -> githubIssueUrl.equals(r.getGithubIssueUrl()))
                .findFirst()
                .orElse(null);
    }

    public void clear() {
        history.clear();
    }

    public static class MigrationRecord {
        private String githubIssueUrl;
        private String githubIssueNumber;
        private String githubRepo;
        private String jiraIssueKey;
        private String jiraIssueUrl;
        private String jiraProjectKey;
        private String suggestedType;
        private List<String> suggestedComponents;
        private String status;
        private String errorMessage;
        private Instant timestamp;

        public MigrationRecord() {
            this.timestamp = Instant.now();
        }

        public String getGithubIssueUrl() {
            return githubIssueUrl;
        }

        public void setGithubIssueUrl(String githubIssueUrl) {
            this.githubIssueUrl = githubIssueUrl;
        }

        public String getGithubIssueNumber() {
            return githubIssueNumber;
        }

        public void setGithubIssueNumber(String githubIssueNumber) {
            this.githubIssueNumber = githubIssueNumber;
        }

        public String getGithubRepo() {
            return githubRepo;
        }

        public void setGithubRepo(String githubRepo) {
            this.githubRepo = githubRepo;
        }

        public String getJiraIssueKey() {
            return jiraIssueKey;
        }

        public void setJiraIssueKey(String jiraIssueKey) {
            this.jiraIssueKey = jiraIssueKey;
        }

        public String getJiraIssueUrl() {
            return jiraIssueUrl;
        }

        public void setJiraIssueUrl(String jiraIssueUrl) {
            this.jiraIssueUrl = jiraIssueUrl;
        }

        public String getJiraProjectKey() {
            return jiraProjectKey;
        }

        public void setJiraProjectKey(String jiraProjectKey) {
            this.jiraProjectKey = jiraProjectKey;
        }

        public String getSuggestedType() {
            return suggestedType;
        }

        public void setSuggestedType(String suggestedType) {
            this.suggestedType = suggestedType;
        }

        public List<String> getSuggestedComponents() {
            return suggestedComponents;
        }

        public void setSuggestedComponents(List<String> suggestedComponents) {
            this.suggestedComponents = suggestedComponents;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }
    }
}
