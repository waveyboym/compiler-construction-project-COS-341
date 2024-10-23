#!/bin/bash

# Create the bin directory if it doesn't exist
if [ ! -d "bin" ]; then
    mkdir bin
fi

# Compile Java source files recursively to the bin directory
javac -d bin -sourcepath src src/Interfaces/*.java src/Utils/*.java src/CodeGenBasic/*.java src/Lexer/*.java src/Parser/*.java src/ScopeAnalyzer/*.java src/TypeChecker/*.java src/App.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful"
    
    # Copy input file (main.spl) to the bin directory
    if [ -f "input/main.spl" ]; then
        cp input/main.spl bin
    else
        echo "Input file not found"
        exit 1
    fi

    # Change to bin directory
    cd bin

    # Run the Java program with main.spl as input
    java App main.spl

    # Remove .class files recursively after execution
    find . -name "*.class" -type f -delete

    # Delete the input file (main.spl) after execution
    rm -f main.spl

    # Copy the output file to the root directory
    if [ -f "out/basic.bas" ]; then
        cp out/basic.bas ..
    fi

    # Go back to the root directory
    cd ..

else
    echo "Compilation failed"
fi
