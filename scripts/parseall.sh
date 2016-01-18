#!/bin/bash
 
for filename in *.fsm
do
echo ---------------------------------------------------------------
echo ---------------------------------------------------------------
echo Processing $filename
echo ---------------------------------------------------------------
echo ----------------------COMPUTING FILE NAMES----------------------
dotfile=`echo "${filename/.fsm/.dot}"`
echo $dotfile
vhdfile=`echo "${filename/.fsm/.vhd}"`
echo $vhdfile
pdffile=`echo "${filename/.fsm/.pdf}"`
echo $pdffile
pngfile=`echo "${filename/.fsm/.png}"`
echo $pngfile
echo ----------------------DELETING OLD FILES----------------------
#rm FsmProcess.class
rm $dotfile $vhdfile
echo ----------------------PARSING THE GRAMMAR----------------------
#java -jar /usr/local/lib/antlr-4.5.1-complete.jar Fsm.g4
echo ----------------------COMPILING THE PARSER----------------------
#javac  Fsm*.java FsmProcess.java
echo ----------------------RUNNING THE PARSER----------------------
#java org.antlr.v4.gui.TestRig Fsm file -gui
java FsmProcess $filename
echo ----------------------DISPLAY THE DOT CODE----------------------
cat $dotfile
if [ -z "$2" ]
then
echo ----------------------DISPLAY THE DOT GRAPH IN PNG----------------------
 #dot -Tpng $dotfile    | display  &
dot -Tpng $dotfile -o $pngfile
#display $pngfile &
#geeqie is displaying the image as it changes
else
echo ----------------------DISPLAY THE DOT GRAPH IN PDF----------------------
dot -Tpdf $dotfile   -o  $pdffile
evince   $pdffile  &
fi
echo ----------------------DISPLAY THE VHDL FILE----------------------
cat $vhdfile

cp $vhdfile testghdl/src/
done

