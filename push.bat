@echo off
git pull origin master
For /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c-%%a-%%b)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a:%%b)
echo Commit Message:
set /p reason=
git commit -am "[%mydate% %mytime%] %reason%"
git push origin master
pause
