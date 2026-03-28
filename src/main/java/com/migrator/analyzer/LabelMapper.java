package com.migrator.analyzer;

import com.migrator.model.github.GitHubLabel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class LabelMapper {

    private static final Map<Pattern, LabelMapping> LABEL_MAPPINGS = new HashMap<>();
    private static final Map<Pattern, String> AREA_MAPPINGS = new HashMap<>();
    private static final Map<Pattern, PriorityMapping> PRIORITY_MAPPINGS = new HashMap<>();
    private static final Map<Pattern, String> KIND_MAPPINGS = new HashMap<>();
    private static final Map<Pattern, String> ENV_MAPPINGS = new HashMap<>();
    private static final Map<Pattern, String> TRIAGE_MAPPINGS = new HashMap<>();

    static {
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^bug(-fix)?$"), 
            new LabelMapping("bug", "Bug Fixes"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^defect$"), 
            new LabelMapping("bug", "Bug Fixes"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^enhancement$"), 
            new LabelMapping("enhancement", "New Features"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^feature(-request)?$"), 
            new LabelMapping("enhancement", "New Features"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^documentation$"), 
            new LabelMapping("documentation", "Documentation"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^docs?$"), 
            new LabelMapping("documentation", "Documentation"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^security$"), 
            new LabelMapping("security", "Security"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^performance$"), 
            new LabelMapping("performance", "Performance"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^breaking[- ]?change$"), 
            new LabelMapping("breaking-change", "Breaking Changes"));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^good[- ]?first[- ]?issue$"), 
            new LabelMapping("good-first-issue", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^help[- ]?wanted$"), 
            new LabelMapping("help-wanted", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^wont[- ]?fix$"), 
            new LabelMapping("excluded", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^invalid$"), 
            new LabelMapping("excluded", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^duplicate$"), 
            new LabelMapping("excluded", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^question$"), 
            new LabelMapping("question", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^refactor(ing)?$"), 
            new LabelMapping("refactoring", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^test(ing)?$"), 
            new LabelMapping("testing", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^ci/cd$"), 
            new LabelMapping("ci-cd", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^dependencies?$"), 
            new LabelMapping("dependencies", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^tech[- ]?debt$"), 
            new LabelMapping("technical-debt", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^build$"), 
            new LabelMapping("build", null));
        LABEL_MAPPINGS.put(Pattern.compile("(?i)^regression$"), 
            new LabelMapping("regression", "Bug Fixes"));

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/core$"), "Core");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/arc$"), "Core");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jakarta$"), "Core");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/virtual[- ]?threads$"), "Core");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/security$"), "Security");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/oidc$"), "Security");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/keycloak$"), "Security");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/maven$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/gradle$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/cli$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/devtools$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jbang$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/codestarts$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/picocli$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/dependencies$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/platform$"), "Build & Tooling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/infra[- ]?automation$"), "Build & Tooling");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/devmode$"), "Developer Experience");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/dev[- ]?ui$"), "Developer Experience");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/devservices$"), "Developer Experience");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/continuous[- ]?testing$"), "Developer Experience");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/testing$"), "Developer Experience");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/config$"), "Configuration & Logging");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/logging$"), "Configuration & Logging");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/kubernetes$"), "Cloud & Infrastructure");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/container[- ]?image$"), "Cloud & Infrastructure");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/infrastructure$"), "Cloud & Infrastructure");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/devops$"), "Cloud & Infrastructure");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/native[- ]?image$"), "Native & Performance");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/performance$"), "Native & Performance");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/hibernate[- ]?orm$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/hibernate[- ]?search$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/hibernate[- ]?validator$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/hibernate[- ]?reactive$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/panache$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jdbc$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/agroal$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/flyway$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/liquibase$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/database$"), "Persistence");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/artemis$"), "Persistence");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/mongodb$"), "NoSQL");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/redis$"), "NoSQL");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/neo4j$"), "NoSQL");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/elasticsearch$"), "NoSQL");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/infinispan$"), "NoSQL");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/cache$"), "NoSQL");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/kafka$"), "Messaging");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/kafka[- ]?streams$"), "Messaging");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/reactive[- ]?messaging$"), "Messaging");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/stork$"), "Messaging");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/rest(-client)?$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/resteasy[- ]?classic$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/graphql$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/openapi$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/swagger[- ]?ui$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/smallrye$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/api$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/web[- ]?sockets?$"), "REST & APIs");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/undertow$"), "REST & APIs");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/vertx$"), "Reactive");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/netty$"), "Reactive");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/mutiny$"), "Reactive");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/reactive[- ]?streams[- ]?operators$"), "Reactive");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/reactive[- ]?sql[- ]?clients$"), "Reactive");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/context[- ]?propagation$"), "Reactive");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/tracing$"), "Observability");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jaeger$"), "Observability");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/metrics$"), "Observability");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/health$"), "Observability");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/fault[- ]?tolerance$"), "Observability");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/narayana$"), "Transactions");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/lra$"), "Transactions");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/amazon[- ]?lambda$"), "Functions");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/google[- ]?cloud[- ]?functions$"), "Functions");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/funqy$"), "Functions");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/scheduler$"), "Scheduling");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/qute$"), "Templating");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/grpc$"), "gRPC");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/kogito$"), "Business Automation");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/optaplanner$"), "Business Automation");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jackson$"), "Serialization");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/jaxb$"), "Serialization");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/json$"), "Serialization");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/xml$"), "Serialization");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/kotlin$"), "Languages");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/scala$"), "Languages");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/mailer$"), "Integration");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/vault$"), "Integration");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/frontend$"), "UI/UX");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/backend$"), "UI/UX");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/ui$"), "UI/UX");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/ux$"), "UI/UX");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/mobile$"), "UI/UX");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/documentation$"), "Documentation");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/docstyle$"), "Documentation");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/adr$"), "Documentation");

        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/awt$"), "Other");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/graphics$"), "Other");
        AREA_MAPPINGS.put(Pattern.compile("(?i)^area/securepipeline$"), "Other");

        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/extension[- ]?proposal$"), "Extension Proposal");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/bug$"), "Bug");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/enhancement$"), "Enhancement");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/feature$"), "Feature");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/question$"), "Question");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/documentation$"), "Documentation");
        KIND_MAPPINGS.put(Pattern.compile("(?i)^kind/epic$"), "Epic");

        ENV_MAPPINGS.put(Pattern.compile("(?i)^env/windows$"), "Windows");
        ENV_MAPPINGS.put(Pattern.compile("(?i)^env/linux$"), "Linux");
        ENV_MAPPINGS.put(Pattern.compile("(?i)^env/macos$"), "macOS");
        ENV_MAPPINGS.put(Pattern.compile("(?i)^env/m1$"), "Apple M1");

        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/needs[,[- ]]?investigation$"), "Needs Investigation");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/needs[,[- ]]?feedback$"), "Needs Feedback");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/approved$"), "Approved");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/awaiting[,[- ]]?pr$"), "Awaiting PR");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/downstream$"), "Downstream");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/stale$"), "Stale");
        TRIAGE_MAPPINGS.put(Pattern.compile("(?i)^triage/quarkus[- ]?[0-9]+$"), "Quarkus Version");

        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^priority/(?:high|highest|critical|urgent)$"), 
            new PriorityMapping("priority-critical", com.migrator.model.analysis.Priority.HIGH));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p0$"), 
            new PriorityMapping("priority-critical", com.migrator.model.analysis.Priority.HIGH));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p1$"), 
            new PriorityMapping("priority-high", com.migrator.model.analysis.Priority.HIGH));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^priority/(?:medium|normal)$"), 
            new PriorityMapping("priority-medium", com.migrator.model.analysis.Priority.MEDIUM));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p2$"), 
            new PriorityMapping("priority-medium", com.migrator.model.analysis.Priority.MEDIUM));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p3$"), 
            new PriorityMapping("priority-medium", com.migrator.model.analysis.Priority.MEDIUM));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^priority/(?:low|lowest)$"), 
            new PriorityMapping("priority-low", com.migrator.model.analysis.Priority.LOW));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p4$"), 
            new PriorityMapping("priority-low", com.migrator.model.analysis.Priority.LOW));
        PRIORITY_MAPPINGS.put(Pattern.compile("(?i)^p5$"), 
            new PriorityMapping("priority-low", com.migrator.model.analysis.Priority.LOW));
    }

    public LabelMappingResult mapLabels(List<GitHubLabel> labels) {
        LabelMappingResult result = new LabelMappingResult();

        if (labels == null || labels.isEmpty()) {
            return result;
        }

        for (GitHubLabel label : labels) {
            String labelName = label.getName();
            
            boolean mapped = false;

            for (Map.Entry<Pattern, LabelMapping> entry : LABEL_MAPPINGS.entrySet()) {
                System.out.println(" => " + entry.getKey() + " - " + labelName);
                if (entry.getKey().matcher(labelName).matches()) {
                    LabelMapping mapping = entry.getValue();
                    result.addLabel(mapping.jiraLabel);
                    if (mapping.component != null) {
                        result.addComponent(mapping.component);
                    }
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                for (Map.Entry<Pattern, String> entry : AREA_MAPPINGS.entrySet()) {
                    if (entry.getKey().matcher(labelName).matches()) {
                        result.addComponent(entry.getValue());
                        mapped = true;
                        break;
                    }
                }
            }

            if (!mapped) {
                for (Map.Entry<Pattern, String> entry : KIND_MAPPINGS.entrySet()) {
                    if (entry.getKey().matcher(labelName).matches()) {
                        result.addLabel(entry.getValue().toLowerCase().replace(" ", "-"));
                        mapped = true;
                        break;
                    }
                }
            }

            if (!mapped) {
                for (Map.Entry<Pattern, String> entry : ENV_MAPPINGS.entrySet()) {
                    if (entry.getKey().matcher(labelName).matches()) {
                        result.addLabel("env:" + entry.getValue().toLowerCase());
                        mapped = true;
                        break;
                    }
                }
            }

            if (!mapped) {
                for (Map.Entry<Pattern, String> entry : TRIAGE_MAPPINGS.entrySet()) {
                    if (entry.getKey().matcher(labelName).matches()) {
                        result.addLabel(entry.getValue().toLowerCase().replace(" ", "-"));
                        mapped = true;
                        break;
                    }
                }
            }

            if (!mapped) {
                for (Map.Entry<Pattern, PriorityMapping> entry : PRIORITY_MAPPINGS.entrySet()) {
                    if (entry.getKey().matcher(labelName).matches()) {
                        PriorityMapping pm = entry.getValue();
                        result.addLabel(pm.jiraLabel);
                        if (pm.priority != null) {
                            result.setDetectedPriority(pm.priority);
                        }
                        mapped = true;
                        break;
                    }
                }
            }

            if (!mapped && !labelName.startsWith("area/") && 
                !labelName.startsWith("kind/") && 
                !labelName.startsWith("env/") && 
                !labelName.startsWith("triage/") && 
                !labelName.startsWith("priority/") &&
                !labelName.startsWith("area:") && 
                !labelName.startsWith("kind:") && 
                !labelName.startsWith("triage:") && 
                !labelName.startsWith("priority:")) {
                result.addLabel(labelName.toLowerCase().replace(" ", "-"));
            }
        }

        return result;
    }

    public static class LabelMappingResult {
        private final List<String> labels = new ArrayList<>();
        private final List<String> components = new ArrayList<>();
        private com.migrator.model.analysis.Priority priority = com.migrator.model.analysis.Priority.MEDIUM;

        public void addLabel(String label) {
            if (label != null && !labels.contains(label)) {
                labels.add(label);
            }
        }

        public void addComponent(String component) {
            if (component != null && !components.contains(component)) {
                components.add(component);
            }
        }

        public List<String> getLabels() {
            return new ArrayList<>(labels);
        }

        public List<String> getComponents() {
            return new ArrayList<>(components);
        }

        public com.migrator.model.analysis.Priority getDetectedPriority() {
            return priority;
        }

        public void setDetectedPriority(com.migrator.model.analysis.Priority priority) {
            if (priority != null) {
                this.priority = priority;
            }
        }
    }

    private static class LabelMapping {
        final String jiraLabel;
        final String component;

        LabelMapping(String jiraLabel, String component) {
            this.jiraLabel = jiraLabel;
            this.component = component;
        }
    }

    private static class PriorityMapping {
        final String jiraLabel;
        final com.migrator.model.analysis.Priority priority;

        PriorityMapping(String jiraLabel, com.migrator.model.analysis.Priority priority) {
            this.jiraLabel = jiraLabel;
            this.priority = priority;
        }
    }
}
