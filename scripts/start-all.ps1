param(
  [string]$DbUser = "root",
  [string]$DbPassword = "root",
  [string]$DbUrl = "jdbc:mysql://127.0.0.1:3306/doda?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
  [string]$WslDistro = "",
  [string]$WslRscriptPath = "",
  [switch]$UseCondaRun
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$backendScript = Join-Path $PSScriptRoot "start-backend.ps1"
$frontendScript = Join-Path $PSScriptRoot "start-frontend.ps1"

Write-Host "[DODA] Launching backend + frontend in separate windows..." -ForegroundColor Cyan

$backendArgs = @(
  "-NoProfile",
  "-ExecutionPolicy", "Bypass",
  "-File", $backendScript,
  "-DbUser", $DbUser,
  "-DbPassword", $DbPassword,
  "-DbUrl", $DbUrl
)
if ($WslDistro -ne "") {
  $backendArgs += @("-WslDistro", $WslDistro)
}
if ($WslRscriptPath -ne "") {
  $backendArgs += @("-WslRscriptPath", $WslRscriptPath)
}
if ($UseCondaRun) {
  $backendArgs += @("-UseCondaRun")
}

Start-Process powershell -WorkingDirectory $repoRoot -ArgumentList $backendArgs | Out-Null

$frontendArgs = @(
  "-NoProfile",
  "-ExecutionPolicy", "Bypass",
  "-File", $frontendScript
)
Start-Process powershell -WorkingDirectory $repoRoot -ArgumentList $frontendArgs | Out-Null

Write-Host "[DODA] Started."
Write-Host "Frontend: http://localhost:8080/#/analysis"
Write-Host "Backend:  http://localhost:8081"
