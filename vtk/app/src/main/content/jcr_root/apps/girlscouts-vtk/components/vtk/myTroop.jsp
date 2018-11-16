<%@ page import="com.day.text.Text, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, java.io.*, java.net.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<div id="error-message"></div>      
  
<%
  String activeTab = "myTroop";
  boolean showVtkNav = true;
  String sectionClassDefinition="";
%>

<%@include file="include/bodyTop.jsp" %>
 
  <%if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID) ){ %>
    <%@include file="mytroop_react.jsp"%>
  
   <%} else { %>
  
    <%@include file='myTroopImg.jsp' %>
    <%@include file="myTroopOff.jsp"%>
  <% } %>
  
<script>loadNav('myTroop')</script>
<%@include file="include/bodyBottom.jsp" %>


<%!

public boolean isRenewMembership(int membershipYear){
	Calendar rightNow = Calendar.getInstance();
		
	if( membershipYear == rightNow.get(Calendar.YEAR) &&
  		( rightNow.get(Calendar.MONTH) > 2 &&  rightNow.get(Calendar.MONTH) < 9)
  	){
  		return true;
	}
	return false;
}
%>

