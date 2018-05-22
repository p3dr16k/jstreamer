#!/bin/bash

ip=$1
to_control=$2

while [ true ]; 
do
	ping $ip -c 4 > tmp_$ip
	nl=$(cat tmp_$ip | wc -l)
	echo "NL = $nl"
	
	to_check=$(grep -i Unreachable tmp_$ip)

	if [ "x$to_check" \!= "x" ];
	then
		#javapid=$(ps ax | grep "java -jar JStreamer.jar $to_control" | xargs | awk '{print $1}')
		 javapid=$(ps ax | grep "java -jar JStreamer.jar $to_control" | awk '{print$1}' | xargs | awk '{print $1}')
		kill -9 $javapid	
		echo "killed $javapid"
	fi
	sleep 300 
done
