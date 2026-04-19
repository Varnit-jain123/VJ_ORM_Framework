@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 4 (Student insertion with FK)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg4psp
pause
