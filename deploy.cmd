@echo off
echo Deploying files...
cd %DEPLOYMENT_SOURCE% 
cd
cd %DEPLOYMENT_TARGET%
cd
;xcopy %DEPLOYMENT_SOURCE% %DEPLOYMENT_TARGET% /Y /E /D