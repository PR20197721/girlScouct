<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>



<h1>Troop Photo</h1>

<%

	String troopId= troop.getTroop().getTroopId();
	if( troopId ==null || troopId.trim().equals("") ){
	  
		%>
			<span class="error">Warning: no troop is specified.</span>
		<% 
		return;
	}
%>
<b>Troop:<%=troop.getTroop().getTroopName()%></b>

<form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  
              		id="frmImg"	name="frmImg"   enctype="multipart/form-data">
              		
	<input type="hidden" name="troopId"  value="<%=troopId%>"/>       		
	<input type="file"   name="upldTroopPic" value="" />              
	<input type="submit" value="Upload Photo"  />
	
</form> 


<img src="/content/dam/girlscouts-vtk/troops/<%=troopId %>/imgLib/troop_pic.png/troop_pic.png"/>