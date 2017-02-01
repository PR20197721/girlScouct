package org.girlscouts.vtk.sso.saml;

import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.apache.commons.codec.binary.Base64;
import org.girlscouts.vtk.sso.AccountSettings;
import org.girlscouts.vtk.sso.Constants;
import org.girlscouts.vtk.sso.Error;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Response {

	private Document xmlDoc;
	private NodeList assertions;
	private Element rootElement;
	private final AccountSettings accountSettings;
	private final Certificate certificate;
	private String currentUrl;
	private StringBuffer error;

	public Response(AccountSettings accountSettings) throws CertificateException {
		error = new StringBuffer();
		this.accountSettings = accountSettings;		
		certificate = new Certificate();
		certificate.loadCertificate(this.accountSettings.getCertificate());
	}
	
	public Response(AccountSettings accountSettings, String response) throws Exception {
		this(accountSettings);
		loadXmlFromBase64(response);
	}

	public void loadXmlFromBase64(String response) throws Exception {
		Base64 base64 = new Base64();
		byte[] decodedB = base64.decode(response);
		String decodedS = new String(decodedB);
		
System.err.println("test:"+ decodedS);
	xmlDoc = Utils.loadXML(decodedS);

		
	}

	
	public boolean isValid(){
		try{
		
	System.err.println("test saml: "+ xmlDoc);
			// Security Checks
			rootElement = xmlDoc.getDocumentElement();		
			assertions = xmlDoc.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Assertion");		
			xmlDoc.getDocumentElement().normalize();
		
			// Check SAML version			
			if (!rootElement.getAttribute("Version").equals("2.0")) {
				throw new Exception("Unsupported SAML Version.");
			}
				
			
			// Check ID in the response	
			if (!rootElement.hasAttribute("ID")) {
				throw new Exception("Missing ID attribute on SAML Response.");
			}
				
			checkStatus();
				
			if (assertions == null || assertions.getLength() != 1) {
				throw new Exception("SAML Response must contain 1 Assertion.");
			}
				
			NodeList nodes = xmlDoc.getElementsByTagNameNS("*", "Signature");
			if (nodes == null || nodes.getLength() == 0) {
				throw new Exception("Can't find signature in Document.");
			}
			
			// Check destination
			String destinationUrl = rootElement.getAttribute("Destination");
			if (destinationUrl != null) {
				if(!destinationUrl.equals(currentUrl)){
					throw new Exception("The response was received at " + currentUrl + " instead of " + destinationUrl);
				}
			}
			
			// Check Audience 
			NodeList nodeAudience = xmlDoc.getElementsByTagNameNS("*", "Audience");
			String audienceUrl = nodeAudience.item(0).getChildNodes().item(0).getNodeValue();
			if (audienceUrl != null) {
/*
				if(!audienceUrl.equals(currentUrl)){
					throw new Exception(audienceUrl + " is not a valid audience for this Response");
				}
*/
			}
			
			// Check SubjectConfirmation, at least one SubjectConfirmation must be valid
			NodeList nodeSubConf = xmlDoc.getElementsByTagNameNS("*", "SubjectConfirmation");
			boolean validSubjectConfirmation = true;
			for(int i = 0; i < nodeSubConf.getLength(); i++){
				Node method = nodeSubConf.item(i).getAttributes().getNamedItem("Method");			
System.err.println(1);				
				if(method != null && !method.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:cm:bearer")){
					continue;
				}
System.err.println(2);				
				NodeList childs = nodeSubConf.item(i).getChildNodes();			
				for(int c = 0; c < childs.getLength(); c++){				
					if(childs.item(c).getLocalName().equals("SubjectConfirmationData")){
						Node inResponseTo = childs.item(c).getAttributes().getNamedItem("InResponseTo");					
	//					if(inResponseTo != null && !inResponseTo.getNodeValue().equals("ID of the AuthNRequest")){
	//						validSubjectConfirmation = false;
	//					}
						Node recipient = childs.item(c).getAttributes().getNamedItem("Recipient");					
						if(recipient != null && !recipient.getNodeValue().equals(currentUrl)){
							System.err.println(3);	
							validSubjectConfirmation = false;
							System.err.println(4);			
						}
						Node notOnOrAfter = childs.item(c).getAttributes().getNamedItem("NotOnOrAfter");
						if(notOnOrAfter != null){						
							final Calendar notOnOrAfterDate = javax.xml.bind.DatatypeConverter.parseDateTime(notOnOrAfter.getNodeValue());
							Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
							
							//server diff: per Mike
							now.add(java.util.Calendar.MINUTE, -99);
							
							if(notOnOrAfterDate.before(now)){
								System.err.println(5);	
								validSubjectConfirmation = false;
								System.err.println(6);	
							}
						}
						Node notBefore = childs.item(c).getAttributes().getNamedItem("NotBefore");
						if(notBefore != null){						
							final Calendar notBeforeDate = javax.xml.bind.DatatypeConverter.parseDateTime(notBefore.getNodeValue());
							Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
							if(notBeforeDate.before(now)){
								System.err.println(7);	
								validSubjectConfirmation = false;
								System.err.println(8);	
							}
						}
					}
				}
			}
			if (!validSubjectConfirmation) {
	            throw new Exception("A valid SubjectConfirmation was not found on this Response");
	        }
			
			/*
			if (setIdAttributeExists()) {
				tagIdAttributes(xmlDoc);
			}
	*/
			
			X509Certificate cert = certificate.getX509Cert();		
			
			DOMValidateContext ctx = new DOMValidateContext(cert.getPublicKey(), nodes.item(0));
			
			XMLSignatureFactory sigF = XMLSignatureFactory.getInstance("DOM");	
	
			XMLSignature xmlSignature = sigF.unmarshalXMLSignature(ctx);		
			System.err.println(9);	
			return xmlSignature.validate(ctx); 
			//return true;
		}catch (Error e) {
			e.printStackTrace();
			error.append(e.getMessage());
			return false;
		}catch(Exception e){
			e.printStackTrace();
		    error.append(e.getMessage());
			return false;
		}
	}

	public String getNameId() throws Exception {
		NodeList nodes = xmlDoc.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "NameID");
		if (nodes.getLength() == 0) {
			throw new Exception("No name id found in Document.");
		}
		return nodes.item(0).getTextContent();
	}

	public String getAttribute(String name) {
		HashMap attributes = getAttributes();
		if (!attributes.isEmpty()) {
			return attributes.get(name).toString();
		}
		return null;
	}

	public HashMap getAttributes() {
		HashMap<String, ArrayList> attributes = new HashMap<String, ArrayList>();
		NodeList nodes = xmlDoc.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Attribute");

		if (nodes.getLength() != 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				NamedNodeMap attrName = nodes.item(i).getAttributes();
				String attName = attrName.getNamedItem("Name").getNodeValue();
				NodeList children = nodes.item(i).getChildNodes();

				ArrayList<String> attrValues = new ArrayList<String>();
				for (int j = 0; j < children.getLength(); j++) {
					attrValues.add(children.item(j).getTextContent());
				}
				attributes.put(attName, attrValues);
			}
		} else {
			return null;
		}
		return attributes;
	}
	
	/**
     * Checks if the Status is success
	 * @throws Exception 
	 * @throws $statusExceptionMsg If status is not success
     */
	public Map<String, String>  checkStatus() throws Exception{
		Map<String, String> status = Utils.getStatus(xmlDoc);
		if(status.containsKey("code") && !status.get("code").equals(Constants.STATUS_SUCCESS) ){
			String statusExceptionMsg = "The status code of the Response was not Success, was " + 
					status.get("code").substring(status.get("code").lastIndexOf(':') + 1);
			if(status.containsKey("msg")){
				statusExceptionMsg += " -> " + status.containsKey("msg");
			}
			throw new Exception(statusExceptionMsg);
		}

		return status;
		
	}

	private boolean setIdAttributeExists() {
		for (Method method : Element.class.getDeclaredMethods()) {
			if (method.getName().equals("setIdAttribute")) {
				return true;
			}
		}
		return false;
	}

	private void tagIdAttributes(Document xmlDoc) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	public void setDestinationUrl(String urld){
		currentUrl = urld;
	}
	
	public String getError() {
		if(error!=null)
			return error.toString();
		return "";
	}

	public String getToken(String response) throws Exception {
		
		Base64 base64 = new Base64();
		byte[] decodedB = base64.decode(response);
		String xml = new String(decodedB);
		String id= xml.substring( xml.indexOf("<saml:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent\">"));		
		id= id.substring(1);
		id= id.substring( 74, id.indexOf("<") );
		
		
		return id;
		
	}
	
	
