JAVA=java
JAVAC=javac
JFLEX=/Users/eason/jflex-1.9.1/bin/jflex
CLASSPATH=-cp /Users/eason/java-cup-bin-11b-20160615/java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main
CC = gcc

all: CM.class tm

CM.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java SemanticAnalyzer.java CodeGenerator.java Scanner.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cm.flex
	$(JFLEX) cm.flex

parser.java: cm.cup
	#$(CUP) -dump -expect 3 cm.cup
	$(CUP) -expect 3 cm.cup

tm: tm.c
	$(CC) $(CFLAGS) tm.c -o tm

clean:
	rm -f tm parser.java Lexer.java sym.java *.class absyn/*.class *~
