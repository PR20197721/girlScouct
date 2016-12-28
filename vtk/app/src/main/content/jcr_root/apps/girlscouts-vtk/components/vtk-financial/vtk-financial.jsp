<%@include file="/libs/foundation/global.jsp" %>
<%
	String finsum1 = properties.get("finsum1","");
    String finsum2 = properties.get("finsum2","");
	String troopbankinfo = properties.get("troopbankinfo","");
	String reviewandpublish1 = properties.get("reviewandpublish1","");
	String reviewandpublish2 = properties.get("reviewandpublish2","");
%>

##### VTK Financial Tab Settings #####
<div>
  <p>
    Financial Summary Line 1: <%=finsum1 %><br>
    Financial Summary Line 2: <%=finsum2 %><br>
    Troop Bank Info: <%=troopbankinfo %><br>
    Review & Publish Line 1: <%=reviewandpublish1 %>
    Review & Publish Line 2: <%=reviewandpublish2 %>
  </p>
</div>