#!/usr/bin/env bash
# Run maven tests and tee output into backend/logs with timestamped filename
set -euo pipefail
mkdir -p "$(dirname "$0")/../logs"
cd "$(dirname "$0")/.."
TS=$(date +%Y%m%d-%H%M%S)
LOGFILE=logs/mvn-test-${TS}.log
echo "Running mvn test, logging to ${LOGFILE}"
./mvnw -B -e test 2>&1 | tee "${LOGFILE}"
exit ${PIPESTATUS[0]}
