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
$backendDir = Join-Path $repoRoot "backend\doda"

Write-Host "[DODA] Starting backend in WSL R mode..." -ForegroundColor Cyan
Write-Host "[DODA] Backend dir: $backendDir"

# DB config
$env:SPRING_DATASOURCE_URL = $DbUrl
$env:SPRING_DATASOURCE_USERNAME = $DbUser
$env:SPRING_DATASOURCE_PASSWORD = $DbPassword

# R execution config (via WSL + conda env r_doda)
$env:DODA_EXEC_MODE = "wsl"
$env:DODA_CONDA_ENV = "r_doda"
$env:DODA_WSL_USE_CONDA_RUN = if ($UseCondaRun) { "true" } else { "false" }

if ([string]::IsNullOrWhiteSpace($WslRscriptPath)) {
  try {
    $linuxHome = (wsl -e bash -lc 'getent passwd $(id -un) | cut -d: -f6').Trim()
  }
  catch {
    $linuxHome = ""
  }

  if (-not $linuxHome.StartsWith("/")) {
    $linuxHome = "/home/$env:USERNAME"
  }
  $WslRscriptPath = "$linuxHome/miniconda3/envs/r_doda/bin/Rscript"
}

$checkCmd = "test -x '$WslRscriptPath' && echo OK || echo MISSING"
$checkResult = (wsl -e bash -lc $checkCmd).Trim()
if ($checkResult -ne "OK") {
  throw "WSL Rscript not found: $WslRscriptPath"
}

$env:DODA_RSCRIPT_CMD = $WslRscriptPath
if ($WslDistro -ne "") {
  $env:DODA_WSL_DISTRO = $WslDistro
}

Push-Location $backendDir
try {
  & .\mvnw.cmd spring-boot:run
}
finally {
  Pop-Location
}
