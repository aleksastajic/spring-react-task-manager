#!/usr/bin/env bash
set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"

mkdir -p "$LOG_DIR"

TS="$(date +"%Y%m%d-%H%M%S")"
LOG_FILE="$LOG_DIR/health-check-$TS.log"

# Run a command, tee output to the main log, and preserve exit code.
run() {
  local title="$1"; shift
  echo "" | tee -a "$LOG_FILE"
  echo "========== $title ==========" | tee -a "$LOG_FILE"
  echo "> $*" | tee -a "$LOG_FILE"

  # We want to continue even if a step fails, but remember status.
  set +e
  (cd "$ROOT_DIR" && "$@") 2>&1 | tee -a "$LOG_FILE"
  local status=${PIPESTATUS[0]}
  set -e

  echo "[exit=$status]" | tee -a "$LOG_FILE"
  return $status
}

usage() {
  cat <<EOF
Usage: $(basename "$0") [options]

Options:
  --git             Run git status/summary
  --frontend-lint   Run frontend lint (npm run lint)
  --frontend-build  Run frontend build (npm run build)
  --backend-test    Run backend tests (./mvnw test)
  --compose-config  Validate docker compose config (docker compose config)
  --all             Run all checks

Logs:
  - Writes a combined log to: $LOG_FILE
EOF
}

set -e

DO_GIT=0
DO_FE_LINT=0
DO_FE_BUILD=0
DO_BE_TEST=0
DO_COMPOSE=0

if [[ $# -eq 0 ]]; then
  usage
  exit 2
fi

while [[ $# -gt 0 ]]; do
  case "$1" in
    --git) DO_GIT=1 ;;
    --frontend-lint) DO_FE_LINT=1 ;;
    --frontend-build) DO_FE_BUILD=1 ;;
    --backend-test) DO_BE_TEST=1 ;;
    --compose-config) DO_COMPOSE=1 ;;
    --all)
      DO_GIT=1
      DO_FE_LINT=1
      DO_FE_BUILD=1
      DO_BE_TEST=1
      DO_COMPOSE=1
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage
      exit 2
      ;;
  esac
  shift
done

echo "Health check started: $(date -Is)" | tee "$LOG_FILE"
echo "Repo: $ROOT_DIR" | tee -a "$LOG_FILE"

overall=0

if [[ $DO_GIT -eq 1 ]]; then
  run "git status" git status --porcelain=v1 || overall=1
  run "git diff --stat" git diff --stat || overall=1
fi

if [[ $DO_COMPOSE -eq 1 ]]; then
  run "docker compose config" docker compose config || overall=1
fi

if [[ $DO_BE_TEST -eq 1 ]]; then
  run "backend tests" bash -lc 'cd backend && ./mvnw -q test' || overall=1
fi

if [[ $DO_FE_LINT -eq 1 ]]; then
  run "frontend lint" bash -lc 'cd frontend && npm run lint' || overall=1
fi

if [[ $DO_FE_BUILD -eq 1 ]]; then
  run "frontend build" bash -lc 'cd frontend && npm run build' || overall=1
fi

echo "" | tee -a "$LOG_FILE"
echo "Health check finished: $(date -Is) overall_exit=$overall" | tee -a "$LOG_FILE"

echo "$LOG_FILE"
exit $overall
