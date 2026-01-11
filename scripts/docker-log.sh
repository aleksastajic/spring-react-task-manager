#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs/docker"
mkdir -p "${LOG_DIR}"

TS="$(date +"%Y%m%d-%H%M%S")"
LOG_FILE="${LOG_DIR}/docker-${TS}.log"

echo "Logging to: ${LOG_FILE}"

(
  echo "== docker-log =="
  echo "Timestamp: $(date -Iseconds)"
  echo "Workdir:   ${ROOT_DIR}"
  echo "Command:   docker compose $*"
  echo

  cd "${ROOT_DIR}"

  set +e
  docker compose "$@" 2>&1
  status=$?
  echo
  echo "EXIT:${status}"
  exit ${status}
) | tee "${LOG_FILE}"
