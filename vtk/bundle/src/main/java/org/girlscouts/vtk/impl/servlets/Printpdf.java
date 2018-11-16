package org.girlscouts.vtk.impl.servlets;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.ejb.PdfUtil;
import org.girlscouts.vtk.ejb.YearPlanUtil;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;

import java.io.IOException;
import java.rmi.ServerException;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "pdfPrint", extensions = "html", methods = "GET")
public class Printpdf extends SlingSafeMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	YearPlanUtil yearPlanUtil;
	
	@Reference
	PdfUtil pdfUtil;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {

		response.setHeader("Content-Disposition", "inline;filename=\"print.pdf\"");
		response.setContentType("application/pdf");

		Troop troop = VtkUtil.getTroop(request.getSession());
		User user = VtkUtil.getUser(request.getSession());
		String act = request.getParameter("act");
		if (act == null){
			act = "";
		}

		pdfUtil.createPrintPdf( act,  troop,  user,  request.getParameter("mid"), response);
		
	    }
}//edn main 


