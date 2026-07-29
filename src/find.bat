@echo off
setlocal enabledelayedexpansion

for /r %%F in (*) do (
    for /f "delims=" %%L in ('findstr /c:"Lang.get" "%%F" 2^>nul') do (
        echo %%F: %%L
    )
)

pause