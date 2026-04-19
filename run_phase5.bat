@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 5 (ORM Update Operation)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg5psp
pause
