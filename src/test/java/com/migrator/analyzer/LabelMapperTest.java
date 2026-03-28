package com.migrator.analyzer;

import com.migrator.model.analysis.Priority;
import com.migrator.model.github.GitHubLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LabelMapperTest {

    private LabelMapper labelMapper;

    @BeforeEach
    void setUp() {
        labelMapper = new LabelMapper();
    }

    @Test
    void mapLabels_withBugLabel_shouldMapToBugFixes() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("bug")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("bug"));
        assertTrue(result.getComponents().contains("Bug Fixes"));
    }

    @Test
    void mapLabels_withEnhancementLabel_shouldMapToNewFeatures() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("enhancement")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("enhancement"));
        assertTrue(result.getComponents().contains("New Features"));
    }

    @Test
    void mapLabels_withDocumentationLabel_shouldMapToDocumentation() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("documentation")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("documentation"));
        assertTrue(result.getComponents().contains("Documentation"));
    }

    @Test
    void mapLabels_withSecurityLabel_shouldMapToSecurity() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("security")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("security"));
        assertTrue(result.getComponents().contains("Security"));
    }

    @Test
    void mapLabels_withPerformanceLabel_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("performance")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("performance"));
    }

    @Test
    void mapLabels_withGoodFirstIssueLabel_shouldMapWithoutComponent() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("good-first-issue")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("good-first-issue"));
        assertTrue(result.getComponents().isEmpty());
    }

    @Test
    void mapLabels_withWontFixLabel_shouldMapToExcluded() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("wontfix")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("excluded"));
    }

    @Test
    void mapLabels_withAreaCore_shouldMapToCoreComponent() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:core")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Core"));
    }

    @Test
    void mapLabels_withAreaKubernetes_shouldMapToCloudInfrastructure() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:kubernetes")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Cloud & Infrastructure"));
    }

    @Test
    void mapLabels_withAreaSecurity_shouldMapToSecurityComponent() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:security")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Security"));
    }

    @Test
    void mapLabels_withAreaHibernateORM_shouldMapToPersistence() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:hibernate-orm")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Persistence"));
    }

    @Test
    void mapLabels_withAreaRest_shouldMapToRestAndAPIs() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:rest")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("REST & APIs"));
    }

    @Test
    void mapLabels_withAreaGraphQL_shouldMapToRestAndAPIs() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:graphql")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("REST & APIs"));
    }

    @Test
    void mapLabels_withAreaKafka_shouldMapToMessaging() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:kafka")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Messaging"));
    }

    @Test
    void mapLabels_withAreaVertx_shouldMapToReactive() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:vertx")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Reactive"));
    }

    @Test
    void mapLabels_withAreaTracing_shouldMapToObservability() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:tracing")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Observability"));
    }

    @Test
    void mapLabels_withAreaHealth_shouldMapToObservability() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:health")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Observability"));
    }

    @Test
    void mapLabels_withAreaMetrics_shouldMapToObservability() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:metrics")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Observability"));
    }

    @Test
    void mapLabels_withAreaJaeger_shouldMapToObservability() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:jaeger")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Observability"));
    }

    @Test
    void mapLabels_withAreaMongoDB_shouldMapToNoSQL() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:mongodb")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("NoSQL"));
    }

    @Test
    void mapLabels_withAreaRedis_shouldMapToNoSQL() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:redis")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("NoSQL"));
    }

    @Test
    void mapLabels_withAreaElasticsearch_shouldMapToNoSQL() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:elasticsearch")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("NoSQL"));
    }

    @Test
    void mapLabels_withAreaMaven_shouldMapToBuildAndTooling() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:maven")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Build & Tooling"));
    }

    @Test
    void mapLabels_withAreaDevmode_shouldMapToDeveloperExperience() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:devmode")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Developer Experience"));
    }

    @Test
    void mapLabels_withAreaConfig_shouldMapToConfigurationAndLogging() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:config")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Configuration & Logging"));
    }

    @Test
    void mapLabels_withAreaNarayana_shouldMapToTransactions() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:narayana")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Transactions"));
    }

    @Test
    void mapLabels_withAreaAmazonLambda_shouldMapToFunctions() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:amazon-lambda")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Functions"));
    }

    @Test
    void mapLabels_withAreaNativeImage_shouldMapToNativeAndPerformance() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:native-image")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Native & Performance"));
    }

    @Test
    void mapLabels_withAreaKotlin_shouldMapToLanguages() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:kotlin")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Languages"));
    }

    @Test
    void mapLabels_withAreaGrpc_shouldMapToGrpc() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:grpc")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("gRPC"));
    }

    @Test
    void mapLabels_withAreaJackson_shouldMapToSerialization() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:jackson")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Serialization"));
    }

    @Test
    void mapLabels_withKindExtensionProposal_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("kind:extension-proposal")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("extension-proposal"));
    }

    @Test
    void mapLabels_withEnvWindows_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("env:windows")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("env:windows"));
    }

    @Test
    void mapLabels_withEnvM1_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("env/m1")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("env:apple m1"));
    }

    @Test
    void mapLabels_withTriageNeedingInvestigation_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("triage:needs-investigation")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("needs-investigation"));
    }

    @Test
    void mapLabels_withPriorityCritical_shouldSetHighestPriority() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("priority:critical")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertEquals(Priority.HIGH, result.getDetectedPriority());
        assertTrue(result.getLabels().contains("priority-critical"));
    }

    @Test
    void mapLabels_withAreaOIDC_shouldMapToSecurity() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:oidc")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Security"));
    }

    @Test
    void mapLabels_withMultipleLabels_shouldMapAllToAggregatedComponents() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("bug"),
                new GitHubLabel("area:core"),
                new GitHubLabel("area:hibernate-orm"),
                new GitHubLabel("area:oidc")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("bug"));
        assertTrue(result.getComponents().contains("Core"));
        assertTrue(result.getComponents().contains("Persistence"));
        assertTrue(result.getComponents().contains("Security"));
        assertTrue(result.getComponents().contains("Bug Fixes"));
    }

    @Test
    void mapLabels_withEmptyList_shouldReturnEmpty() {
        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(Collections.emptyList());

        assertTrue(result.getLabels().isEmpty());
        assertTrue(result.getComponents().isEmpty());
        assertEquals(Priority.MEDIUM, result.getDetectedPriority());
    }

    @Test
    void mapLabels_withNullList_shouldReturnEmpty() {
        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(null);

        assertTrue(result.getLabels().isEmpty());
        assertTrue(result.getComponents().isEmpty());
        assertEquals(Priority.MEDIUM, result.getDetectedPriority());
    }

    @Test
    void mapLabels_withBreakingChangeLabel_shouldMapCorrectly() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("breaking-change")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("breaking-change"));
        assertTrue(result.getComponents().contains("Breaking Changes"));
    }

    @Test
    void mapLabels_withRefactorLabel_shouldMapToRefactoring() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("refactoring")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("refactoring"));
    }

    @Test
    void mapLabels_withDuplicateLabel_shouldMapToExcluded() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("duplicate")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("excluded"));
    }

    @Test
    void mapLabels_withCustomLabel_shouldPreserveLabel() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("needs-review")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getLabels().contains("needs-review"));
    }

    @Test
    void mapLabels_withDuplicateMappedLabels_shouldNotDuplicate() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("bug"),
                new GitHubLabel("Bug")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertEquals(1, result.getLabels().stream()
                .filter(l -> l.equals("bug")).count());
    }

    @Test
    void mapLabels_withAreaFlyway_shouldMapToPersistence() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:flyway")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Persistence"));
    }

    @Test
    void mapLabels_withAreaMailer_shouldMapToIntegration() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:mailer")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Integration"));
    }

    @Test
    void mapLabels_withAreaVault_shouldMapToIntegration() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:vault")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Integration"));
    }

    @Test
    void mapLabels_withAreaScheduler_shouldMapToScheduling() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:scheduler")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Scheduling"));
    }

    @Test
    void mapLabels_withAreaQute_shouldMapToTemplating() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:qute")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Templating"));
    }

    @Test
    void mapLabels_withAreaKogito_shouldMapToBusinessAutomation() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:kogito")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Business Automation"));
    }

    @Test
    void mapLabels_withAreaUI_shouldMapToUIUX() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:ui")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("UI/UX"));
    }

    @Test
    void mapLabels_withAreaFaultTolerance_shouldMapToObservability() {
        List<GitHubLabel> labels = Arrays.asList(
                new GitHubLabel("area:fault-tolerance")
        );

        LabelMapper.LabelMappingResult result = labelMapper.mapLabels(labels);

        assertTrue(result.getComponents().contains("Observability"));
    }
}
