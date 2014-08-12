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

  Flash component

--%><%@ page import="com.day.cq.commons.jcr.JcrUtil,
                     com.day.cq.wcm.foundation.Download,
                     com.day.cq.wcm.api.components.DropTarget,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.text.Text,
                     java.net.URLEncoder" %><%
%><%@include file="/libs/foundation/global.jsp"%><%!

    private String getImgUrl(Resource resource, ValueMap properties, String name) {
        String imgUrl = null;
        String imgRef = properties.get("imageReference", null);
        Resource img = resource.getResourceResolver().resolve(resource.getPath()+"/image/jcr:content");
        if (imgRef != null) {
            imgUrl = imgRef;
        } else if (img != null) {
            Node imgNode = img.adaptTo(Node.class);
            String ext = "gif";
            try {
                String mimeType = imgNode.getProperty("jcr:mimeType").getString();
                ext = mimeType.substring(mimeType.indexOf("/") + 1);
            } catch (Exception e) {}
            imgUrl = img.getPath() + ".res/" + name + "." + ext;
        }
        return imgUrl;
    }

%><%

    String name = currentNode.getName();
    String xiUrl = "/etc/clientlibs/foundation/swfobject/swf/expressInstall.swf";
    String flashUrl = currentNode.getPath() + "/file.res/" + name + ".swf";
    String width = properties.get("width", "100%");
    String height = properties.get("height", "160");
    String flashVersion = properties.get("flashVersion", "9.0.0");
    String flashVars = "{backgroundColor:\"" + properties.get("bgColor", "ffffff") + "\"}";
    String menu = properties.get("menu", "");
    menu = "show".equals(menu) ? "true" : "false";
    String params = "{menu:\"" + menu + "\",wmode:\"" + properties.get("wmode", "opaque") + "\"}";

    String[] attrs = properties.get("attrs", "").split(",");
    String jsAttrs = "{";
    for (int i = 0; i < attrs.length; i++) {
        if (attrs[i].indexOf(":") == -1) continue; // skip none name/value pairs
        if (jsAttrs.length() > 1) jsAttrs += ",";
        attrs[i] = attrs[i].replaceAll("\"", ""); // remove all double quotes to avoid unterminated string js errors
        jsAttrs += attrs[i].replace(":", ":\"") + "\""; // set value in quotes
    }
    jsAttrs += "}";

    //drop target css class = dd prefix + name of the drop target in the edit config
    String ddClassName = DropTarget.CSS_CLASS_PREFIX + "flash";

    Download dld = new Download(resource);

    String cls = " class=\"" + ddClassName + (WCMMode.fromRequest(request) == WCMMode.EDIT && !dld.hasContent() ? " cq-flash-placeholder" : "") + "\"";
    String style = WCMMode.fromRequest(request) == WCMMode.EDIT && !dld.hasContent() ? " style=\"width:100%\"" : "";

    String imgUrl = getImgUrl(resource, properties, name);
    if (imgUrl != null) {
        imgUrl = URLEncoder.encode(imgUrl, "UTF-8");
        imgUrl = imgUrl.replaceAll("%2F", "/");
        String styleWidth = width.endsWith("%") ? width : width + "px";
        String styleHeight = height.endsWith("%") ? height : height + "px";
        style = " style=\"width:" + styleWidth + ";height:" + styleHeight + ";background:transparent url('" + request.getContextPath() + imgUrl + "') no-repeat center center;\"";
    }

    String id = JcrUtil.createValidName(resource.getPath()) + "_swf";

    if (dld.hasContent()) {
        cls = ""; // remove empty representation
        String href = dld.getHref();
        if ("".equals(href)) {
            href = flashUrl;
        }
        href = Text.escape(href, '%', true);
        %>
        <div <%= cls %><%= style %>>
                <div id="<%= id %>">
                    <object type="application/x-shockwave-flash" data="<%= request.getContextPath() + href %>" width="<%= width %>" height="<%= height %>" id="<%= id %>" style="outline: none; visibility: visible;">
                                <param name="menu" value="false">
                                <param name="wmode" value="opaque">
                                <param name="flashvars" value="backgroundColor=ffffff">
                        </object>
            </div>
        </div>
<%
    }
%>
