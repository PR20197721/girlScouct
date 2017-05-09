<%@include file="/libs/foundation/global.jsp" %>
<%
	String customizedName = properties.get("customizedName","");
    String linkText = properties.get("linkText","");
%>

Selected Customized Year Plan
<div>
  <p>
   Selected Customized Year Plan Name : <%=customizedName %><br>
  Selected Customized Year Plan Name : <%=linkText %><br>

  </p>
</div>