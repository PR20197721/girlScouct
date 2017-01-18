<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Tag Cloud component

  Draws a tag cloud

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="
        java.util.Properties,
        java.util.Locale,
        org.apache.commons.lang3.StringEscapeUtils,
        org.apache.jackrabbit.util.Text,
        com.day.cq.wcm.api.WCMMode,
        com.day.cq.tagging.Tag,
        com.day.cq.tagging.TagManager,
        com.day.cq.tagging.TagCloud" %><%
%><%

    TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
    String display = properties.get("display", "page");
    
    Locale locale = currentPage.getLanguage(false);
    
    String searchPath = "/";
    
    TagCloud cloud;
    if ("all".equals(display)) {
        // entire repository
        Resource root = resource.getResourceResolver().getResource("/");
        cloud = new TagCloud(tagManager.getTagsForSubtree(root, false), locale);
    } else {
        String path = properties.get("path", String.class);
        Page p = (path != null ? pageManager.getPage(path) : currentPage);
        if (p == null) {
            // not found => empty
            cloud = new TagCloud(new Tag[] {}, locale);
        } else {
            if ("page".equals(display)) {
                // page only
                cloud = new TagCloud(tagManager.getTagsForSubtree(p.getContentResource(), true), locale);
                searchPath = p.getContentResource().getPath();
            } else /* if ("pagetree".equals(display)) */ {
                // subtree of page
                cloud = new TagCloud(tagManager.getTagsForSubtree(p.adaptTo(Resource.class), false), locale);
                searchPath = p.getPath();
            }
        }
    }

if (cloud.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %><img src="/libs/cq/ui/resources/0.gif" class="cq-tagcloud-placeholder" alt=""><%
} else {

    boolean showAllTags = !currentStyle.get("showOnlyLeafTags", false);
    boolean noLinks = properties.get("noLinks", false);
    boolean allTagsSameSize = currentStyle.get("allTagsSameSize", false);
    
    %>
    <style type="text/css">
        .tagcloud {
            text-align:center;
            margin: 0px;
            padding: 0px;
        }
        
        .tagcloud li {
            display: inline;
            text-align: center;
            white-space: nowrap;
            padding-left: 5px;
        }
        
    <%
    if (!allTagsSameSize) {
    %>
        .tagcloud .tag10 { font-size:15pt; }
        .tagcloud .tag9  { font-size:14pt; }
        .tagcloud .tag8  { font-size:13pt; }
        .tagcloud .tag7  { font-size:12pt; }
        .tagcloud .tag6  { font-size:11pt; }
        .tagcloud .tag5  { font-size:10pt; }
        .tagcloud .tag4  { font-size: 9pt; }
        .tagcloud .tag3  { font-size: 8pt; }
        .tagcloud .tag2,
        .tagcloud .tag1,
        .tagcloud .tag0  { display:none; }
    <%
    }
    %>
    </style>
    <%

    %><ul class="tagcloud"><%

    for (Tag tag : cloud.getTags()) {
        long count = tag.getCount();
        if (count > 0 && (showAllTags || !tag.listChildren().hasNext())) {
            int dectil = cloud.calculateNtiles(count, 10);
            String title = currentStyle.get("showTitlePath", false) ? tag.getTitlePath(locale) : tag.getTitle(locale);
            
    %>
          <li class="tag<%= dectil %>" title="<%= count %>"><%
          
            String linkTemplate = currentStyle.get("linkTemplate", "");
            if (!noLinks && linkTemplate != null && linkTemplate.trim().length() > 0) {
                Properties tagProps = new Properties();
                tagProps.setProperty("tagid", tag.getTagID());
                String tagIDForLink = Text.escape(tag.getTagID().replace("/", "__"), '%', true);
                tagProps.setProperty("tagid_link", tagIDForLink);
                tagProps.setProperty("localtagid", tag.getLocalTagID());
                String localTagIDForLink = Text.escape(tag.getLocalTagID().replace("/", "__"), '%', true);
                tagProps.setProperty("localtagid_link", localTagIDForLink);
                tagProps.setProperty("path", tag.getPath());
                tagProps.setProperty("name", tag.getName());
                tagProps.setProperty("title", tag.getTitle(locale));
                tagProps.setProperty("titlepath", tag.getTitlePath(locale));
                tagProps.setProperty("pagepath", currentPage.getPath());
                tagProps.setProperty("searchpath", searchPath);
                
                String link = Text.replaceVariables(tagProps, linkTemplate, true);
                
    %><a class="tag<%= dectil %>" href="<%= link %>"><%= StringEscapeUtils.escapeHtml4(title) %></a><%

            } else {
                
    %><%= StringEscapeUtils.escapeHtml4(title) %><%
    
            }
    %></li><%
        }
    }
    
    %>
    </ul><%
}

%>
