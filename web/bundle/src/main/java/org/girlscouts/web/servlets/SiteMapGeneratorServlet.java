package org.girlscouts.web.servlets;

import java.io.IOException;
import java.util.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.*;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.*;

@Component(metatype = true, label = "Site Map Generator Servlet",
			description = "Create sitemap.xml from homepage template")
@Service(Servlet.class)

@SuppressWarnings("serial")
@Properties({
  @Property(	name = "sling.servlet.resourceTypes", 
		  		unbounded = PropertyUnbounded.ARRAY,
		  		label = "Homepage Resource Type", 
		  		description = "Sling Resource Type for Home Page component", 
		  		value={"/apps/girlscouts/components/homepage","/apps/gsusa/components/homepage"}),
  @Property(name = "sling.servlet.selectors", value = "sitemap"),
  @Property(name = "sling.servlet.extensions", value = "xml"),
  @Property(name = "sling.servlet.methods", value = "GET"),
  @Property(name = "webconsole.configurationFactory.nameHint",
    value = "Site Map on resource types: [{sling.servlet.resourceTypes}]"),
  @Property(name = "include.lastmod", boolValue = true, label = "Include Last Modified Date",
    description = "If checked, last modified value will be shown in sitemap.")
 })
public final class SiteMapGeneratorServlet extends SlingSafeMethodsServlet {
 
 private static final Logger LOG = LoggerFactory.getLogger(SiteMapGeneratorServlet.class);
 private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
 private static final boolean INCLUDE_LAST_MODIFIED_DEFAULT_VALUE = true;
 
 private static final String INCLUDE_LAST_MODIFIED_PROPERTY = "include.lastmod";
 
 private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
 
 @Reference
 private Externalizer externalizer;
 
 private boolean incLastModified;
 
 @Activate
 protected void activate(Map<String, Object> properties) {
  this.incLastModified = PropertiesUtil.toBoolean(properties.get(INCLUDE_LAST_MODIFIED_PROPERTY),
    INCLUDE_LAST_MODIFIED_DEFAULT_VALUE);
 }
 
 @Override
 protected void doGet(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse)
   throws ServletException, IOException {
  
  slingResponse.setContentType(slingRequest.getResponseContentType());
  ResourceResolver resourceResolver = slingRequest.getResourceResolver();
  PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
  Page contPage = pageManager.getContainingPage(slingRequest.getResource());
  
  XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
  try {
   XMLStreamWriter stream = outputFactory.createXMLStreamWriter(slingResponse.getWriter());
   
   stream.writeStartDocument("1.0");
   stream.writeStartElement("", "urlset", SITEMAP_NAMESPACE);
   stream.writeNamespace("", SITEMAP_NAMESPACE);
   
   // Current page
   Resource pageResource = contPage.getContentResource();
   ValueMap pageValueMap = pageResource.getValueMap();
   writeXMLPage(contPage, stream, slingRequest);

   //DAM logic
   String damLoc = pageValueMap.get("damPath", String.class);
   Resource dam = resourceResolver.getResource(damLoc);
   Resource documents = dam.getChild("documents"); //this is most council's location

   //some councils don't use "documents", they use one of these strings
   String[] specificDocLocations = {"forms-and-documents-","forms","documents","GSGCF-Forms-and-Documents",
   "forms-and-documents"};
   for (String specificDocLocation : specificDocLocations){
    if (documents != null){
     break;
    }
    documents = dam.getChild(specificDocLocation);
   }


   if (documents != null) {
    writeDamFolderXML(stream, slingRequest, documents);
   }




   
   for (Iterator<Page> children = contPage.listChildren(new PageFilter(), true); children.hasNext();) {
    Page childPage = (Page) children.next();
    stream.writeComment(childPage.getName().replace('-',' '));
    // If condition added to make sure the pages hidden in search in page properties do not show up in sitemap
    if (null != childPage) {
     if (!childPage.getProperties().containsKey("hideInSearch")
       || (childPage.getProperties().containsKey("hideInSearch")
         && childPage.getProperties().get("hideInSearch").equals("false"))
       || (childPage.getProperties().containsKey("hideInSearch")
         && childPage.getProperties().get("hideInSearch").equals("")))
      writeXMLPage(childPage, stream, slingRequest);
    }
   }
   
   stream.writeEndElement();
   stream.writeEndDocument();
   
  } catch (XMLStreamException e) {
   throw new IOException(e);
  }
 }

 private void writeDamFolderXML(final XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest, Resource folder)
   throws XMLStreamException{
  Iterator<Resource> children = folder.listChildren();
  while (children.hasNext()){
   Resource child = children.next();
   ValueMap childValueMap = child.getValueMap();
   String jcrPrimaryType = childValueMap.get("jcr:primaryType",String.class);
   if (jcrPrimaryType.equalsIgnoreCase("dam:Asset")){
    writeXMLResource(child, xmlStream, slingRequest);
   }
   if (jcrPrimaryType.equalsIgnoreCase("sling:OrderedFolder")){
    writeDamFolderXML(xmlStream, slingRequest, child);
   }
  }
 }

 private void writeXMLResource(Resource resource, XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest)
   throws XMLStreamException{
  /*xmlStream.writeStartElement(SITEMAP_NAMESPACE, "url");

  String protocolPort = "http";
  if (slingRequest.isSecure()) {
   protocolPort = "https";
  }*/

  String path = resource.getPath();
  /*this.externalizer.absoluteLink(slingRequest, protocolPort,
          String.format("%s.html", resource.getPath()));

  writeXMLElement(xmlStream, "loc", locPath);
*/

  Calendar calendarObj = null;
  Resource jcrContent = resource.getChild("jcr:content");
  if (this.incLastModified && null != jcrContent) {
   calendarObj = jcrContent.getValueMap().get("jcr:lastModified", Calendar.class);
  }


  writeXML(path, calendarObj, xmlStream, slingRequest);

 }
 
 private void writeXMLPage(Page pageObj, XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest)
   throws XMLStreamException {
  /*xmlStream.writeStartElement(SITEMAP_NAMESPACE, "url");
  
  String protocolPort = "http";
  if (slingRequest.isSecure()) {
   protocolPort = "https";
  }*/
  
  String path = String.format("%s.html", pageObj.getPath());
  /*this.externalizer.absoluteLink(slingRequest, protocolPort,
    String.format("%s.html", pageObj.getPath()));
  
  writeXMLElement(xmlStream, "loc", locPath);
  */

  Calendar calendarObj = null;
  if (this.incLastModified) {
   calendarObj = pageObj.getLastModified();
  }


  writeXML(path, calendarObj, xmlStream, slingRequest);

 }

 private void writeXML(String path, Calendar lastMod, XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest)
   throws XMLStreamException{
  xmlStream.writeStartElement(SITEMAP_NAMESPACE, "url");

  String protocolPort = "http";
  if (slingRequest.isSecure()) {
   protocolPort = "https";
  }

  String locPath = this.externalizer.absoluteLink(slingRequest, protocolPort, path);

  writeXMLElement(xmlStream, "loc", locPath);

  if (this.incLastModified) {
   if (null != lastMod) {
    writeXMLElement(xmlStream, "lastmod", DATE_FORMAT.format(lastMod));
   }
  }
  xmlStream.writeEndElement();
 }


 
 private void writeXMLElement(final XMLStreamWriter xmlStream, final String elementName, final String xmlText)
   throws XMLStreamException {
  xmlStream.writeStartElement(SITEMAP_NAMESPACE, elementName);
  xmlStream.writeCharacters(xmlText);
  xmlStream.writeEndElement();
 }
 
}
