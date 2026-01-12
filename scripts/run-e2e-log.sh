#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"

mkdir -p "$LOG_DIR"

TS="$(date +"%Y%m%d-%H%M%S")"
LOGFILE="$LOG_DIR/e2e-$TS.log"
LATEST="$LOG_DIR/e2e-latest.log"

echo "Running Playwright smoke, logging to $LOGFILE"
(
  cd "$ROOT_DIR/frontend"
  npm run e2e
) 2>&1 | tee "$LOGFILE"

cp -f "$LOGFILE" "$LATEST"
exit ${PIPESTATUS[0]}
