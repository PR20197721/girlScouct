package org.girlscouts.vtk.sso.saml;

import org.apache.commons.codec.binary.Base64;
import org.girlscouts.vtk.sso.AccountSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.cert.CertificateException;

public class Response {
    private final AccountSettings accountSettings;
    private final Certificate certificate;
    private Document xmlDoc;

    public Response(AccountSettings accountSettings) throws CertificateException {
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
        this.xmlDoc = Utils.loadXML(decodedS);
    }

    public String getUserId() throws Exception {
        NodeList attributes = xmlDoc.getElementsByTagName("saml:AttributeStatement");
        if (attributes != null) {
            NodeList attributeshildNodes = attributes.item(0).getChildNodes();
            for (int i = 0; i < attributeshildNodes.getLength(); i++) {
                Node node = attributeshildNodes.item(i);
                String name = node.getAttributes().getNamedItem("Name").getNodeValue();
                if ("userId".equals(name)) {
                    return node.getTextContent();
                }
            }
        } else {
            throw new Exception("No saml:AttributeStatement element found in SAML response.");
        }
        return null;
    }

    public String getToken() throws Exception {
        NodeList subject = xmlDoc.getElementsByTagName("saml:Subject");
        if (subject != null) {
            NodeList subjectChildNodes = subject.item(0).getChildNodes();
            for (int i = 0; i < subjectChildNodes.getLength(); i++) {
                Node node = subjectChildNodes.item(i);
                if ("saml:NameID".equals(node.getNodeName())) {
                    return node.getTextContent();
                }
            }
        } else {
            throw new Exception("No saml:Subject element found in SAML response.");
        }
        return null;
    }
}