public String getUserId(String response) throws Exception {
		
		Base64 base64 = new Base64();
		byte[] decodedB = base64.decode(response);
		String xml = new String(decodedB);
		String id= xml.substring( xml.indexOf("<saml:Attribute Name=\"userId\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified\"><saml:AttributeValue xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:anyType\">"));		
	
		id= id.substring(240);
	
		id= id.substring( 0, id.indexOf("<") );
    	
		
		return id;
		
	}


public String getUserId() throws Exception {
	NodeList nodes = xmlDoc.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "userId");
	if (nodes.getLength() == 0) {
		throw new Exception("No name id found in Document.");
	}
	return nodes.item(0).getTextContent();
}
	


/*
private static Element signSamlElement(Element element,PrivateKey privKey,PublicKey pubKey){
	  try {
	    final String providerName=System.getProperty("jsr105Provider",JSR_105_PROVIDER);
	    final XMLSignatureFactory sigFactory=XMLSignatureFactory.getInstance("DOM",(Provider)Class.forName(providerName).newInstance());
	    final List envelopedTransform=Collections.singletonList(sigFactory.newTransform(Transform.ENVELOPED,(TransformParameterSpec)null));
	    final Reference ref=sigFactory.newReference("",sigFactory.newDigestMethod(DigestMethod.SHA1,null),envelopedTransform,null,null);
	    SignatureMethod signatureMethod;
	    if (pubKey instanceof DSAPublicKey) {
	      signatureMethod=sigFactory.newSignatureMethod(SignatureMethod.DSA_SHA1,null);
	    }
	 else     if (pubKey instanceof RSAPublicKey) {
	      signatureMethod=sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1,null);
	    }
	 else {
	      throw new RuntimeException("Error signing SAML element: Unsupported type of key");
	    }
	    final CanonicalizationMethod canonicalizationMethod=sigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,(C14NMethodParameterSpec)null);
	    final SignedInfo signedInfo=sigFactory.newSignedInfo(canonicalizationMethod,signatureMethod,Collections.singletonList(ref));
	    final KeyInfoFactory keyInfoFactory=sigFactory.getKeyInfoFactory();
	    final KeyValue keyValuePair=keyInfoFactory.newKeyValue(pubKey);
	    final KeyInfo keyInfo=keyInfoFactory.newKeyInfo(Collections.singletonList(keyValuePair));
	    org.w3c.dom.Element w3cElement=toDom(element);
	    DOMSignContext dsc=new DOMSignContext(privKey,w3cElement);
	    org.w3c.dom.Node xmlSigInsertionPoint=getXmlSignatureInsertLocation(w3cElement);
	    dsc.setNextSibling(xmlSigInsertionPoint);
	    XMLSignature signature=sigFactory.newXMLSignature(signedInfo,keyInfo);
	    signature.sign(dsc);
	    return toJdom(w3cElement);
	  }
	 catch (  final Exception e) {
	    throw new RuntimeException("Error signing SAML element: " + e.getMessage(),e);
	  }
	}
	*/
}
