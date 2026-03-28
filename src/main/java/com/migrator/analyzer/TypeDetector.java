package com.migrator.analyzer;

import com.migrator.model.analysis.JiraIssueType;
import com.migrator.model.github.GitHubIssue;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class TypeDetector {

    private static final List<String> BUG_KEYWORDS = Arrays.asList(
            "bug", "defect", "crash", "broken", "error", "fail", "failing",
            "doesn't work", "not working", "issue", "wrong", "incorrect",
            "regression", "hotfix", "critical", "urgent"
    );

    private static final List<String> STORY_KEYWORDS = Arrays.asList(
            "feature", "enhancement", "request", "implement", "add",
            "support", "would like", "ability to", "allow users",
            "as a user", "so that", "i want"
    );

    private static final List<String> TASK_KEYWORDS = Arrays.asList(
            "task", "todo", "cleanup", "refactor", "update", "modify",
            "change", "improve", "optimize", "organize", "maintain",
            "technical debt", "tech debt", "docs", "configuration"
    );

    private static final List<String> EPIC_KEYWORDS = Arrays.asList(
            "epic", "large", "comprehensive", "multiple", "across",
            "system-wide", "architecture", "overhaul", "redesign"
    );

    private static final Map<Pattern, JiraIssueType> PATTERN_TYPE_MAP = new HashMap<>();

    static {
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bbug\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.BUG);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bdefect\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.BUG);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bcrash\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.BUG);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bbroken\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.BUG);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bfix(?:es|ed)?\\s+(?:this\\s+)?(?:issue|bug)\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.BUG);
        
        PATTERN_TYPE_MAP.put(Pattern.compile("\\b(?:new\\s+)?feature\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.STORY);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\benhancement\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.STORY);
        PATTERN_TYPE_MAP.put(Pattern.compile("\\b(?:feature|user)\\s+request\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.STORY);
        
        PATTERN_TYPE_MAP.put(Pattern.compile("\\bepic\\b", Pattern.CASE_INSENSITIVE), JiraIssueType.EPIC);
    }

    public JiraIssueType detectType(GitHubIssue issue) {
        if (issue == null) {
            return JiraIssueType.TASK;
        }

        String combinedText = issue.getCombinedText();
        if (combinedText == null || combinedText.isEmpty()) {
            return JiraIssueType.TASK;
        }

        Map<JiraIssueType, Integer> typeScores = new HashMap<>();
        typeScores.put(JiraIssueType.BUG, 0);
        typeScores.put(JiraIssueType.STORY, 0);
        typeScores.put(JiraIssueType.TASK, 0);
        typeScores.put(JiraIssueType.EPIC, 0);

        for (String keyword : BUG_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                typeScores.put(JiraIssueType.BUG, typeScores.get(JiraIssueType.BUG) + 2);
            }
        }

        for (String keyword : STORY_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                typeScores.put(JiraIssueType.STORY, typeScores.get(JiraIssueType.STORY) + 1);
            }
        }

        for (String keyword : TASK_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                typeScores.put(JiraIssueType.TASK, typeScores.get(JiraIssueType.TASK) + 1);
            }
        }

        for (String keyword : EPIC_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                typeScores.put(JiraIssueType.EPIC, typeScores.get(JiraIssueType.EPIC) + 1);
            }
        }

        for (Map.Entry<Pattern, JiraIssueType> entry : PATTERN_TYPE_MAP.entrySet()) {
            if (entry.getKey().matcher(combinedText).find()) {
                typeScores.put(entry.getValue(), typeScores.get(entry.getValue()) + 3);
            }
        }

        int wordCount = issue.getWordCount();
        if (wordCount > 500) {
            typeScores.put(JiraIssueType.EPIC, typeScores.get(JiraIssueType.EPIC) + 3);
        } else if (wordCount > 200) {
            typeScores.put(JiraIssueType.STORY, typeScores.get(JiraIssueType.STORY) + 2);
        }

        JiraIssueType detectedType = JiraIssueType.TASK;
        int maxScore = 0;

        for (Map.Entry<JiraIssueType, Integer> entry : typeScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                detectedType = entry.getKey();
            }
        }

        if (maxScore == 0) {
            detectedType = JiraIssueType.TASK;
        }

        return detectedType;
    }

    public double calculateTypeConfidence(JiraIssueType detectedType, GitHubIssue issue) {
        if (issue == null) {
            return 0.5;
        }

        String combinedText = issue.getCombinedText();
        if (combinedText == null || combinedText.isEmpty()) {
            return 0.3;
        }

        double confidence = 0.5;

        int matchCount = 0;
        List<String> keywords = getKeywordsForType(detectedType);

        for (String keyword : keywords) {
            if (combinedText.contains(keyword)) {
                matchCount++;
            }
        }

        if (matchCount >= 3) {
            confidence += 0.3;
        } else if (matchCount >= 1) {
            confidence += 0.15;
        }

        if (detectedType == JiraIssueType.EPIC && issue.getWordCount() > 200) {
            confidence += 0.2;
        }

        return Math.min(1.0, confidence);
    }

    private List<String> getKeywordsForType(JiraIssueType type) {
        return switch (type) {
            case BUG -> BUG_KEYWORDS;
            case STORY -> STORY_KEYWORDS;
            case TASK -> TASK_KEYWORDS;
            case EPIC -> EPIC_KEYWORDS;
        };
    }
}
