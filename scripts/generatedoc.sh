#!/bin/bash
 
echo ---------------------------------------------------------------
echo ---------------------------------------------------------------
echo Processing doc.txt
echo ---------------------------------------------------------------
echo ----------------------COMPUTING FILE NAMES----------------------
../bin/FsmProcess.jar -i -f ../doc.txt

for filename in `ls ../doc_generated/*.fsm`
do
echo ---------------------------------------------------------------
echo ---------------------------------------------------------------
echo Processing FSM File: $filename
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
rm $dotfile $vhdfile
echo ----------------------RUNNING THE PARSER----------------------
../bin/FsmProcess.jar -i -f $filename
echo ----------------------DISPLAY THE DOT CODE----------------------
cat $dotfile
echo ----------------------GENERATE THE DOT GRAPH IN PNG----------------------
dot -Tpng $dotfile -o $pngfile
echo ----------------------GENERATE THE DOT GRAPH IN PDF----------------------
dot -Tpdf $dotfile   -o  $pdffile
echo ----------------------DISPLAY THE VHDL FILE----------------------
cat $vhdfile
done

