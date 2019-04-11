package org.girlscouts.vtk.sso.saml;

import org.apache.commons.codec.binary.Base64;
import org.girlscouts.vtk.sso.AccountSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        xmlDoc.toString();
        NodeList nodes = xmlDoc. getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "userId");

        if (nodes.getLength() == 0) {
            throw new Exception("No name id found in Document.");
        }
        return nodes.item(0).getTextContent();
    }

    public String getToken() throws Exception {
        NodeList nodes = xmlDoc.get getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "saml:NameID");
        if (nodes.getLength() == 0) {
            throw new Exception("No name id found in Document.");
        }
        return nodes.item(0).getTextContent();
    }
}
