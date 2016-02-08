#!/bin/sh
for filename in *.fsm
do
java -jar ../bin/FsmProcess.jar -c -i  -f $filename
done

