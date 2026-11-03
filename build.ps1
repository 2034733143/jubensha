$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$tomcat = Join-Path (Split-Path $root -Parent) 'tools\apache-tomcat-9.0.123'
$servletApi = Join-Path $tomcat 'lib\servlet-api.jar'
$build = Join-Path $root 'build'
$classes = Join-Path $build 'classes'
$warDir = Join-Path $build 'war'
$warFile = Join-Path $root 'dist\jubensha.war'

Write-Host 'Clean build dir...'
Remove-Item -Recurse -Force $build -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $classes, $warDir, (Join-Path $root 'dist') | Out-Null

Write-Host 'Compile java sources...'
$sources = Get-ChildItem -Path (Join-Path $root 'src\main\java') -Filter *.java -Recurse | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 --release 8 -Xlint:-options -cp $servletApi -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'compile failed' }

Write-Host 'Copy resources and webapp...'
Copy-Item (Join-Path $root 'src\main\resources\*') -Destination $classes -Recurse -Force
$webappDir = Join-Path $root 'src\main\webapp'
Copy-Item (Join-Path $webappDir '*') -Destination $warDir -Recurse -Force
New-Item -ItemType Directory -Force -Path (Join-Path $warDir 'WEB-INF\classes'), (Join-Path $warDir 'WEB-INF\lib') | Out-Null
Copy-Item (Join-Path $classes '*') -Destination (Join-Path $warDir 'WEB-INF\classes') -Recurse -Force
Copy-Item (Join-Path $root 'lib\*.jar') -Destination (Join-Path $warDir 'WEB-INF\lib') -Force

Write-Host 'Package war...'
Remove-Item -Force $warFile -ErrorAction SilentlyContinue
& jar --create --file $warFile -C $warDir .
if ($LASTEXITCODE -ne 0) { throw 'package failed' }

$size = [math]::Round((Get-Item $warFile).Length / 1MB, 2)
Write-Host "BUILD OK: $warFile ($size MB)"
