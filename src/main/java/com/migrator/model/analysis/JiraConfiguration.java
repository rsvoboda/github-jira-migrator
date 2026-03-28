package com.migrator.model.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JiraConfiguration {

    private JiraIssueType suggestedType;
    private Set<String> suggestedLabels;
    private Set<String> suggestedComponents;
    private Priority suggestedPriority;
    private double confidenceScore;
    private StringBuilder reasoning;

    public JiraConfiguration() {
        this.suggestedLabels = new HashSet<>();
        this.suggestedComponents = new HashSet<>();
        this.reasoning = new StringBuilder();
        this.confidenceScore = 0.0;
    }

    public JiraIssueType getSuggestedType() {
        return suggestedType;
    }

    public void setSuggestedType(JiraIssueType suggestedType) {
        this.suggestedType = suggestedType;
    }

    public Set<String> getSuggestedLabels() {
        return suggestedLabels;
    }

    public void addLabel(String label) {
        this.suggestedLabels.add(label);
    }

    public void addLabels(List<String> labels) {
        this.suggestedLabels.addAll(labels);
    }

    public Set<String> getSuggestedComponents() {
        return suggestedComponents;
    }

    public void addComponent(String component) {
        this.suggestedComponents.add(component);
    }

    public void addComponents(List<String> components) {
        this.suggestedComponents.addAll(components);
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
        this.confidenceScore = Math.max(0.0, Math.min(1.0, confidenceScore));
    }

    public void addConfidence(double delta) {
        this.confidenceScore = Math.max(0.0, Math.min(1.0, this.confidenceScore + delta));
    }

    public String getReasoning() {
        return reasoning.toString();
    }

    public void addReasoning(String reason) {
        if (reasoning.length() > 0) {
            reasoning.append("; ");
        }
        reasoning.append(reason);
    }

    public void appendReasoning(String reason) {
        if (reasoning.length() > 0) {
            reasoning.append("; ");
        }
        reasoning.append(reason);
    }

    public List<String> getSuggestedLabelsAsList() {
        return new ArrayList<>(suggestedLabels);
    }

    public List<String> getSuggestedComponentsAsList() {
        return new ArrayList<>(suggestedComponents);
    }
}
