#!/bin/bash
 
echo usage: ./dot2png.sh fichier.fsm   
echo ----------------------COMPUTING FILE NAMES----------------------
dotfile=`echo "${1/.fsm/.dot}"`
echo $dotfile
pngfile=`echo "${1/.fsm/.png}"`
echo $pngfile

dot -Tpng $dotfile -o $pngfile

