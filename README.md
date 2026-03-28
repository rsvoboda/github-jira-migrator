# GitHub to JIRA Migration Analyzer

A Quarkus-based application that analyzes GitHub issues to determine appropriate JIRA issue configurations (type, labels, components) for migration.

## Features

- **Web UI**: User-friendly interface to analyze GitHub issues
- **GitHub Issue Fetching**: Retrieve issues from any public GitHub repository
- **Intelligent Type Detection**: Automatically classifies issues as Bug, Story, Task, or Epic based on content analysis
- **Label Mapping**: Maps GitHub labels to JIRA labels and components using configurable rules
- **Priority Detection**: Extracts priority from labels (P1-P5, priority:high/medium/low)
- **Confidence Scoring**: Provides confidence scores for analysis recommendations
- **Batch Processing**: Analyze multiple issues from a repository at once
- **REST API**: Easy integration with CI/CD pipelines and automation tools

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- (Optional) GitHub Personal Access Token for higher API rate limits

## Quick Start

### 1. Clone and Build

```bash
cd github-jira-migrator
mvn clean package
```

### 2. Run in Dev Mode

```bash
mvn quarkus:dev
```

The application will start at `http://localhost:8080`

### 3. Web UI

Open your browser to `http://localhost:8080` to access the web interface where you can:
- Analyze single GitHub issues
- Batch analyze repository issues
- View detailed analysis results with suggested type, components, labels, and priority

### 4. Run the JAR

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `GITHUB_TOKEN` | GitHub Personal Access Token for API rate limits | (none) |

### application.yaml

```yaml
quarkus:
  http:
    port: 8080

github:
  api:
    base-url: https://api.github.com
    token: ${GITHUB_TOKEN:}

migration:
  analysis:
    default-type: TASK
    confidence-threshold: 0.5
```

## API Reference

### Analyze Single Issue

Analyze a single GitHub issue by repository and issue number.

**Endpoint:** `GET /api/v1/analyze/issue`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `owner` | string | Yes | Repository owner (username or organization) |
| `repo` | string | Yes | Repository name |
| `issueNumber` | integer | Yes | Issue number |
| `url` | string | Alternative | Full GitHub issue URL |

**Examples:**

```bash
# Using owner, repo, and issue number
curl "http://localhost:8080/api/v1/analyze/issue?owner=quarkusio&repo=quarkus&issueNumber=40000"

# Using full URL
curl "http://localhost:8080/api/v1/analyze/issue?url=https://github.com/quarkusio/quarkus/issues/40000"
```

**Response:**

```json
{
  "issueNumber": 40000,
  "issueTitle": "Bug in authentication flow",
  "issueUrl": "https://github.com/quarkusio/quarkus/issues/40000",
  "issueState": "open",
  "author": "username",
  "createdAt": "2024-01-15T10:30:00Z",
  "originalLabels": ["bug", "area:backend", "priority:high"],
  "suggestedType": "BUG",
  "suggestedLabels": ["bug", "priority-high"],
  "suggestedComponents": ["Bug Fixes", "Backend"],
  "suggestedPriority": "HIGH",
  "confidenceScore": 0.85,
  "reasoning": "Detected as Bug based on labels: bug; Mapped 3 GitHub labels to JIRA; Type-indicating label found - increased confidence",
  "analyzedSuccessfully": true
}
```

### Analyze Repository Issues

Analyze multiple issues from a repository with optional filters.

**Endpoint:** `POST /api/v1/analyze/repository`

**Request Body:**

```json
{
  "owner": "quarkusio",
  "repo": "quarkus",
  "state": "open",
  "labels": ["bug", "enhancement"],
  "limit": 50
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `owner` | string | Yes | Repository owner |
| `repo` | string | Yes | Repository name |
| `state` | string | No | Filter by state: `open`, `closed`, `all` (default: `open`) |
| `labels` | array | No | Filter by GitHub label names |
| `limit` | integer | No | Maximum issues to analyze (default: 100, max: 100) |

**Example:**

```bash
curl -X POST "http://localhost:8080/api/v1/analyze/repository" \
  -H "Content-Type: application/json" \
  -d '{
    "owner": "quarkusio",
    "repo": "quarkus",
    "state": "open",
    "limit": 10
  }'
