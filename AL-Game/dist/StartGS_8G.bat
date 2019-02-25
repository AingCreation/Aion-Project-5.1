@echo off
TITLE Aion 5.1 - Game Server Console
SET PATH="C:\Program Files\Java\jdk1.7.0_80\bin"
REM NOTE: Remove tag REM from previous line.
:START
CLS

echo.

echo Starting Battelefield Aion 5.1 Version 5.x Game Server.

echo.

REM -------------------------------------  
REM Default parameters for a basic server.
java -Xms90800m -Xmx9192m -XX:MaxHeapSize=9192m -Xdebug -XX:MaxNewSize=25m -XX:NewSize=25m -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC -XX:-UseSplitVerifier -ea -javaagent:./libs/al-commons-1.0.jar -cp ./libs/*;AL-Game.jar com.aionemu.gameserver.GameServer
REM -------------------------------------
SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
if ERRORLEVEL 0 goto end

REM Restart...
:restart
echo.
echo Administrator Restart ...
echo.
goto start

REM Error...
:error
echo.
echo Server terminated abnormaly ...
echo.
goto end

REM End...
:end
echo.
echo Server terminated ...
echo.
pause
