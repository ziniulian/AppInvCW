@echo off
xcopy L:\Doc\SVN\Work\CW\trunk\src\CWtHd\app\src\main L:\Doc\Git\AppInvCW\app\src\main\ /S
xcopy L:\Doc\SVN\Work\CW\trunk\src\CWtHd\app\libs L:\Doc\Git\AppInvCW\app\libs\ /S
copy L:\Doc\SVN\Work\CW\trunk\src\CWtHd\app\build.gradle L:\Doc\Git\AppInvCW\app
pause
