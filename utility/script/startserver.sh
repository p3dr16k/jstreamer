#!/bin/bash

#javapid=$(pidof java)
#javapid=$(ps ax | grep "java -jar JStreamer.jar $1" | xargs | awk '{print $1}')
javapid=$(ps ax | grep "java -jar JStreamer.jar $1" | awk '{print $1}' | xargs | awk '{print $2}')
#javapid="y"
outfile=$2
errfile=$3

echo "JAVAPID = $javapid"
while [ true ]; 
do
	if [ x$javapid = x ];
	then		
		echo "Server Started"
		#javapid=$(pidof java)
		#javapid=$(ps ax | grep "java -jar JStreamer.jar $1" | xargs | awk '{print $1}')
		javapid=$(ps ax | grep "java -jar JStreamer.jar $1" | awk '{print $1}' | xargs | awk '{print $2}')
		echo "JAVAPID = $javapid"
		setsid java -jar JStreamer.jar $1 > $outfile 2> $errfile		
	fi
	sleep 60
done&
