<%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" %><%
    String date = properties.get("date","");
	String title = properties.get("title","title placeholder");
	String text = properties.get("text","no description");
%><div>
	<p><%=date %> &nbsp;&nbsp;&nbsp;<%=title %></p>
</div>

<div>
    <p><%=text %></p>
</div>

     
