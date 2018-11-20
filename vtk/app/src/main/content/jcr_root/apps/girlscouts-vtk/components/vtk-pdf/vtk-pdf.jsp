
<%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String footerLine1 = properties.get("footerLine1", "");
	String footerLine2 = properties.get("footerLine2", "");
%>
<div class="vtk-pdf">
		<br/>Footer Line 1: <%=footerLine1 %><br/>
		<br/>Footer Line 2: <%=footerLine2 %><br/>
</div>

