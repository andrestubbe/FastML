@echo off
setlocal
cd /d "%~dp0"

echo [FastML] Compiling and starting HandwritingDemo...
call mvn clean compile exec:java -Dexec.mainClass="fastml.demo.HandwritingDemo" -q
