<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String path = properties.get("path","");
if(path.isEmpty() || !path.endsWith(".swf")){
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%	
}else{
	String width = properties.get("width","100%");
	String height = properties.get("height","100%");
	String scale = properties.get("scale","default");
%>

<object id="flashcontent" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="<%= width %>" height="<%= height %>">
    <param name="movie" value="<%= path %>"/>
    <% if(!scale.equals("default")){ %>
  	<param name="scale" value="<%= scale %>" />
  	<% } %>
    <!--[if !IE]>-->
  <object type="application/x-shockwave-flash" data="<%= path %>" width="<%= width %>" height="<%= height %>">
  <% if(!scale.equals("default")){ %>
  <param name="scale" value="<%= scale %>" />
  <% } %>
  <!--<![endif]-->
    <p>Please update your Flash Player</p>
  <!--[if !IE]>-->
  </object>
  <!--<![endif]-->
</object>
<% } %>