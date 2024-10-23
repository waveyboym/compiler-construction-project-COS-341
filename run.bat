@echo off

rem Create the bin directory if it doesn't exist
if not exist bin (
    mkdir bin
)

rem Compile Java source files recursively to the bin directory
javac -d bin -sourcepath src src\Interfaces\*.java src\Utils\*.java src\CodeGenBasic\*.java src\Lexer\*.java src\Parser\*.java src\ScopeAnalyzer\*.java src\TypeChecker\*.java src\App.java

rem Check if compilation was successful
if %errorlevel% == 0 (
    echo Compilation successful
    rem Copy input file to bin directory
    copy input\main.spl bin
    rem Go into bin directory
    cd bin
    rem check input file was copied successfuly
    if not exist main.spl (
        echo Input file not found
        exit /b
    )
    rem Run the program with the input file as an argument
    java App main.spl
    rem Remove .class files recursively after execution and delete the input file
    del /s /q *.class > nul 2>&1
    del main.spl > nul 2>&1
    rem Copy the output file in out/basic.bas to root
    copy out\basic.bas ..
    cd ..
) else (
    echo Compilation failed
)