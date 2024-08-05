@ECHO off

echo AA server autorestarter working...

:startserver

start /w server.exe

echo AA server turned off, restarting...

timeout 1 >nul

goto startserver