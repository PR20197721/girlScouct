<%@include file="/libs/foundation/global.jsp" %>
<%
	String finsum1 = properties.get("finsum1","");
    String finsum2 = properties.get("finsum2","");
	String troopbankinfo1 = properties.get("troopbankinfo1","");
	String troopbankinfo2 = properties.get("troopbankinfo2","");
	String councilnotes1 = properties.get("councilnotes1","");
	String councilnotes2 = properties.get("councilnotes2","");
	String reviewandpublish1 = properties.get("reviewandpublish1","");
	String reviewandpublish2 = properties.get("reviewandpublish2","");
	String setuptip = properties.get("setuptip", "");
%>

##### VTK Financial Tab Settings #####
<div>
  <p>
    Financial Summary Line 1: <%=finsum1 %><br>
    Financial Summary Line 2: <%=finsum2 %><br>
    Troop Bank Info Line 1: <%=troopbankinfo1 %><br>
    Troop Bank Info Line 2: <%=troopbankinfo2 %><br>
    Council Notes And Questions For Troops Line 1: <%=councilnotes1 %><br>
    Council Notes And Questions For Troops Line 2: <%=councilnotes2 %><br>
    Review & Publish Line 1: <%=reviewandpublish1 %>
    Review & Publish Line 2: <%=reviewandpublish2 %>
    Set Up Tip: <%= setuptip %>
  </p>
</div>
