<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.girlscouts.vtk.sso.*,org.girlscouts.vtk.sso.saml.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<%

String certificateS="MIIErDCCA5SgAwIBAgIOAUpP+IcIAAAAADe+GfMwDQYJKoZIhvcNAQEFBQAwgZAxKDAmBgNVBAMMH1NlbGZTaWduZWRDZXJ0XzE1RGVjMjAxNF8yMjAxMzQxGDAWBgNVBAsMDzAwRFowMDAwMDBNaWEwNjEXMBUGA1UECgwOU2FsZXNmb3JjZS5jb20xFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xCzAJBgNVBAgMAkNBMQwwCgYDVQQGEwNVU0EwHhcNMTQxMjE1MjIwMTM1WhcNMTYxMjE0MjIwMTM1WjCBkDEoMCYGA1UEAwwfU2VsZlNpZ25lZENlcnRfMTVEZWMyMDE0XzIyMDEzNDEYMBYGA1UECwwPMDBEWjAwMDAwME1pYTA2MRcwFQYDVQQKDA5TYWxlc2ZvcmNlLmNvbTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzELMAkGA1UECAwCQ0ExDDAKBgNVBAYTA1VTQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN5j2MSAwSC8LK4vUQ7TI1WlxCd4WSf23+L3PKEIHkMp406/W9HQ3xaPoWSpGaAejIErcdFKJ21T8X6I9zpivceIReW57/v+GsPeq+UiAuie4C1tkQsC/+R3rC3YXwdzmAU0nmYhNw3DtDXpw5AVxOGLYSBiN54q2/b6jd0VBU+esJuAkb7JUe+DiY+xjflT1rxuyrM9vmbVC4s/tninYITirQ21A4+bgiKpSPJZPryrUPfD4/9GgT7YZz43pwym6d4eaDghcdZTknUPNAtYHRVY/mgpEucT5qoAnhAU8Knzpdonu6nCy/eZAZ27EZaGCPyH6g38NlnD3SK90/tZYFkCAwEAAaOCAQAwgf0wHQYDVR0OBBYEFNX9rNUaXacYj241ABzrmycw48C6MIHKBgNVHSMEgcIwgb+AFNX9rNUaXacYj241ABzrmycw48C6oYGWpIGTMIGQMSgwJgYDVQQDDB9TZWxmU2lnbmVkQ2VydF8xNURlYzIwMTRfMjIwMTM0MRgwFgYDVQQLDA8wMERaMDAwMDAwTWlhMDYxFzAVBgNVBAoMDlNhbGVzZm9yY2UuY29tMRYwFAYDVQQHDA1TYW4gRnJhbmNpc2NvMQswCQYDVQQIDAJDQTEMMAoGA1UEBhMDVVNBgg4BSk/4hzIAAAAAN74Z8zAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQCIRc5HjrN15ZzB7talPEJzYqVhZP0z8gd9BAN5FbFpb488ai57KzhrfTH7f5wVAquFm4OaXAnfizJeAEgmMw4Abp//Dt+/WWDMkM6goUREotAF3vNcoFuYHBmrMyyxMIY4x7HryIB0lOEjn1DOh0+8G7kz2gkZ52BxlD/3tWnXUwasZ+TK9ZYniTUnjArPlwb8k5aUyzcCjWdZ/oNpU4x0slJzHgm89IpT0oqOKj4n0Pl8I/o7EhHoKSzFQDP02XF26r2poudJkS+OQb/p/U7MdnruBIiGFrQ80RGEg81TRTbmN+xpf6h1mXAuwmAlc0O0ezv+pgrLxaz+wLYAogDP";

  org.girlscouts.vtk.sso.AccountSettings accountSettings = new org.girlscouts.vtk.sso.AccountSettings();
  accountSettings.setCertificate(certificateS);

System.err.println("SAml resp: "+request.getParameter("SAMLResponse") );

org.girlscouts.vtk.sso.saml.Response samlResponse = new org.girlscouts.vtk.sso.saml.Response(accountSettings);
System.err.println("ttttt: ");
  samlResponse.loadXmlFromBase64(request.getParameter("SAMLResponse"));

System.err.println("URL : "+ request.getRequestURL().toString() );
  samlResponse.setDestinationUrl(request.getRequestURL().toString());

  if (samlResponse.isValid()) {

    // the signature of the SAML Response is valid. The source is trusted
    java.io.PrintWriter writer = response.getWriter();
    writer.write("OK!");
    String nameId = samlResponse.getNameId();
    writer.write(nameId);
    writer.flush();

//new OAuthJWTHandler_v1().doIt(); 

OAuthRequest oauth= new OAuthRequest();
//oauth.doAuth();
oauth.x(nameId);





  } else {
System.err.println("bad ");
    // the signature of the SAML Response is not valid
    java.io.PrintWriter writer = response.getWriter();
    writer.write("Failed\n");
    writer.write(samlResponse.getError());
    writer.flush();

  }
%>
</body>
</html>
