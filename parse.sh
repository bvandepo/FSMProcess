#!/bin/bash
 
echo ----------------------PARSING THE GRAMMAR----------------------
#java -jar /usr/local/lib/antlr-4.5.1-complete.jar Fsm.g4
echo ----------------------COMPILING THE PARSER----------------------
javac  Fsm*.java FsmProcess.java
echo ----------------------RUNNING THE PARSER----------------------
#java org.antlr.v4.gui.TestRig Fsm file -gui
java FsmProcess fsm2.fsm > out.dot
echo ----------------------DISPLAY THE DOT CODE----------------------
cat out.dot
#echo ----------------------DISPLAY THE DOT GRAPH----------------------
#dot -Tpng out.dot   | display  
