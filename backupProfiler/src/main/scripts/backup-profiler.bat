@IF "%DEBUG%"=="" @ECHO OFF

SET JAVA=javaw.exe
IF "%1" == "--no-gui" SET JAVA=java.exe

IF "%JRE%" == "" SET JRE=%JRE_HOME%
IF "%JRE%" == "" SET JRE=%JDK_HOME%
IF "%JRE%" == "" SET JRE=%JAVA_HOME%

SET JAVA_EXE=%JRE%\bin\%JAVA%
IF EXIST "%JAVA_EXE%" goto start

%JAVA% -version 2> nul

IF NOT "%errorlevel%" == "0" goto error
SET JAVA_EXE=%JAVA%

:start

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

SET JAR=%DIRNAME%\lib\backup-profiler.jar
IF NOT EXIST "%JAR%" goto errorJar

SET JVM_ARGS=-Xms128m -Xmx750m -XX:MaxPermSize=350m -ea
start "Backup Profiler" "%JAVA_EXE%" %JVM_ARGS% -jar %JAR% %*

goto end

:error
echo ---------------------------------------------------------------------
echo ERROR: cannot start Backup Profiler.
echo No Java found. Please validate either JRE_HOME, JDK_HOME or JAVA_HOME environment variable points to valid JRE installation.
echo ---------------------------------------------------------------------
pause
goto end

:errorJar
echo ---------------------------------------------------------------------
echo ERROR: cannot start Backup Profiler.
echo No libs found. Please validate the distribution archive is fully downloaded and it's not corrupt.
echo ---------------------------------------------------------------------
pause
goto end

:end