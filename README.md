# Compiler Construction Project

## Table of contents

- [Compiler Construction Project](#compiler-construction-project)
  - [Table of contents](#table-of-contents)
  - [How do I run it?](#how-do-i-run-it)
  - [Run precompiled binary](#run-precompiled-binary)
      - [Run exe file on windows](#run-exe-file-on-windows)
  - [I am feeling brave and I don't trust you](#i-am-feeling-brave-and-i-dont-trust-you)
    - [Pre-requisites](#pre-requisites)
      - [Run jar file](#run-jar-file)
      - [Run scripts](#run-scripts)
  - [Additional info](#additional-info)

## How do I run it?
You can run either the <a href="#run-exe-file">pre-compiled binary</a> or the <a href="#run-jar-file">jar file</a> or <a href="#run-scripts">compile and run the program yourself</a>

## Run precompiled binary

#### Run exe file on windows
1. Navigate to <a href="build">build</a> and unzip the ```compiler-windows.zip``` file
2. Run:
```
cd compiler-windows
```
3. Copy the file you want to compile into that folder
4. Run the executable in a terminal:
```
compiler-windows.exe main.spl
```
5. Give it a bit of some time after execution has completed and you will see and ```out``` folder appear which you canexpand to see the ```basic.bas``` file.
6. Use this website to execute the basic code <a href="https://www.jdoodle.com/execute-yabasic-online">basic compiler</a>
> [!NOTE]  
> We made use of YABASIC and as such our syntax follows YABASIC dialect

## I am feeling brave and I don't trust you
> [!WARNING]  
> Proceed with this if you feel brave and you may want to deal with unexepected errors

### Pre-requisites
1. Download and install <a href="https://www.oracle.com/za/java/technologies/downloads/#jdk22-windows">Java development kit</a>
> [!IMPORTANT]  
> You cannot proceed past this point if you don't have java installed

#### Run jar file
1. Go to the jar file in the <a href="build">build</a> folder
2. Open up a terminal and run:
```
java -jar compiler-1.0-SNAPSHOT.jar main.spl
```
3. Use this website to execute the basic code <a href="https://www.jdoodle.com/execute-yabasic-online">basic compiler</a>
> [!NOTE]  
> We made use of YABASIC and as such our syntax follows YABASIC dialect

#### Run scripts
1. if you are on windows, open a terminal and run:
```
run.bat
```
otherwise if you are on linux, run:
```
./run.sh
```
2. Go into the <a href="bin/out">bin</a> folder and find the <a href="bin/out/basic.bas">basic.bas</a> file there
3. Use this website to execute the basic code <a href="https://www.jdoodle.com/execute-yabasic-online">basic compiler</a>
> [!NOTE]  
> We made use of YABASIC and as such our syntax follows YABASIC dialect
> 
> [!NOTE]  
> You may have to grant run permissions to the bash or bat files

> [!WARNING]  
> It is always good practice to read and make sure you understand what the bat/bash file will do on your system before just running it.

## Additional info

1. Create bin executable with jpackage
```
jpackage --input target/ --name compiler-windows --main-jar compiler-1.0-SNAPSHOT.jar  --type app-image
```