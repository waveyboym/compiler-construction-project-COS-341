#!/bin/bash

# Go into src directory
cd src

# Compile Java source files recurively to ../bin directory
javac -d ../bin App.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation successful"
    # cd up to root directory
    cd ..
    # Ask user for input file from root directory
    echo "Enter input file path: "
    read input
    # Copy input file to bin directory
    cp $input bin
    # cd into bin directory
    cd bin
    # Run the Java program
    java App < $input
    # Remove .class files after execution
    del /s *.class
else
    echo "Compilation failed"
fi