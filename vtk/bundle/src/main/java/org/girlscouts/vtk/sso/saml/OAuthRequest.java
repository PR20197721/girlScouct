package org.girlscouts.vtk.sso.saml;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.sso.AccountSettings;
import org.girlscouts.vtk.sso.AppSettings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthRequest {


	public static final int base64 = 1;
	private Deflater deflater;

	public String getRequest(int format) throws XMLStreamException, IOException {
		String result = "";

String certificateS="MIIErDCCA5SgAwIBAgIOAUpP+IcIAAAAADe+GfMwDQYJKoZIhvcNAQEFBQAwgZAx"+
"KDAmBgNVBAMMH1NlbGZTaWduZWRDZXJ0XzE1RGVjMjAxNF8yMjAxMzQxGDAWBgNV"+
"BAsMDzAwRFowMDAwMDBNaWEwNjEXMBUGA1UECgwOU2FsZXNmb3JjZS5jb20xFjAU"+
"BgNVBAcMDVNhbiBGcmFuY2lzY28xCzAJBgNVBAgMAkNBMQwwCgYDVQQGEwNVU0Ew"+
"HhcNMTQxMjE1MjIwMTM1WhcNMTYxMjE0MjIwMTM1WjCBkDEoMCYGA1UEAwwfU2Vs"+
"ZlNpZ25lZENlcnRfMTVEZWMyMDE0XzIyMDEzNDEYMBYGA1UECwwPMDBEWjAwMDAw"+
"ME1pYTA2MRcwFQYDVQQKDA5TYWxlc2ZvcmNlLmNvbTEWMBQGA1UEBwwNU2FuIEZy"+
"YW5jaXNjbzELMAkGA1UECAwCQ0ExDDAKBgNVBAYTA1VTQTCCASIwDQYJKoZIhvcN"+
"AQEBBQADggEPADCCAQoCggEBAN5j2MSAwSC8LK4vUQ7TI1WlxCd4WSf23+L3PKEI"+
"HkMp406/W9HQ3xaPoWSpGaAejIErcdFKJ21T8X6I9zpivceIReW57/v+GsPeq+Ui"+
"Auie4C1tkQsC/+R3rC3YXwdzmAU0nmYhNw3DtDXpw5AVxOGLYSBiN54q2/b6jd0V"+
"BU+esJuAkb7JUe+DiY+xjflT1rxuyrM9vmbVC4s/tninYITirQ21A4+bgiKpSPJZ"+
"PryrUPfD4/9GgT7YZz43pwym6d4eaDghcdZTknUPNAtYHRVY/mgpEucT5qoAnhAU"+
"8Knzpdonu6nCy/eZAZ27EZaGCPyH6g38NlnD3SK90/tZYFkCAwEAAaOCAQAwgf0w"+
"HQYDVR0OBBYEFNX9rNUaXacYj241ABzrmycw48C6MIHKBgNVHSMEgcIwgb+AFNX9"+
"rNUaXacYj241ABzrmycw48C6oYGWpIGTMIGQMSgwJgYDVQQDDB9TZWxmU2lnbmVk"+
"Q2VydF8xNURlYzIwMTRfMjIwMTM0MRgwFgYDVQQLDA8wMERaMDAwMDAwTWlhMDYx"+
"FzAVBgNVBAoMDlNhbGVzZm9yY2UuY29tMRYwFAYDVQQHDA1TYW4gRnJhbmNpc2Nv"+
"MQswCQYDVQQIDAJDQTEMMAoGA1UEBhMDVVNBgg4BSk/4hzIAAAAAN74Z8zAPBgNV"+
"HRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQCIRc5HjrN15ZzB7talPEJz"+
"YqVhZP0z8gd9BAN5FbFpb488ai57KzhrfTH7f5wVAquFm4OaXAnfizJeAEgmMw4A"+
"bp//Dt+/WWDMkM6goUREotAF3vNcoFuYHBmrMyyxMIY4x7HryIB0lOEjn1DOh0+8"+
"G7kz2gkZ52BxlD/3tWnXUwasZ+TK9ZYniTUnjArPlwb8k5aUyzcCjWdZ/oNpU4x0"+
"slJzHgm89IpT0oqOKj4n0Pl8I/o7EhHoKSzFQDP02XF26r2poudJkS+OQb/p/U7M"+
"dnruBIiGFrQ80RGEg81TRTbmN+xpf6h1mXAuwmAlc0O0ezv+pgrLxaz+wLYAogDP";

String signatureValue="PmrZ1HHc9+yZD9B7nb1HFtyzpnl9OBVd7F0Z5Q1S4VzjUADF+2c3acoKTHmaxl73/8DFL3K1iYeJ"+
"kD7sPBInq9TfCB3Bu7icfn9rTjC09GV8egOpWsGW/KNfQ+L7NOM2OV7MWL++dcT4aV53gWdmP8HJ"+
"/CwSJADEdHKn4VWKUOQ0ZkjpHvwOXhPvEmQb9y6d/U9uaGhoLHosQOzikVqmapkNyhssdBFP7qne"+
"KabHyXDhVxs7OBWSb1S0M22ia8J6ct1+Ev9BaZBMv+7Ap0gIoQsXazOpHu42E/05FR6YD59UcWlI"+
"tz9soSZNVjvDrLU4THwsuAe/9ixe1GNNqvYrEg==";

String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
"<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_cd3649b3639560458bc9d9b33dfee8d21378409114655\" IssueInstant=\"2015-05-27T14:25:14.654Z\" Version=\"2.0\">"+
  "<saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">3MVG9GiqKapCZBwHKlrZlQzDydYIMVImlAqkN6xOQASZjR1gpCDTFSdBAHismLIq003WzHDWbw_bne9NY6WGE</saml:Issuer>"+
  "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">"+
    "<ds:SignedInfo>"+
    "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"+
    "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>"+
    "<ds:Reference URI=\"#_596e1aec0c6ae807ed86541aa18060241432756416337\">"+
      "<ds:Transforms>"+
      "<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>"+
      "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"ds saml\"/>"+
      "</ds:Transform>"+
      "</ds:Transforms>"+
    "<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>"+
    "<ds:DigestValue>fnxuFKeG/iwNTGrfCBtrYqyutgE=</ds:DigestValue>"+
    "</ds:Reference>"+
    "</ds:SignedInfo>"+
    "<ds:SignatureValue>"+signatureValue+
    "</ds:SignatureValue>"+
    "<ds:KeyInfo>"+
    "<ds:X509Data>"+
    "<ds:X509Certificate>"+certificateS+
    "</ds:X509Certificate>"+
    "</ds:X509Data>"+
    "</ds:KeyInfo>"+
  "</ds:Signature>"+
"<saml:Subject xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">00DZ000000Mia06@appled.strudel@gmail.com</saml:NameID>"+
  "<saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
  "<saml:SubjectConfirmationData NotOnOrAfter=\"2015-05-29T20:10:31.223Z\" Recipient=\"https://gsuat-gsmembers.cs11.force.com/members/services/oauth2/token\"/>"+
  "</saml:SubjectConfirmation>"+
"</saml:Subject>"+
"<saml:Conditions NotBefore=\"2015-05-27T20:00:17.950Z\" NotOnOrAfter=\"2015-05-29T20:05:47.950Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:AudienceRestriction xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:Audience>https://gsuat-gsmembers.cs11.force.com</saml:Audience>"+
"</saml:AudienceRestriction>"+
"</saml:Conditions>"+
  "<saml:AuthnStatement AuthnInstant=\"2015-05-27T15:25:14.655Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
    "<saml:AuthnContext xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
    "<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml:AuthnContextClassRef>"+
    "</saml:AuthnContext>"+
  "</saml:AuthnStatement>"+
"</saml:Assertion>";


              
		result = encodeSAMLRequest(xml.getBytes());
		return result;
	}

	public String encodeSAMLRequest(byte[] pSAMLRequest) throws RuntimeException {

		Base64 base64Encoder = new Base64();

		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);

			DeflaterOutputStream def = new DeflaterOutputStream(byteArray, deflater);
			def.write(pSAMLRequest);
			def.close();
			byteArray.close();

			String stream = new String(base64Encoder.encode(byteArray.toByteArray()));

			return stream.trim();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	

public void doAuth() throws Exception{
String xml =  getRequest(1);

		try {
			//code = URLDecoder.decode(code, "UTF-8");
		} catch (Exception e) {
			//log.error("Error decoding the code. Left it as is.");
		}

		HttpClient httpclient = new HttpClient();
		String tokenUrl =  "https://gsuat-gsmembers.cs11.force.com/members/services/oauth2/token";
		PostMethod post = new PostMethod(tokenUrl);
		//post.addParameter("code", code);
		post.addParameter("grant_type", "urn:ietf:params:oauth:grant-type:saml2-bearer");//authorization_code");
post.addParameter("assertion",xml);		
//post.addParameter("client_id", clientId);
		//post.addParameter("client_secret", clientSecret);
		//post.addParameter("redirect_uri", callbackUrl);
		//log.debug(post.getRequestCharSet());
		//log.debug(post.getRequestEntity().toString());

		try {

			Header headers[] = post.getRequestHeaders();
			for (Header h : headers) {
				//log.debug("Headers: " + h.getName() + " : " + h.getValue());
			}

			httpclient.executeMethod(post);

			if (post.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject authResponse = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));
				} catch (JSONException e) {
					
				}
			} else {
				
			}
		} catch (Exception e) {
			
		} finally {
			post.releaseConnection();
		}
	}


public void x(String token){


token="00DZ000000Mia06c0gsKMhJTdErLikqTUnNcUjPTczM0UvOzwUA";
            try{
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod( "https://gsuat-gsmembers.cs11.force.com/members/services/data/v20.0/query");
		get.setRequestHeader("Authorization",
				"OAuth " + token);

		NameValuePair[] params = new NameValuePair[1];
		params[0] = new NameValuePair(
				"q",
				"SELECT ID,name,email, phone, mobilephone, ContactId, FirstName  from User where id='005Z0000002P51XIAS' limit 1");
		get.setQueryString(params);
		try {

			httpclient.executeMethod(get);

			if (get.getStatusCode() == HttpStatus.SC_OK) {
				try {

					JSONObject response = new JSONObject(
							new JSONTokener(new InputStreamReader(
									get.getResponseBodyAsStream())));
					
				} catch (Exception e) {
					
				}
			}
		} catch (Exception e) {
			
		} finally {
			get.releaseConnection();
		}
	}catch(Exception e){e.printStackTrace();}
}	



}//end class
