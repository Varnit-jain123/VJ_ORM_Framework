@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 3 (ORM Save Operation)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg3psp
pause
