@echo off
echo Deploying files...
cd
cmd "xcopy %DEPLOYMENT_SOURCE% %DEPLOYMENT_TARGET% /Y /E /D"