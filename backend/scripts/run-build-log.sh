#!/usr/bin/env bash
# Run maven build and tee output into backend/logs with timestamped filename
set -euo pipefail
mkdir -p "$(dirname "$0")/../logs"
cd "$(dirname "$0")/.."
TS=$(date +%Y%m%d-%H%M%S)
LOGFILE=logs/mvn-${TS}.log
echo "Running mvn package, logging to ${LOGFILE}"
./mvnw -DskipTests -B -e package 2>&1 | tee "${LOGFILE}"
exit ${PIPESTATUS[0]}
