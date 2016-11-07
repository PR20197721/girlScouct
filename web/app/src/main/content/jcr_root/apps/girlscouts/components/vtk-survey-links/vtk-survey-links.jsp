
<%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String meetingid = properties.get("meetingid", "");
	String link = properties.get("surveyLink", "");
	String bannerCopy = properties.get("bannerCopy", "");
	String buttonCopy = properties.get("buttonCopy", "");


%>
<div class="vtk-survey-link">
		<br>Meeting ID: <%=meetingid %><br>
            Banner Copy : <%=bannerCopy %><br>
            Button Copy : <%=buttonCopy %><br>
            Survey Link : <%=link %><br>

</div>

