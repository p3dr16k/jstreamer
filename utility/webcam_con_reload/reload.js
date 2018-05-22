function reloadApplet(appletid, divid, appletCode)
{
	var applet = document.getElementById(appletid);
	var divToRefresh = document.getElementById(divid);
	//alert(applet + " " + applet.get_isOn() + " " + divToRefresh + " " + appletCode);
	if(!applet.get_isOn())
	{
		applet.destroy();
		divToRefresh.innerHTML = appletCode;
	}	
	setTimeout('reloadApplet("cambozola", "applet", appletCode)', 10000);
}


