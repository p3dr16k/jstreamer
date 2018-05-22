
function generateCode(url, width, height, failureImage, appletUrl)
{
			var divForEmbedding = document.getElementById('webcam_div');

			var appletCode = '<applet id="cambozola"' + 
			 'code="com.charliemouse.cambozola.Viewer"' + 
			 'archive="' + appletUrl + '" width="' +
			  width + '" height="' + height + '">' +
			 '<param name="url" value="http://' + url + '" >' +
			 '<param name="accessories" value="Home,ZoomIn,ZoomOut,Pan,Snapshot">' +
			 '<param name="accessorystyle" value="always">' +
		 	 '<param name="failureimage" value="' + failureImage + '">' +
		 	 '</applet>';							 
							 
			var reloadCode = "<script type='text/javascript'>" + 
			 "reloadApplet('cambozola', 'webcam_div'," +  appletCode + ");" +
			 "<\/script>";	
	
	//se esiste un div con id =  webcam_div
	//inserisco l'applet all'interno del div
	if(divForEmbedding != null)
	{
		var codeToPrint = appletCode + reloadCode;
		divForEmbedding.innerHTML = codeToPrint;
		//alert(codeToPrint);
		
	}	
	else
	{
		alert('You must create div element with id = webcam_div');
	}
}	
