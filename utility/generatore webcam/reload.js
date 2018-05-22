function reloadApplet(appletid, divid, appletCode)
{	
	alert("reload");
	var applet = document.getElementById(appletid);
	var divToRefresh = document.getElementById(divid);	
	if(!applet.get_isOn())
	{
		applet.destroy();
		divToRefresh.innerHTML = appletCode;
	}	
	setTimeout('reloadApplet("' + appletid + '", "' + divid + '", appletCode)', 10000);
}


