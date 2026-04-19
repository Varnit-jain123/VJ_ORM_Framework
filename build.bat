@echo off
set "LIB_DIR=lib"
set "SRC_DIR=src"
set "BIN_DIR=classes"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo Compiling...
javac -d "%BIN_DIR%" -cp "%LIB_DIR%/*" %SRC_DIR%/com/vj/orm/annotation/*.java %SRC_DIR%/com/vj/orm/config/*.java %SRC_DIR%/com/vj/orm/core/*.java %SRC_DIR%/com/vj/orm/exception/*.java testcases/*.java DTO_files/*.java

if %ERRORLEVEL% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed.
)
pause
