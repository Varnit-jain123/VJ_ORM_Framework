@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 1 (Metadata Extraction)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg1psp
pause
