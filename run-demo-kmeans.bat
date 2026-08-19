@echo off
setlocal
cd /d "%~dp0"

echo [FastML] Compiling and starting KMeansDemo...
call mvn clean compile exec:java -Dexec.mainClass="fastml.demo.KMeansDemo" -q