```

**Response:**

```json
{
  "repository": "quarkusio/quarkus",
  "totalAnalyzed": 10,
  "successful": 9,
  "failed": 1,
  "results": [
    {
      "issueNumber": 40001,
      "suggestedType": "STORY",
      ...
    }
  ]
}
```

### Health Check

**Endpoint:** `GET /api/v1/analyze/health`

```bash
curl http://localhost:8080/api/v1/analyze/health
```

**Response:**

```json
{
  "status": "UP",
  "service": "GitHub-JIRA Migration Analyzer"
}
```

## Issue Type Detection

The analyzer uses keyword matching and pattern recognition to determine the appropriate JIRA issue type:

| Type | Detection Criteria |
|------|-------------------|
| **BUG** | Contains "bug", "defect", "crash", "broken", "error", "fix" in title/body |
| **STORY** | Contains "feature", "enhancement", "request", "as a user" |
| **TASK** | Contains "task", "todo", "cleanup", "update", "refactor" |
| **EPIC** | Contains "epic", large content (>500 words), multiple sections |

## Label Mapping

The following label mappings are applied, based on Quarkus ecosystem conventions.
Labels are mapped to JIRA **components** (high-level categories) and **labels**.

### Standard Labels

| GitHub Label | JIRA Label | JIRA Component |
|--------------|------------|----------------|
| `bug`, `bug-fix` | `bug` | Bug Fixes |
| `defect` | `bug` | Bug Fixes |
| `enhancement` | `enhancement` | New Features |
| `feature-request` | `enhancement` | New Features |
| `documentation`, `docs` | `documentation` | Documentation |
| `security` | `security` | Security |
| `performance` | `performance` | - |
| `breaking-change` | `breaking-change` | Breaking Changes |
| `good-first-issue` | `good-first-issue` | - |
| `help-wanted` | `help-wanted` | - |
| `wontfix`, `invalid`, `duplicate` | `excluded` | - |
| `refactoring` | `refactoring` | - |
| `dependencies` | `dependencies` | - |
| `tech-debt` | `technical-debt` | - |
| `regression` | `regression` | Bug Fixes |

### Aggregated Area Labels → JIRA Components

| GitHub Label(s) | JIRA Component |
|-----------------|----------------|
| `area:core`, `area:arc`, `area:jakarta`, `area:virtual-threads` | **Core** |
| `area:security`, `area:oidc`, `area:keycloak` | **Security** |
| `area:maven`, `area:gradle`, `area:cli`, `area:devtools`, `area:jbang`, `area:codestarts`, `area:picocli`, `area:dependencies`, `area:platform`, `area:infra-automation` | **Build & Tooling** |
| `area:devmode`, `area:dev-ui`, `area:devservices`, `area:continuous-testing`, `area:testing` | **Developer Experience** |
| `area:config`, `area:logging` | **Configuration & Logging** |
| `area:kubernetes`, `area:container-image`, `area:infrastructure`, `area:devops` | **Cloud & Infrastructure** |
| `area:native-image`, `area:performance` | **Native & Performance** |
| `area:hibernate-orm`, `area:hibernate-search`, `area:hibernate-validator`, `area:hibernate-reactive`, `area:panache`, `area:jdbc`, `area:agroal`, `area:flyway`, `area:liquibase`, `area:database`, `area:artemis` | **Persistence** |
| `area:mongodb`, `area:redis`, `area:neo4j`, `area:elasticsearch`, `area:infinispan`, `area:cache` | **NoSQL** |
| `area:rest`, `area:resteasy-classic`, `area:rest-client`, `area:graphql`, `area:openapi`, `area:swagger-ui`, `area:smallrye`, `area:api`, `area:web-sockets`, `area:undertow` | **REST & APIs** |
| `area:vertx`, `area:netty`, `area:mutiny`, `area:reactive-streams-operators`, `area:reactive-sql-clients`, `area:context-propagation` | **Reactive** |
| `area:kafka`, `area:kafka-streams`, `area:reactive-messaging`, `area:stork` | **Messaging** |
| `area:tracing`, `area:jaeger`, `area:metrics`, `area:health`, `area:fault-tolerance` | **Observability** |
| `area:narayana`, `area:lra` | **Transactions** |
| `area:amazon-lambda`, `area:google-cloud-functions`, `area:funqy` | **Functions** |
| `area:scheduler` | **Scheduling** |
| `area:qute` | **Templating** |
| `area:grpc` | **gRPC** |
| `area:jackson`, `area:jaxb`, `area:json`, `area:xml` | **Serialization** |
| `area:kotlin`, `area:scala` | **Languages** |
| `area:kogito`, `area:optaplanner` | **Business Automation** |
| `area:mailer`, `area:vault` | **Integration** |
| `area:frontend`, `area:backend`, `area:ui`, `area:ux`, `area:mobile` | **UI/UX** |
| `area:documentation`, `area:docstyle`, `area:adr` | **Documentation** |
| `area:awt`, `area:graphics`, `area:securepipeline` | **Other** |

### Kind Labels

| GitHub Label | JIRA Label |
|--------------|------------|
| `kind:extension-proposal` | extension-proposal |
| `kind:bug` | bug |
| `kind:enhancement` | enhancement |
| `kind:feature` | feature |
| `kind:question` | question |
| `kind:documentation` | documentation |
| `kind:epic` | epic |

### Environment Labels

| GitHub Label | JIRA Label |
|--------------|------------|
| `env:windows` | env:windows |
| `env:linux` | env:linux |
| `env/m1` | env:apple m1 |

### Priority Labels

| GitHub Label | JIRA Label | Priority |
|--------------|------------|----------|
| `priority:critical`, `priority:urgent`, `priority:highest`, `p0` | `priority-critical` | HIGH |
| `priority:high`, `p1` | `priority-high` | HIGH |
| `priority:medium`, `priority:normal`, `p2`, `p3` | `priority-medium` | MEDIUM |
| `priority:low`, `priority:lowest`, `p4`, `p5` | `priority-low` | LOW |

### Triage Labels

| GitHub Label | JIRA Label |
|--------------|------------|
| `triage:needs-investigation` | needs-investigation |
| `triage:needs-feedback` | needs-feedback |
| `triage:approved` | approved |
| `triage:awaiting-pr` | awaiting-pr |
| `triage:stale` | stale |

## Response Fields Explained

| Field | Description |
|-------|-------------|
| `suggestedType` | Recommended JIRA issue type (BUG, STORY, TASK, EPIC) |
| `suggestedLabels` | List of recommended JIRA labels |
| `suggestedComponents` | List of recommended JIRA components |
| `suggestedPriority` | Recommended priority (HIGH, MEDIUM, LOW) |
| `confidenceScore` | Analysis confidence (0.0 - 1.0). Values below 0.5 may need manual review |
| `reasoning` | Human-readable explanation of the recommendations |

## Rate Limits

- **Without GitHub Token**: 60 requests/hour per IP
- **With GitHub Token**: 5,000 requests/hour

For production use, it's recommended to set the `GITHUB_TOKEN` environment variable.

## Running Tests

```bash
mvn test
```

## Project Structure

```
github-jira-migrator/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/migrator/
│   │   │   ├── analyzer/       # Analysis logic
│   │   │   ├── client/         # GitHub API client
│   │   │   ├── model/          # Data models
│   │   │   ├── resource/       # REST endpoints
│   │   │   └── service/        # Business logic
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── META-INF/resources/
│   │           └── index.html  # Web UI
│   └── test/                   # Unit tests
└── SPEC.md                     # Detailed specification
```

## Future Enhancements

- [ ] JIRA issue creation (actual migration)
- [ ] Custom label mapping configuration via API
- [ ] Webhook support for automatic issue analysis
- [ ] Migration history and audit trail
- [ ] Batch export to JIRA CSV format
- [ ] GitHub Actions integration

## License

MIT License
