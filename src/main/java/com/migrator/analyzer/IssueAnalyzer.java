package com.migrator.analyzer;

import com.migrator.model.analysis.AnalysisResult;
import com.migrator.model.analysis.JiraConfiguration;
import com.migrator.model.analysis.JiraIssueType;
import com.migrator.model.analysis.Priority;
import com.migrator.model.github.GitHubIssue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class IssueAnalyzer {

    private static final Set<String> EXCLUDED_LABELS = Set.of("excluded", "wontfix", "invalid", "duplicate");

    private final TypeDetector typeDetector;
    private final LabelMapper labelMapper;
    private final String defaultType;
    private final double confidenceThreshold;

    public IssueAnalyzer() {
        this(new TypeDetector(), new LabelMapper(), "TASK", 0.5);
    }

    public IssueAnalyzer(TypeDetector typeDetector, LabelMapper labelMapper, 
                         String defaultType, double confidenceThreshold) {
        this.typeDetector = typeDetector;
        this.labelMapper = labelMapper;
        this.defaultType = defaultType;
        this.confidenceThreshold = confidenceThreshold;
    }

    public AnalysisResult analyze(GitHubIssue issue) {
        AnalysisResult result = AnalysisResult.fromIssue(issue);

        try {
            JiraConfiguration config = analyzeIssue(issue);
            result.applyConfiguration(config);

            if (config.getConfidenceScore() < confidenceThreshold) {
                result.setReasoning(result.getReasoning() + 
                    " (Confidence below threshold - consider manual review)");
            }

        } catch (Exception e) {
            result.setAnalyzedSuccessfully(false);
            result.setErrorMessage("Analysis failed: " + e.getMessage());
        }

        return result;
    }

    public JiraConfiguration analyzeIssue(GitHubIssue issue) {
        JiraConfiguration config = new JiraConfiguration();

        boolean hasExcludedLabel = issue.getLabels() != null && 
            issue.getLabels().stream()
                .anyMatch(l -> EXCLUDED_LABELS.contains(l.getName().toLowerCase()));

        if (hasExcludedLabel) {
            config.setSuggestedType(JiraIssueType.TASK);
            config.addLabel("excluded");
            config.setSuggestedPriority(Priority.LOW);
            config.setConfidenceScore(1.0);
            config.appendReasoning("Issue has exclusion label - marked for exclusion");
            return config;
        }

        JiraIssueType detectedType = typeDetector.detectType(issue);
        config.setSuggestedType(detectedType);
        double typeConfidence = typeDetector.calculateTypeConfidence(detectedType, issue);
        config.setConfidenceScore(typeConfidence);

        if (issue.getLabels() != null && !issue.getLabels().isEmpty()) {
            LabelMapper.LabelMappingResult labelResult = labelMapper.mapLabels(issue.getLabels());
            config.addLabels(labelResult.getLabels());
            config.addComponents(labelResult.getComponents());
            
            if (labelResult.getDetectedPriority() != null) {
                config.setSuggestedPriority(labelResult.getDetectedPriority());
            }

            if (!labelResult.getLabels().isEmpty()) {
                config.appendReasoning("Mapped " + labelResult.getLabels().size() + " GitHub labels to JIRA");
            }
            if (!labelResult.getComponents().isEmpty()) {
                config.appendReasoning("Mapped " + labelResult.getComponents().size() + " labels to components");
            }
        }

        applyDefaultPriorityIfNeeded(config, issue);

        buildReasoning(config, issue, detectedType);

        adjustConfidenceBasedOnLabels(config, issue);

        return config;
    }

    private void applyDefaultPriorityIfNeeded(JiraConfiguration config, GitHubIssue issue) {
        if (config.getSuggestedPriority() == null) {
            if (issue.getLabels() != null) {
                for (var label : issue.getLabels()) {
                    String name = label.getName().toLowerCase();
                    if (name.contains("urgent") || name.contains("critical")) {
                        config.setSuggestedPriority(Priority.HIGH);
                        config.addLabel("priority-high");
                        return;
                    }
                }
            }
            config.setSuggestedPriority(Priority.MEDIUM);
        }
    }

    private void buildReasoning(JiraConfiguration config, GitHubIssue issue, JiraIssueType type) {
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Detected as ").append(type.getDisplayName());

        if (issue.getLabels() != null && !issue.getLabels().isEmpty()) {
            List<String> labelNames = issue.getLabels().stream()
                    .map(l -> l.getName())
                    .toList();
            reasoning.append(" based on labels: ").append(String.join(", ", labelNames));
        } else {
            reasoning.append(" based on content analysis");
        }

        if (issue.getWordCount() > 200) {
            reasoning.append(". Issue has ").append(issue.getWordCount())
                    .append(" words - considered for Epic classification");
        }

        config.appendReasoning(reasoning.toString());
    }

    private void adjustConfidenceBasedOnLabels(JiraConfiguration config, GitHubIssue issue) {
        if (issue.getLabels() == null || issue.getLabels().isEmpty()) {
            config.addConfidence(-0.1);
            config.appendReasoning("No labels present - reduced confidence");
        }

        boolean hasTypeIndicatingLabel = issue.getLabels() != null && 
            issue.getLabels().stream()
                .anyMatch(l -> {
                    String name = l.getName().toLowerCase();
                    return name.contains("bug") || name.contains("feature") || 
                           name.contains("enhancement") || name.contains("task");
                });

        if (hasTypeIndicatingLabel) {
            config.addConfidence(0.15);
            config.appendReasoning("Type-indicating label found - increased confidence");
        }
    }
}
