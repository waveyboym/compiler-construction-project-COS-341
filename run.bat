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
    rem Ask user for input file
    set /p input="Enter input file: "

    rem Check if the input file exists
    if exist "%input%" (
        rem Copy input file to bin directory
        copy "%input%" bin
        rem Go into bin directory
        cd bin
        rem Run the program with the input file as an argument
        java App < "%input%"
        
        rem Remove .class files recursively after execution
        del /s /q *.class
        cd ..
    ) else (
        echo File "%input%" does not exist.
    )
) else (
    echo Compilation failed
)