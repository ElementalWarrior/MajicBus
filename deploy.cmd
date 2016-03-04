@echo off
echo Deploying files...
cd
xcopy %DEPLOYMENT_SOURCE% %DEPLOYMENT_TARGET% /Y /E /D