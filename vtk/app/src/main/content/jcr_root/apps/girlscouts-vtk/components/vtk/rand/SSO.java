package org.girlscouts.vtk.impl.servlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.ServerException;
import java.security.cert.CertificateException;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.dao.SalesforceDAOFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.ejb.TroopUtil;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.sso.saml.OAuthRequest;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts VTK Salesforce Authentication Servlet", description = "Handles OAuth Authentication with Salesforce", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sso"),
		//@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "null"),
		//@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "null") })
	
		
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = {
				"html", "xml", "saml", "res", "jsp" }),
		
		
		
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = {
		"POST", "GET" })
		 })
		

public class SSO extends SlingAllMethodsServlet{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		
	}



	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
Enumeration enParams = request.getParameterNames(); 
while(enParams.hasMoreElements()){
 String paramName = (String)enParams.nextElement();

}

  String certificateS="MIIErDCCA5SgAwIBAgIOAUpP+IcIAAAAADe+GfMwDQYJKoZIhvcNAQEFBQAwgZAxKDAmBgNVBAMMH1NlbGZTaWduZWRDZXJ0XzE1RGVjMjAxNF8yMjAxMzQxGDAWBgNVBAsMDzAwRFowMDAwMDBNaWEwNjEXMBUGA1UECgwOU2FsZXNmb3JjZS5jb20xFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xCzAJBgNVBAgMAkNBMQwwCgYDVQQGEwNVU0EwHhcNMTQxMjE1MjIwMTM1WhcNMTYxMjE0MjIwMTM1WjCBkDEoMCYGA1UEAwwfU2VsZlNpZ25lZENlcnRfMTVEZWMyMDE0XzIyMDEzNDEYMBYGA1UECwwPMDBEWjAwMDAwME1pYTA2MRcwFQYDVQQKDA5TYWxlc2ZvcmNlLmNvbTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzELMAkGA1UECAwCQ0ExDDAKBgNVBAYTA1VTQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN5j2MSAwSC8LK4vUQ7TI1WlxCd4WSf23+L3PKEIHkMp406/W9HQ3xaPoWSpGaAejIErcdFKJ21T8X6I9zpivceIReW57/v+GsPeq+UiAuie4C1tkQsC/+R3rC3YXwdzmAU0nmYhNw3DtDXpw5AVxOGLYSBiN54q2/b6jd0VBU+esJuAkb7JUe+DiY+xjflT1rxuyrM9vmbVC4s/tninYITirQ21A4+bgiKpSPJZPryrUPfD4/9GgT7YZz43pwym6d4eaDghcdZTknUPNAtYHRVY/mgpEucT5qoAnhAU8Knzpdonu6nCy/eZAZ27EZaGCPyH6g38NlnD3SK90/tZYFkCAwEAAaOCAQAwgf0wHQYDVR0OBBYEFNX9rNUaXacYj241ABzrmycw48C6MIHKBgNVHSMEgcIwgb+AFNX9rNUaXacYj241ABzrmycw48C6oYGWpIGTMIGQMSgwJgYDVQQDDB9TZWxmU2lnbmVkQ2VydF8xNURlYzIwMTRfMjIwMTM0MRgwFgYDVQQLDA8wMERaMDAwMDAwTWlhMDYxFzAVBgNVBAoMDlNhbGVzZm9yY2UuY29tMRYwFAYDVQQHDA1TYW4gRnJhbmNpc2NvMQswCQYDVQQIDAJDQTEMMAoGA1UEBhMDVVNBgg4BSk/4hzIAAAAAN74Z8zAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQCIRc5HjrN15ZzB7talPEJzYqVhZP0z8gd9BAN5FbFpb488ai57KzhrfTH7f5wVAquFm4OaXAnfizJeAEgmMw4Abp//Dt+/WWDMkM6goUREotAF3vNcoFuYHBmrMyyxMIY4x7HryIB0lOEjn1DOh0+8G7kz2gkZ52BxlD/3tWnXUwasZ+TK9ZYniTUnjArPlwb8k5aUyzcCjWdZ/oNpU4x0slJzHgm89IpT0oqOKj4n0Pl8I/o7EhHoKSzFQDP02XF26r2poudJkS+OQb/p/U7MdnruBIiGFrQ80RGEg81TRTbmN+xpf6h1mXAuwmAlc0O0ezv+pgrLxaz+wLYAogDP";
  org.girlscouts.vtk.sso.AccountSettings accountSettings = new org.girlscouts.vtk.sso.AccountSettings();
  accountSettings.setCertificate(certificateS);


org.girlscouts.vtk.sso.saml.Response samlResponse = null;
try {
	samlResponse = new org.girlscouts.vtk.sso.saml.Response(accountSettings);
} catch (CertificateException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
String token= null;
  try {
	//samlResponse.loadXmlFromBase64(request.getParameter("SAMLResponse"));
	 token =samlResponse.getToken(request.getParameter("SAMLResponse"));
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
System.out.println("Saml token: " + token);
response.sendRedirect("http://localhost:4503/content/girlscouts-vtk/en/vtk.html");
	}

	public void updateConfig(Dictionary configs) {
		// TODO Auto-generated method stub
		
	}

	
}
