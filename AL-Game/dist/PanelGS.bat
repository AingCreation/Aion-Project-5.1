@ECHO off
TITLE AionUnity - Game Server Panel
:MENU
color f0
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    AionUnity - Game Server Panel                             ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                                       ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|    3 - Production X2                                                     ^|
ECHO   ^|    4 - Quit                                                              ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms14000m -Xmx14000m -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y,suspend=n -ea
CALL StartGS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms1536m -Xmx1536m -server
CALL StartGS.bat
)
IF %OPTION% == 3 (
SET MODE=PRODUCTION X2
SET JAVA_OPTS=-Xms9000m -Xmx9000m -server
CALL StartGS.bat
)
IF %OPTION% == 4 (
EXIT
)
IF %OPTION% GEQ 5 (
GOTO :MENU
)