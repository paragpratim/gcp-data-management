#!/bin/zsh
# JaCoCo CLI merge and report script

JACOCO_CLI_JAR="jacoco-merged-report/jacoco-cli.jar"
JACOCO_CLI_URL="https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.11/org.jacoco.cli-0.8.11-nodeps.jar"
MERGED_EXEC="jacoco-merged-report/merged.exec"
REPORT_DIR="jacoco-merged-report/html"

# Ensure output directory exists
mkdir -p jacoco-merged-report/html

# Download JaCoCo CLI if not present
if [ ! -f "$JACOCO_CLI_JAR" ]; then
    echo "Downloading JaCoCo CLI..."
    curl -L -o "$JACOCO_CLI_JAR" "$JACOCO_CLI_URL"
fi

# Merge exec files
java -jar "$JACOCO_CLI_JAR" merge \
  services/target/jacoco.exec \
  common-model/target/jacoco.exec \
  --destfile "$MERGED_EXEC"

# Generate HTML report
java -jar "$JACOCO_CLI_JAR" report "$MERGED_EXEC" \
  --classfiles services/target/classes \
  --classfiles common-model/target/classes \
  --sourcefiles services/src/main/java \
  --sourcefiles common-model/src/main/java \
  --html "$REPORT_DIR"

echo "Unified JaCoCo report generated at $REPORT_DIR/index.html"