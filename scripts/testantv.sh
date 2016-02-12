#!/bin/bash
cd ../src/
echo ----------------------RUNNING THE PARSER----------------------
echo WARNING: the name to use is neither FsmParser of FsmLexer BUT: Fsm

#java org.antlr.v4.gui.TestRig Fsm fsmfile -gui  -tokens  ../examples/test.fsm
java org.antlr.v4.gui.TestRig Fsm fsmgeneric -gui   -tokens  ../examples/test.fsm

cd ../srcipts/
#grun FsmParser fsmfile
