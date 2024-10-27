# compilers_cp1

By: Thomas Phan, Raeein Bagheri, Eason Liang

Compiler based off the C Minus Language

## Acknowledgement

The sample parser provided for the Tiny programming language provided by Professor Fei Song and 
was used as a starting point for this assignment

## Packages Required

Jflex: https://www.jflex.de

CUP: https://www2.cs.tum.edu/projects/cup/

Java: https://www.oracle.com/ca-en/java/technologies/downloads/


## Running the program 


To compile and run the program enter the make command

```bash
make
# in order to run the program
java -cp  java-cup-11b.jar:. CM testfiles/fac.cm -a

# To view Syntax Errors and output errors to file

java -cp   java-cup-11b.jar:.  CM testfiles/fac.cm -a > output.txt

# To View Jflex tokens generated

java -cp /cup_path Scanner:. Scanner  < your_input_file(e.g fac.tiny)
```

If your program is unable to compile you will need to configure the makefile and edit both the JFLEX variable and CLASSPATH variable. The JFLEX should point to the bin/jflex from the download jflex package while the CLASSPATH should be the path to the CUP jar file which is the one that doesn't include the runtime


# compilers_cp1

Notes:
- What is the difference between NOT and TILDE?
- Notable test cases
    char main int a);
    int main (int a;
    int p[;

testfiles/func-error.cm -a
Syntax error at character 0 of input
instead expected token classes are []
Couldn't repair and continue parse
java.lang.Exception: Can't recover from previous error(s)
        at java_cup.runtime.lr_parser.report_fatal_error(lr_parser.java:392)
        at java_cup.runtime.lr_parser.unrecovered_syntax_error(lr_parser.java:539)
        at java_cup.runtime.lr_parser.parse(lr_parser.java:731)
        at CM.main(CM.java:31)
(base) thomasphan@Thomass-MacBook-Air compilers_cp1