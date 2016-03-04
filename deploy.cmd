@echo off
echo Deploying files...
echo %DEPLOYMENT_SOURCE%
echo %DEPLOYMENT_TARGET%
cmd "xcopy %DEPLOYMENT_SOURCE% %DEPLOYMENT_TARGET% /Y /E /D"