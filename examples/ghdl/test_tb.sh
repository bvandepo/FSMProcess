#!/bin/bash
 
COMPONENT_FILES="../test"
TB_DIR="../"
TB_FILE="test_tb"

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
ghdl -r $GHDL_OPTIONS $TB_FILE  --vcd=$TB_FILE.vcd --stop-time=20us
#TODO: compute automatically --stop-time=10us

rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
echo ------------------------ Display simulation of $TB_FILE.vcd ------------------------ 
gtkwave $TB_FILE.vcd $TB_FILE.sav 
rc=$?; if [[ $rc != 0 ]]; then exit $rc; fi
#le .sav sert à conserver les paramètres d'affichage , ctrl-s dans gtkwave pour sauver

