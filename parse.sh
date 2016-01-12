#!/bin/bash
 
echo usage: ./parse.sh fichier.fsm   to display in png
echo usage: ./parse.sh fichier.fsm pdf  to display in pdf
echo ----------------------COMPUTING FILE NAMES----------------------
dotfile=`echo "${1/.fsm/.dot}"`
echo $dotfile
vhdfile=`echo "${1/.fsm/.vhd}"`
echo $vhdfile
pdffile=`echo "${1/.fsm/.pdf}"`
echo $pdffile
echo ----------------------DELETING OLD FILES----------------------
rm FsmProcess.class $dotfile $vhdfile
echo ----------------------PARSING THE GRAMMAR----------------------
#java -jar /usr/local/lib/antlr-4.5.1-complete.jar Fsm.g4
echo ----------------------COMPILING THE PARSER----------------------
javac  Fsm*.java FsmProcess.java
echo ----------------------RUNNING THE PARSER----------------------
#java org.antlr.v4.gui.TestRig Fsm file -gui
java FsmProcess $1
echo ----------------------DISPLAY THE DOT CODE----------------------
cat $dotfile
if [ -z "$2" ]
then
echo ----------------------DISPLAY THE DOT GRAPH IN PNG----------------------
 dot -Tpng $dotfile   | display  &
else
echo ----------------------DISPLAY THE DOT GRAPH IN PDF----------------------
dot -Tpdf $dotfile   -o  $pdffile
evince   $pdffile  &
fi
echo ----------------------DISPLAY THE VHDL FILE----------------------
cat $vhdfile

cp $vhdfile testghdl/src/

