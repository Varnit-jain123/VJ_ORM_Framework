@echo off
set "LIB_DIR=lib"
set "ITEXT_DIR=lib\itext7"
set "BIN_DIR=classes"

set "CP=%BIN_DIR%"
set "CP=%CP%;%LIB_DIR%\vj-orm-core.jar"
set "CP=%CP%;%LIB_DIR%\gson-2.13.1.jar"
set "CP=%CP%;%LIB_DIR%\mysql.jar"
set "CP=%CP%;%ITEXT_DIR%\kernel-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\layout-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\io-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\Forms-7.1.11.jar"
set "CP=%CP%;%ITEXT_DIR%\slf4j.api-1.6.1.jar"
set "CP=%CP%;%ITEXT_DIR%\log4j-1.2.16.jar"
set "CP=%CP%;%ITEXT_DIR%\slf4j-log4j12-1.6.1.jar"

echo Running VJORMTool with tool_config.json...
java -cp "%CP%" com.vj.orm.tool.VJORMTool tool_config.json
pause
