@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 10 (ORM In-Memory Caching)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg10psp
pause
