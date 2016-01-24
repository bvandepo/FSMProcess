#!/bin/bash
cd ../src/
echo ----------------------REMOVING OLD GRAMMAR FILES----------------------
rm *.class 
rm *.tokens
rm FsmLexer.java

rm FsmParserListener.java
rm FsmParserBaseListener.java

rm FsmParser.java

echo ----------------------PARSING THE GRAMMAR----------------------

echo generate FsmLexer.tokens and FsmLexer.java
java -jar /usr/local/lib/antlr-4.5.1-complete.jar FsmLexer.g4
echo generate FsmParserBaseListener.java and FsmParserListener.java and  FsmParser.java
java -jar /usr/local/lib/antlr-4.5.1-complete.jar FsmParser.g4

echo ----------------------COMPILING THE PARSER----------------------
javac  FsmLexer.java
javac  FsmParserListener.java
javac  FsmParserBaseListener.java 
javac  FsmParser.java 

echo ----------------------RUNNING THE PARSER----------------------
echo WARNING: the name to use is neither FsmParser of FsmLexer BUT: Fsm

java org.antlr.v4.gui.TestRig Fsm fsmfile -gui 

cd ../srcipts/
#grun FsmParser fsmfile
