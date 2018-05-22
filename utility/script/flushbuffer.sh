#!/bin/bash
while [ true ]; 
do
	wget http://localhost:$1 -nv -O /dev/null
	sleep 10
done&
