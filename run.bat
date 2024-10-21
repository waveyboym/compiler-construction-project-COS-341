@echo off

rem Go into src directory
cd src

rem Compile Java source files recurively to ../bin directory
javac -d ../bin App.java

rem Check if compilation was successful
if %errorlevel% == 0 (
    echo Compilation successful
    rem cd up to root directory
    cd ..
    rem Ask user for input file
    set /p input="Enter input file: "
    rem Copy input file to bin directory
    copy %input% bin
    rem Go into bin directory
    cd bin
    rem Run the program
    java App < %input%
    rem Remove .class files recursively after execution
    del /s *.class
) else (
    echo Compilation failed
)