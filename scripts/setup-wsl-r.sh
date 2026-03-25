#!/usr/bin/env bash
set -euo pipefail

# One-time setup in WSL for DODA R environment.
# Usage:
#   wsl -e bash -lc "cd /mnt/e/Web1/Web && bash scripts/setup-wsl-r.sh"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend/doda"
ENGINE_DIR="$BACKEND_DIR/engine/doda"
ENV_NAME="r_doda"

if ! command -v conda >/dev/null 2>&1; then
  echo "[ERROR] conda not found in WSL."
  echo "Install Miniconda first, then re-run this script."
  exit 1
fi

if [ ! -f "$ENGINE_DIR/r_doda.yml" ]; then
  echo "[ERROR] Missing env file: $ENGINE_DIR/r_doda.yml"
  exit 1
fi

echo "[1/3] Creating/updating conda env: $ENV_NAME"
conda env update -n "$ENV_NAME" -f "$ENGINE_DIR/r_doda.yml" --prune

echo "[2/3] Verifying Rscript in env"
conda run -n "$ENV_NAME" Rscript --version

echo "[3/3] Setup done."
echo "Next: run PowerShell script scripts/start-backend.ps1 to start backend with WSL R mode."
