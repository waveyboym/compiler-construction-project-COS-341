# Compiler Construction Project

## Table of contents

- [Compiler Construction Project](#compiler-construction-project)
  - [Table of contents](#table-of-contents)
  - [How do I run it?](#how-do-i-run-it)
  - [Run precompiled binary](#run-precompiled-binary)
      - [Run exe file](#run-exe-file)
  - [I am feeling brave and I don't trust you](#i-am-feeling-brave-and-i-dont-trust-you)
    - [Pre-requisites](#pre-requisites)
      - [Run jar file](#run-jar-file)
      - [Run scripts](#run-scripts)

## How do I run it?
You can run either the <a href="#run-exe-file">pre-compiled binary</a> or the <a href="#run-jar-file">jar file</a> or <a href="#run-scripts">compile and run the program yourself</a>

## Run precompiled binary

#### Run exe file
1. <a href="https://github.com/waveyboym/compiler-construction-project/releases">Download the executable</a> or navigate to <a href="build">build</a> folder and copy that executable from there. Take the one that aligns with your OS, eg ```compiler-windows.exe``` is for windows whilst ```compiler-unix``` is for unix based operating systems
2. Ensure you have you ```spl``` or input ```txt``` file ready
3. Run the executable in a terminal:
```
// windows
compiler-windows.exe input/main.spl

//unix
./compiler-unix input/main.spl
```
4. Use this website to execute the basic code <a href="https://www.jdoodle.com/execute-yabasic-online">basic compiler</a>
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
1. Copy the jar file from the <a href="build">build</a> folder
2. Open up a terminal and run:
```
java -jar compiler-1.0-SNAPSHOT.jar input/main.spl
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
2. Use this website to execute the basic code <a href="https://www.jdoodle.com/execute-yabasic-online">basic compiler</a>
> [!NOTE]  
> We made use of YABASIC and as such our syntax follows YABASIC dialect
> 
> [!NOTE]  
> You may have to grant run permissions to the bash or bat files

> [!WARNING]  
> It is always good practice to read and make sure you understand what the bat/bash file will do on your system before just running it.