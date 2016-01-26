#!/bin/bash
echo "------Testbench Waveform generator by Bertrand VANDEPORTAELE IUT GEII TOULOUSE, LAAS/CNRS 01/2016----------" 
echo "usage: ./test_tb.sh component_name  duration"
echo "component_name: the file in the parent directory without extension (default: test)"
echo "duration of the simulation as string, eg: 5000us (default: 100us)"
echo "        example: ./test_tb.sh mycomponent 1200us " 
echo "--------------------------------------------------------------------------------------------------------" 

#TODO: deal with multiple COMPONENT_FILES list !!!!

if [ -z "$1" ]
then
COMPONENT_NAME="test"
else
COMPONENT_NAME=$1
fi
if  [ -z "$2" ]
then
DURATION="100us"
else 
DURATION=$2
fi
COMPONENT_FILES="../"$COMPONENT_NAME

java -jar ../../bin/FsmProcess.jar -f $COMPONENT_FILES".fsm"

TB_DIR="../"
TB_FILE=$COMPONENT_NAME"_tb"
GHDL_OPTIONS="--std=02 --ieee=synopsys -fexplicit  --warn-no-vital-generic --workdir=./"
for CURRENT_FILES  in  $COMPONENT_FILES
do
echo ------------------------ Processing $CURRENT_FILES.vhd ------------------------ 
ghdl -a $GHDL_OPTIONS $CURRENT_FILES.vhd
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
done

echo ------------------------ Analysis of $TB_DIR$TB_FILE.vhd ------------------------ 
ghdl -a $GHDL_OPTIONS $TB_DIR$TB_FILE.vhd
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
echo ------------------------ Elaboration of $TB_FILE ------------------------ 
ghdl -e $GHDL_OPTIONS $TB_FILE
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
echo ------------------------ Running simulation of $TB_FILE ------------------------ 
ghdl -r $GHDL_OPTIONS $TB_FILE  --vcd=$TB_FILE.vcd --stop-time=$DURATION
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
echo ------------------------ Display simulation of $TB_FILE.vcd ------------------------ 
gtkwave $TB_FILE.vcd $TB_FILE.sav 
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
#le .sav sert à conserver les paramètres d'affichage , ctrl-s dans gtkwave pour sauver

