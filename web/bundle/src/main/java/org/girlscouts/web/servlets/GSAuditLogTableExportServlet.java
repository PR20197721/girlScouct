package org.girlscouts.web.servlets;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;

import org.apache.commons.validator.routines.UrlValidator;

@Component
@Service({Servlet.class})
@Property(name="sling.servlet.paths", value={"/bin/audit/gspage"})
public class GSAuditLogTableExportServlet
  extends SlingAllMethodsServlet
{
  private static final long serialVersionUID = -9205573694010666588L;
  
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServletException, IOException
  {
    String extension = request.getRequestPathInfo().getExtension();
    String path = request.getParameter("path");
    String url = request.getRequestURL().toString();
    String uri = request.getRequestURI();
    url = url.substring(0,url.indexOf(uri));
    String target = request.getResourceResolver().map(request, path + ".audit.json");
    String secureTarget = (url + target).replace("http","https");
    if (("json".equals(extension)) && AuthUtil.isRedirectValid(request, target)) {	
    	String[] schemes = {"http","https"};
    	UrlValidator urlValidator = new UrlValidator(schemes);
    	if(urlValidator.isValid(secureTarget)){
    		//https
    		response.sendRedirect(secureTarget);
    	} else{
    		//http. localhost, for example
    		response.sendRedirect(target);
    	}
    } else{
    	response.sendError(500);
    }
  }
}
