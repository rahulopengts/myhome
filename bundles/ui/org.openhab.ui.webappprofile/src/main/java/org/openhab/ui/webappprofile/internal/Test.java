package org.openhab.ui.webappprofile.internal;

public class Test {

	public static void main(String sr[]){
					 
		 String f=	"<div class=\"button\"><button type=\"submit\">Create</button></div>";
		 
	    String d=	"</div>	<div class=\"iFooter\">&copy;2010-2014 openHAB.org</div> ]]></data> </part> </root>";
	
	    StringBuffer df	=	new StringBuffer(d);
	    StringBuffer ff	=	new StringBuffer(f);
	    
	    df.indexOf("</div>");
	    
	    df.insert(6, ff);
	    
	    System.out.println(new String(df));
	}
}
