@echo off
set "backup_folder=C:\path\to\DB_Backups"

:: Change directory to backup folder
cd /d "%backup_folder%"

:: Initialize counter
set /a count=0

:: Loop through files ordered by date (newest first)
for /f "delims=" %%A in ('dir /b /o-d /a-d') do (
    set /a count+=1
    if !count! leq 5 (
        echo Keeping %%A
    ) else (
        del "%%A"
        echo Deleting %%A
    )
)
