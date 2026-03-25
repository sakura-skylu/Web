$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$frontendDir = Join-Path $repoRoot "frontend\doda"

Write-Host "[DODA] Starting frontend..." -ForegroundColor Cyan
Write-Host "[DODA] Frontend dir: $frontendDir"

Push-Location $frontendDir
try {
  if (-not (Test-Path "node_modules")) {
    Write-Host "[DODA] node_modules missing, installing deps..." -ForegroundColor Yellow
    cmd /c npm install
  }
  cmd /c npm run serve
}
finally {
  Pop-Location
}
