<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String name = properties.get("name", "");
	String url = properties.get("url", "");
	String meetingid = properties.get("meetingid", "");
	String tag = properties.get("tag", "");
	String flag = "VIDEO";

//if ( "".equals(name) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
##### VTK Video Meeting Adis #####
<div class="vtk-video-container"><p>
        Name: <%=name %><br>
        URL: <%=url %><br>
        ID: <%=meetingid %><br>
        Tag: <%=tag %>
        </p></div>


