@echo off
setlocal
cd /d "%~dp0"

echo [FastML] Compiling and starting GestureDemo...
call mvn clean compile exec:java -Dexec.mainClass="fastml.demo.GestureDemo" -q
