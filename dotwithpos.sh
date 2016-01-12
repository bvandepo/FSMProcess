#!/bin/bash 
dot -Kfdp -n -Tpng $1  | display  &

