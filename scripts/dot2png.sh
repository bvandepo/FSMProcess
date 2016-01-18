#!/bin/bash
 
echo usage: ./dot2png.sh fichier.dot 
echo ----------------------COMPUTING FILE NAMES----------------------
dotfile=`echo "${1}"`
echo $dotfile
pngfile=`echo "${1/.dot/.png}"`
echo $pngfile

dot -Tpng $dotfile -o $pngfile
display $pngfile 

