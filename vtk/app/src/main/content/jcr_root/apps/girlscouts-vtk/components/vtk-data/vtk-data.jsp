<%--

  vtk-data component.

--%><%
%><%@page import="com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%@include file="/libs/foundation/global.jsp"%>
	<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
		<form>
			<input type="button" value="Edit" id="scaffolding-edit" onclick="alert('edit');"></input>
			<input type="button" value="Activate" id="scaffolding-activae" onclick="alert('activate');"></input>
		</form>
<%
		String type = properties.get("vtkDataType", "no-supported");
		String script = type + ".jsp";
		%><cq:include script="<%= script %>" /><%
	}
%>