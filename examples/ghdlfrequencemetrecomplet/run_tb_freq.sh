#!/bin/bash
echo "------Testbench Waveform generator by Bertrand VANDEPORTAELE IUT GEII TOULOUSE, LAAS/CNRS 01/2016----------" 
echo "usage: ./test_tb.sh component_name  duration"
echo "component_name: the file in the parent directory without extension (default: test)"
echo "duration of the simulation as string, eg: 5000us (default: 100us)"
echo "        example: ./test_tb.sh mycomponent 1200us " 
echo "--------------------------------------------------------------------------------------------------------" 
 
GHDL_OPTIONS="--std=02 --ieee=synopsys -fexplicit  --warn-no-vital-generic --workdir=./"



echo ------------------------ Erasing old files            ------------------------  
rm work/*
rm frequencemetrecomplet_testbench
rm frequencemetrecomplet_testbench.vcd

#   export GHDL_OPTIONS="--std=02 --ieee=synopsys -fexplicit  --warn-no-vital-generic --workdir=./"
 
echo ------------------------ Processing included component ------------------------ 
  
mkdir work
ghdl -a $GHDL_OPTIONS --work=work ../multiplier32bits.vhd
ghdl -a $GHDL_OPTIONS --work=work ../multiplier32bits_pack.vhd
ghdl -a $GHDL_OPTIONS --work=work ../diviseur_generic_N_64.vhd
ghdl -a $GHDL_OPTIONS --work=work ../diviseur_generic_N_64_pack.vhd
ghdl -a $GHDL_OPTIONS --work=work ../bin2bcd32bits.vhd
ghdl -a $GHDL_OPTIONS --work=work ../bin2bcd32bits_pack.vhd
ghdl -a $GHDL_OPTIONS --work=work ../frequencemetre2.vhd
ghdl -a $GHDL_OPTIONS --work=work ../frequencemetre2_pack.vhd
ghdl -a $GHDL_OPTIONS --work=work ../frequencemetrebcmd.vhd
ghdl -a $GHDL_OPTIONS --work=work ../frequencemetrebcmd_pack.vhd
 
ghdl --dir --work=work
#ghdl -a $GHDL_OPTIONS  --work=work -Pwork ../frequencemetrepartiel.vhd
ghdl -a $GHDL_OPTIONS  --work=work -Pwork ../frequencemetrecomplet.vhd
ghdl -a $GHDL_OPTIONS  --work=work -Pwork ../frequencemetrecomplet_testbench.vhd

echo -------------------------------------------------------------------

ghdl -e $GHDL_OPTIONS --work=work -Pwork  frequencemetrecomplet_testbench 
 
#ghdl -r $GHDL_OPTIONS  frequencemetrecomplet_testbench   --vcd=frequencemetrecomplet_testbench.vcd --stop-time=110000us
 ghdl -r $GHDL_OPTIONS  frequencemetrecomplet_testbench   --vcd=frequencemetrecomplet_testbench.vcd --stop-time=110us
 
gtkwave  frequencemetrecomplet_testbench.vcd  frequencemetrecomplet_testbench.sav 
 
