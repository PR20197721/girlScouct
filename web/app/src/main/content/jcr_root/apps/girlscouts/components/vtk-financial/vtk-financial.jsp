<%@include file="/libs/foundation/global.jsp" %>
<%
	String finsum1 = properties.get("finsum1","");
    String finsum2 = properties.get("finsum2","");
	String reviewandpublish1 = properties.get("reviewandpublish1","");
	String reviewandpublish2 = properties.get("reviewandpublish2","");


	String troopbankinfo = properties.get("troopbankinfo","");
	String incomeexpenseinst = properties.get("incomeexpenseinst","");
	String messageabout = properties.get("messageabout","");
	String tlpreviewinst = properties.get("tlpreviewinst","");

%>

##### VTK Financial Tab Settings #####
<div>
  <p></p>
  <p>For Council Admin Template Page :</p>
  <p>
    Financial Summary Line 1 : <%=finsum1 %><br>
    Notes and Questions : <%=finsum2 %><br>
    Review & Publish Line 1 : <%=reviewandpublish1 %><br>
    Review & Publish Line 2 : <%=reviewandpublish2 %><br>
  </p>
  <p> --------------------------------------------------------------</P>
  <p>For Troop Leader Report Page :</p>
  <p>
      Troop Bank Info : <%=troopbankinfo %><br>
      Income & Expense Instruction : <%=incomeexpenseinst %><br>
      Message about parent view : <%=messageabout %><br> 
      TL preview instruction :  <%=tlpreviewinst %><br> 
  </p>
</div>