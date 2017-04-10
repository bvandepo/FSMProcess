#!/bin/bash
echo "add licence information to the file" 
echo "usage: ./addlicence.sh fichier.fsm"

orgfile=$1
tmpfile=`echo "${1}.tmp"`

echo "/*" > $tmpfile
cat ../licence >> $tmpfile
echo "*/" >> $tmpfile
cat $orgfile >>$tmpfile
mv $tmpfile $orgfile


