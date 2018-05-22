JStreamer -- A proxy for Mobotix IP cam.

									   
-USAGE:

There are 2 ways for running an instance of JStreamer. 
First passing parameters via command line:
	i.e.: java -jar JStreamer <port> <username> <password> <ip> 
		where <port> is the port where proxy is listening for connection, <username> <password> 
		and <ip> are IP cam parameters.

Second using xml file for configuration:
	
	i.e.: java -jar JStreamer <xml_file.xml>
		where xml_file  has the following syntax
		
		<?xml version="1.0" encoding="UTF-8"?> <!-- Prologue -->
		<proxy>
			<webcam>
			  <port>1234</port> <!-- Port where this proxy is listen for -->
	  		  <username>username</username> <!-- Username for webcam connection -->
	  		  <password>password</password> <!--  Password for webcam connection -->
	  		  <ip>123.456.789.123</ip> <!-- webcam ip address -->
			</webcam>
		</proxy>
