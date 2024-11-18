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

# To run Semantic Analyzer
java -cp  java-cup-11b.jar:. CM testfiles/semantic/fac.cm -s

# To Run and generate Tm Code
java -cp  java-cup-11b.jar:. CM testfiles/semantic/fac.cm -c

# To run the generated Tm code
./tm testfiles/semantic/fac.tm
```

If your program is unable to compile you will need to configure the makefile and edit both the JFLEX variable and CLASSPATH variable. The JFLEX should point to the bin/jflex from the download jflex package while the CLASSPATH should be the path to the CUP jar file which is the one that doesn't include the runtime
