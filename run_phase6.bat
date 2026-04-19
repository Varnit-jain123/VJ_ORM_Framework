@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 6 (ORM Delete Operation)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg6psp
pause
