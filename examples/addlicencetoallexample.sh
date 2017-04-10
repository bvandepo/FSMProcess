#!/bin/bash
#echo "add licence information to all the example files" 


for i in *.fsm
do
#  j="${i%.fsm}"
#  echo ${j}
  echo ${i}
  ./addlicence.sh ${i}
done


