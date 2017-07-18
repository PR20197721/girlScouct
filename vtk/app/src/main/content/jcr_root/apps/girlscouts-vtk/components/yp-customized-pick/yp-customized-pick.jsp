<%@include file="/libs/foundation/global.jsp" %>
<%
	String customizedName = properties.get("title","");
    String linkText = properties.get("linkText","");
%>

Selected Customized Year Plan
<div>
  <p>
   Name of the Customized Year Plan : <%=customizedName %><br>
   Link Text of the Customized Year Plan  : <%=linkText %><br>

  </p>
</div>
