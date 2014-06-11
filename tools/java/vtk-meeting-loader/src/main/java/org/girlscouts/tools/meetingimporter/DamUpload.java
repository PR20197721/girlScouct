package org.girlscouts.tools.meetingimporter;

import javax.jcr.Repository; 
import javax.jcr.Session; 
import javax.jcr.SimpleCredentials; 
import javax.jcr.Node; 
import java.io.FileInputStream;     
import java.io.InputStream;
import java.util.Calendar;
import org.apache.jackrabbit.commons.JcrUtils;
 
//DOM Imports
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
 
public class DamUpload {
 
    /**
     * @param args
     */
    public static void main(String[] args) {
 
try
{
 
 //Create a connection to the CQ repository running on local host 
 Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server");
    
 //Create a Session
 javax.jcr.Session session = repository.login( new SimpleCredentials("admin", "admin".toCharArray()));     
  
//Read the DOM upload XML file from file system
 java.io.File fXmlFile = new java.io.File("/Users/akobovich/caca/DamUpload.xml");
 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
 Document doc = dBuilder.parse(fXmlFile);
     
 //Get the DAM location where files are uploaded to
 String damLocation = getNodeText("DamLocation", doc);
     
 //Get the the location of where the digital assets are located. 
String fileLocation = getNodeText("FileLocation", doc);
     
 NodeList fields = doc.getElementsByTagName("File");
     
 int length = fields.getLength();
 for(int i = 0; i < length; i++)
 {
      org.w3c.dom.Node node = (org.w3c.dom.Node)fields.item(i);
      String fileName = getChildStringValueForElement(node, "name");
              
     //Read the File from the File System
     String myFile = fileLocation+fileName; 
     InputStream stream= new FileInputStream(myFile); 
          
     //Upload the file to the AEM DAM
     writeToDam(stream, fileName,session,damLocation); 
}
  
System.out.println(length +" files have been successfully updated the AEM DAM at "+damLocation);
    
 // log out
session.save(); 
session.logout();
}
catch(Exception e)
{
    e.printStackTrace(); 
 }
}
     
     
     
//Save the digital asset into the AEM DAM
private static String writeToDam(InputStream is, String fileName,javax.jcr.Session session, String damLocation)
{
try
{
    Node node = session.getNode(damLocation);  
    javax.jcr.ValueFactory valueFactory = session.getValueFactory();             
    javax.jcr.Binary contentValue = valueFactory.createBinary(is);            
    Node fileNode = node.addNode(fileName, "nt:file"); 
    fileNode.addMixin("mix:referenceable"); 
    Node resNode = fileNode.addNode("jcr:content", "nt:resource"); 
    resNode.setProperty("jcr:mimeType", "image/jpeg"); 
    resNode.setProperty("jcr:data", contentValue); 
    Calendar lastModified = Calendar.getInstance(); 
    lastModified.setTimeInMillis(lastModified.getTimeInMillis()); 
    resNode.setProperty("jcr:lastModified", lastModified); 
         
    // Return the path to the document that was stored in CQ. 
    return fileNode.getPath();
}
catch(Exception e)
{
    e.printStackTrace();
}
return null; 
}
     
     
 public static String getChildStringValueForElement(org.w3c.dom.Node base, String name)
{
    String value = null;
    NodeList nodeList = base.getChildNodes();
    int length = nodeList.getLength();
    for(int i = 0; i < length; i++)
    {
    org.w3c.dom.Node node = nodeList.item(i);
        if(1 != node.getNodeType() || !name.equals(node.getNodeName()))
                continue;
            org.w3c.dom.Node elNode = node.getFirstChild();
            if(elNode == null)
                continue;
            value = elNode.getNodeValue();
            break;
        }
    return value;
}
     
 
//THis method returns the value of a node in the given XML
public static String getNodeText(String tagName, Document doc)
{
    String res = "";
    NodeList nodes = doc.getElementsByTagName(tagName);
    if(nodes.getLength() > 0)
    {
        org.w3c.dom.Node node = nodes.item(0);
        NodeList children = node.getChildNodes();
        int length = children.getLength();
        for(int i = 0; i < length; i++)
        {
        org.w3c.dom.Node child = children.item(i);
        if(child.getNodeType() == 3)
                res = child.getNodeValue();
        }
    }
return res;
    }
 
}
