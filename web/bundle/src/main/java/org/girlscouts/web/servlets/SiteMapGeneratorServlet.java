package org.girlscouts.web.servlets;

import java.io.IOException;
import java.util.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.*;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.*;
import org.apache.sling.api.resource.ResourceResolver;
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
  Page pageObj = pageManager.getContainingPage(slingRequest.getResource());
  
  XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
  try {
   XMLStreamWriter stream = outputFactory.createXMLStreamWriter(slingResponse.getWriter());
   
   stream.writeStartDocument("1.0");
   stream.writeStartElement("", "urlset", SITEMAP_NAMESPACE);
   stream.writeNamespace("", SITEMAP_NAMESPACE);
   
   // Current page
   writeXML(pageObj, stream, slingRequest);
   
   for (Iterator<Page> children = pageObj.listChildren(new PageFilter(), true); children.hasNext();) {
    Page childPage = (Page) children.next();
    // If condition added to make sure the pages hidden in search in page properties do not show up in sitemap
    if (null != childPage) {
     if (!childPage.getProperties().containsKey("hideInSearch")
       || (childPage.getProperties().containsKey("hideInSearch")
         && childPage.getProperties().get("hideInSearch").equals("false"))
       || (childPage.getProperties().containsKey("hideInSearch")
         && childPage.getProperties().get("hideInSearch").equals("")))
      writeXML(childPage, stream, slingRequest);
    }
   }
   
   stream.writeEndElement();
   stream.writeEndDocument();
   
  } catch (XMLStreamException e) {
   throw new IOException(e);
  }
 }
 
 private void writeXML(Page pageObj, XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest)
   throws XMLStreamException {
  xmlStream.writeStartElement(SITEMAP_NAMESPACE, "url");
  
  String protocolPort = "http";
  if (slingRequest.isSecure())
   protocolPort = "https";
  
  String locPath = this.externalizer.absoluteLink(slingRequest, protocolPort,
    String.format("%s.html", pageObj.getPath()));
  
  writeXMLElement(xmlStream, "loc", locPath);
  
  if (this.incLastModified) {
   Calendar calendarObj = pageObj.getLastModified();
   if (null != calendarObj) {
    writeXMLElement(xmlStream, "lastmod", DATE_FORMAT.format(calendarObj));
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
