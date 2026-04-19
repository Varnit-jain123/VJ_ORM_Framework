@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 9 (ORM Database Views)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg9psp
pause
