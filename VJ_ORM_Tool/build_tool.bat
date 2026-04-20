@echo off
set "LIB_DIR=lib"
set "ITEXT_DIR=lib\itext7"
set "BIN_DIR=classes"

set "CP=%LIB_DIR%\gson-2.13.1.jar"
set "CP=%CP%;%ITEXT_DIR%\kernel-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\layout-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\io-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\Forms-7.1.11.jar"

echo Compiling VJORMTool...
javac -cp "%CP%" -d "%BIN_DIR%" src\com\vj\orm\tool\VJORMTool.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Tool compiled successfully.
pause
