@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 8 (ORM Filtered Queries)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg8psp
pause
