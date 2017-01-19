<%@page session="false"%><%--

  ADOBE CONFIDENTIAL
  __________________

   Copyright 2015 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.

--%><%@ page import="java.io.Writer,
            java.util.Calendar,
            java.util.Collections,
            java.util.Iterator,
            java.util.Locale,
            javax.jcr.Node,
            com.adobe.granite.xss.JSONUtil,
            com.adobe.granite.xss.XSSFilter,
            com.day.cq.commons.date.RelativeTimeFormat,
            com.day.cq.i18n.I18n,
            com.day.cq.tagging.Tag,
            com.day.cq.tagging.TagManager,
            com.day.text.Text,
            org.apache.sling.api.resource.ValueMap,
            org.apache.sling.commons.json.io.JSONWriter,
            org.apache.sling.jcr.api.SlingRepository" %><%
%><%@include file="/libs/wcm/global.jsp"%><%

    XSSFilter xssFilter = sling.getService(XSSFilter.class);
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");

    I18n i18n = new I18n(slingRequest);
    String tagPath = request.getParameter("tagPath");
    Resource tagResource = resourceResolver.getResource(tagPath);

    if (tagResource == null) return;
    
    Tag tag = tagResource.adaptTo(Tag.class);
    if (tag == null) return;
    
    Locale locale = request.getLocale();
    RelativeTimeFormat rtf = new RelativeTimeFormat("r", slingRequest.getResourceBundle(locale));

    long tagLastModification = tag.getLastModified();
    ValueMap vm = tagResource.adaptTo(ValueMap.class);
    if (tagLastModification == 0) {
        Calendar created = vm.get("jcr:created", Calendar.class);
        tagLastModification = (null != created) ? created.getTimeInMillis() : 0;
    }
    String lastModified = i18n.getVar(rtf.format(tagLastModification, true));
    String lastModifiedBy = tag.getLastModifiedBy();

    if (lastModifiedBy == null) {
        lastModifiedBy = vm.get("jcr:createdBy", String.class);
    }

    Calendar replicated = vm.get("cq:lastReplicated", Calendar.class);
    long tagLastReplication = (null != replicated) ? replicated.getTimeInMillis() : 0;
    String lastReplicated = i18n.getVar(rtf.format(tagLastReplication, true));

    String lastReplicatedBy = vm.get("cq:lastReplicatedBy", String.class);
    long referenceCount = 0;
    String tagDescription = tag.getDescription();



    JSONWriter writer = new JSONWriter(out);
    writer.object();

    // Write references

    writer.key("taggedItems");
    writer.array();
    Iterator<Resource> nodes = tag.find();
    if (nodes != null && nodes.hasNext()) {
        while (nodes.hasNext()) {
            Resource rKid = nodes.next();
            Node kid = rKid.adaptTo(Node.class);
            String itemPath, title;
            try {
                if (kid.getName().equals("jcr:content")) { // hack
                    kid = kid.getParent();
                }
                title = Text.getName(kid.getPath());
                itemPath = kid.getPath();
            } catch (RepositoryException re) {
                // try next entry
                continue;
            }
            // write tag data
            writer.object();
            JSONUtil.writeProtected(writer, "path", itemPath, xssFilter);
            JSONUtil.writeProtected(writer, "title", title, xssFilter);
            JSONUtil.writeProtected(writer, "itemPath", itemPath, xssFilter);            

            writer.endObject();
            
            referenceCount++;
        }
    }
    writer.endArray();

    JSONUtil.writeProtected(writer, "tagName", tag.getName(), xssFilter);
    JSONUtil.writeProtected(writer, "tagDescription", tagDescription, xssFilter);
    JSONUtil.writeProtected(writer, "lastModified", lastModified, xssFilter);
    JSONUtil.writeProtected(writer, "lastModifiedBy", lastModifiedBy, xssFilter);
    JSONUtil.writeProtected(writer, "lastReplicated", lastReplicated, xssFilter);
    JSONUtil.writeProtected(writer, "lastReplicatedBy", lastReplicatedBy, xssFilter);
    JSONUtil.writeProtected(writer, "referenceCount", referenceCount+"", xssFilter);

    writer.endObject();


%>
