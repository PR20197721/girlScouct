<%--

  vtk-data component.

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	String type = properties.get("vtkDataType", "no-supported");
	String script = type + ".jsp";
%>
<cq:include script="<%= script %>" />