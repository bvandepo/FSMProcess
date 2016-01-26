#!/bin/sh
echo "------Testbench Waveform erasor by Bertrand VANDEPORTAELE IUT GEII TOULOUSE, LAAS/CNRS 01/2016----------" 
echo "usage: ./clean.sh component_name remove_sav_file"
echo "component_name: the file in the parent directory without extension (default: test)"
echo "remove_sav_file: provide any second parameter to remove the .sav file that is used by Gtkwave to store the display settings for the simulation (default don't remove)"
echo "        example: ./clean.sh mycomponent 1 " 
echo "--------------------------------------------------------------------------------------------------------" 
if [ -z "$1" ]
then
COMPONENT_NAME="test"
else
COMPONENT_NAME=$1
fi
if [ -z "$2" ]
then
echo "Keeping Gtkwave save file"
else
echo "Erasing Gtkwave save file"
rm $COMPONENT_NAME"_tb.sav"
fi
echo "--------------------------------------------------------------------------------------------------------" 
rm *.o *.cf *.vcd *~
rm $COMPONENT_NAME"_tb"
ghdl --clean



