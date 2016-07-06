@echo off

set JAVA_HOME=%JAVA_HOME%
rem set JAVA_HOME=C:\Progra~1\Java\jre1.8.0_91

if exist "%JAVA_HOME%\bin\java.exe" goto okHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome

set APP_HOME=%~dp0..
set APP_LIB=%APP_HOME%\lib

set OPTS=%OPTS% -Xmx512M
set OPTS=%OPTS% -Dfile.encoding=windows-1251
set OPTS=%OPTS% -Dsun.io.useCanonCaches=false
set OPTS=%OPTS% -Dlog4j.configuration=file:%APP_HOME%\config\log4j.properties

set CLASSPATH=%APP_HOME%
set CLASSPATH=%CLASSPATH%;%APP_LIB%\log4j-1.2.17.jar

cd /D %APP_HOME%
rem "%JAVA_HOME%\bin\java" %OPTS% -classpath %CLASSPATH% ru.schegrov.TestApp
start "TestRemainder" /B "%JAVA_HOME%\bin\java" %OPTS% -cp %CLASSPATH% ru.schegrov.TestApp

:end