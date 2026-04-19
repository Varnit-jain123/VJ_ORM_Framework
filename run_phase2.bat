@echo off
set "LIB_DIR=lib"
set "BIN_DIR=classes"

echo Running Phase 2 (Entity Generation)...
java -cp "%BIN_DIR%;%LIB_DIR%/*" testcases.eg2psp
pause
