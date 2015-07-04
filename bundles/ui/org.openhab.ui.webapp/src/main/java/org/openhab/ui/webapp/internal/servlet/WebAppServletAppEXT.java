package org.openhab.ui.webapp.internal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebAppServletAppEXT extends WebAppServlet {

@Override
public void service(ServletRequest req, ServletResponse res)
		throws ServletException, IOException {
	// TODO Auto-generated method stub
	super.service(req, res);
	System.out.println("\n IN SERVICE");
}	

}
