#!/bin/bash
 
echo usage: ./parse.sh fichier.fsm   to display in png
echo usage: ./parse.sh fichier.fsm pdf  to display in pdf
echo ----------------------PARSING THE GRAMMAR----------------------
#java -jar /usr/local/lib/antlr-4.5.1-complete.jar Fsm.g4
echo ----------------------COMPILING THE PARSER----------------------
javac  Fsm*.java FsmProcess.java
echo ----------------------RUNNING THE PARSER----------------------
#java org.antlr.v4.gui.TestRig Fsm file -gui
java FsmProcess $1 > out.dot
echo ----------------------DISPLAY THE DOT CODE----------------------
cat out.dot
if [ -z "$2" ]
then
echo ----------------------DISPLAY THE DOT GRAPH IN PNG----------------------
dot -Tpng out.dot   | display  
else
echo ----------------------DISPLAY THE DOT GRAPH IN PDF----------------------
dot -Tpdf out.dot  -o out.pdf
acroread  out.pdf ?
fi
