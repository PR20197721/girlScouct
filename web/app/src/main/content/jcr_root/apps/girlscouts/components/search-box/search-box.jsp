<%@include file="/libs/foundation/global.jsp" %>

<%
   String action = properties.get("srchaction",String.class);
%>


<form action="<%=action%>.html" method="get">
	<input type="text" name="q" class="searchField" />
</form>