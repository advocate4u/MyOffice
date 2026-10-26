param(
    [Parameter(Mandatory=$true)][string]$ApkPath,
    [string]$OutputDirectory = ".\rea-wps-results"
)
$ErrorActionPreference = "Stop"

$apk = (Resolve-Path -LiteralPath $ApkPath).Path
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
$out = (Resolve-Path -LiteralPath $OutputDirectory).Path

if (-not (Get-Command rea -ErrorAction SilentlyContinue)) {
    throw "REA CLI not found. Install/setup the official project first: https://github.com/morluto/rea"
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java is required. REA Android analysis needs a full JDK 17 or newer."
}
if (-not $env:REA_JADX_MCP_JAR -or -not (Test-Path -LiteralPath $env:REA_JADX_MCP_JAR)) {
    throw "Set REA_JADX_MCP_JAR to the official, SHA-256-verified jadx-headless-mcp-0.7.1-all.jar first. See docs/android-analysis.md in morluto/rea."
}

$hash = (Get-FileHash -Algorithm SHA256 -LiteralPath $apk).Hash.ToLowerInvariant()
$javaVersion = (java -version 2>&1 | Select-Object -First 1)
("APK=" + $apk + [Environment]::NewLine + "SHA256=" + $hash + [Environment]::NewLine + "JAVA=" + $javaVersion) |
    Set-Content -Encoding UTF8 -LiteralPath (Join-Path $out "target-info.txt")

function Invoke-ReaCapture([string]$Name, [string[]]$Arguments) {
    $destination = Join-Path $out ($Name + ".txt")
    ("Command: rea " + ($Arguments -join " ")) | Set-Content -Encoding UTF8 -LiteralPath $destination
    & rea @Arguments 2>&1 | Out-String -Width 300 | Add-Content -Encoding UTF8 -LiteralPath $destination
    if ($LASTEXITCODE -ne 0) {
        ("REA_EXIT_CODE=" + $LASTEXITCODE) | Add-Content -Encoding UTF8 -LiteralPath $destination
        Write-Warning "REA command '$Name' returned exit code $LASTEXITCODE; inspect $destination"
    }
}

Invoke-ReaCapture "01-package" @("inspect-android-package", $apk)
$queries = @(
    @{ name="writer"; query="cn.wps.moffice.writer.Writer" },
    @{ name="spreadsheet"; query="cn.wps.moffice.kfs.mfs.core.spreadsheet" },
    @{ name="presentation"; query="cn.wps.moffice.kfs.mfs.core.presentation" },
    @{ name="scanner"; query="cn.wps.moffice.main.scan" },
    @{ name="pdf"; query="cn.wps.moffice.pdf" },
    @{ name="notes"; query="cn.wps.moffice.note" }
)
foreach ($q in $queries) {
    Invoke-ReaCapture ("02-classes-" + $q.name) @("search-android-classes", $apk, $q.query)
}
Invoke-ReaCapture "03-writer-class" @("inspect-android-class", $apk, "cn.wps.moffice.writer.Writer")

Write-Host "REA outputs saved to: $out"
Write-Host "This is static analysis only; it does not prove the app runs or that the APK is authentic."
