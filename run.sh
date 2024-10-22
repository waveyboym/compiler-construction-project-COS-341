#!/bin/bash

# if bin directory does not exist, create it
if [ ! -d "bin" ]; then
    mkdir bin
fi

# Compile Java source files recurively to ../bin directory
javac -d bin -sourcepath src src/Interfaces/*.java src/Utils/*.java src/CodeGenBasic/*.java src/Lexer/*.java src/Parser/*.java src/ScopeAnalyzer/*.java src/TypeChecker/*.java src/App.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful"
    # cd up to root directory
    # Ask user for input file from root directory
    echo "Enter input file path: "
    read input

    # check if input file exists
    if [ -f $input ]; then
        # Copy input file to bin directory
        cp $input bin
        # cd into bin directory
        cd bin
        # Run the Java program
        java App < $input
        # Remove .class files after execution
        del /s *.class
    else
        echo "File not found"
        exit 1
    fi
else
    echo "Compilation failed"
fi