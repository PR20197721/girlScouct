<%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%>
<%@page session="false" contentType="text/html; charset=utf-8"%><%
%><%@page import="com.adobe.granite.ui.components.Config,
                  java.util.Iterator,
                  org.apache.sling.api.resource.Resource,
                  com.adobe.granite.ui.components.AttrBuilder,
                  org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%><%
%><cq:defineObjects /><%
Config cfg = new Config(resource, null, null);
Iterator<Resource> items = cfg.getItems(cfg.get("itemsName"));
AttrBuilder attr = new AttrBuilder(request, xssAPI);
attr.add("id", cfg.get("id"));

attr.addClass(cfg.get("class"));
attr.addOthers(cfg.getProperties(), "id", "class");

%>
<table <%=attr.build() %>>
	<tbody>
		<tr>
			<%
				while(items.hasNext()) {
				    Resource res = items.next();
				    Config itemCfg = new Config(res);
				    AttrBuilder itemAttr = new AttrBuilder(request, xssAPI);
				    itemAttr.add("id", itemCfg.get("id"));
				    itemAttr.add("class", itemCfg.get("class"));
				    String itemResourceType = itemCfg.get("sling:resourceType","granite/ui/components/foundation/contsys");    				
				    %>
				    	<td <%=itemAttr.build() %>>
				    		<sling:include path="<%= res.getPath() %>" resourceType="<%= itemResourceType %>" />
				    	</td>
				    <%
				}
			%>
		</tr>
	</tbody>
</table>