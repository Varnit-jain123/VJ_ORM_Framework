@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 7 (ORM Select Operation)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg7psp
pause
