<%@include file="/libs/foundation/global.jsp" %>
<%
	String title = properties.get("title","");
    String subtitle = properties.get("subtitle","");
    String level = properties.get("level","");
%>

##### This Title and Subtitle Applies to <%=level %> Level #####
<div>
  <p>
   Year Plan Library Title : <%=title %><br>
  Year Plan Library Subtitle : <%=subtitle %><br>

  </p>
</div>