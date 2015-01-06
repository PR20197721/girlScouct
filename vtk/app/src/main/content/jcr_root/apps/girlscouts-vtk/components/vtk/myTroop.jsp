<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
        String activeTab = "myTroop";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>





<br/><br/><a href="/content/girlscouts-vtk/en/vtk.admin.troopPhoto.html">add/change a photo of your troop</a>

<H3>INFO</H3>
<div class="row">
 <%
 	java.util.List<org.girlscouts.vtk.models.Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
  %>
  
  
  
  	  
<!-- contacts [6 COLS] -->
  <div class="small-24 large-12 columns">
  
  
  	<div class="row"><!-- contact -->
  		<%for(int i=0;i<contacts.size();i++){ 
  			org.girlscouts.vtk.models.Contact contact = contacts.get(i);
  		%>
			  <div class="small-24 large-2 columns">::</div>
		 	  <div class="small-24 large-5 columns"> <%=contact.getFirstName() %> </div>
		 	  <div class="small-24 large-5 columns"> RollXXX </div>
		 	  <div class="small-24 large-2 columns">I</div>
		 	  <div class="small-24 large-5 columns"> <%=contact.getEmail() %> </div>
		 	  <div class="small-24 large-5 columns"> <%=contact.getPhone() %> </div>
		<%}//end for %>
	</div>
	  
  	
  </div>
 
 </div>
 
 
<HR/>
 <H3>VOLUNTEERS</H3>
 <!-- 5 COLS -->
 <div class="row">

<!-- contacts -->
 
 
 
 </div>
 
 
 <HR/>
 <H3>CONTACTS</H3>
  <!-- 5 COLS -->
 <div class="row">

<!-- contacts -->
  <div class="small-24 large-12 columns">
  
	<div class="row"><!-- contact -->
 	  <div class="small-24 large-7 columns"> Name </div>
 	  <div class="small-24 large-5 columns"> Roll </div>
 	  <div class="small-24 large-2 columns">I</div>
 	  <div class="small-24 large-5 columns"> Email </div>
 	  <div class="small-24 large-5 columns"> TEL </div>
	</div>
	<div class="row"><!-- contact -->
 	  <div class="small-24 large-7 columns"> Name </div>
 	  <div class="small-24 large-5 columns"> Roll </div>
 	  <div class="small-24 large-2 columns">I</div>
 	  <div class="small-24 large-5 columns"> Email </div>
 	  <div class="small-24 large-5 columns"> TEL </div>
	</div>
	<div class="row"><!-- contact -->
 	  <div class="small-24 large-7 columns"> Name </div>
 	  <div class="small-24 large-5 columns"> Roll </div>
 	  <div class="small-24 large-2 columns">I</div>
 	  <div class="small-24 large-5 columns"> Email </div>
 	  <div class="small-24 large-5 columns"> TEL </div>
	</div>
	<div class="row"><!-- contact -->
 	  <div class="small-24 large-7 columns"> Name </div>
 	  <div class="small-24 large-5 columns"> Roll </div>
 	  <div class="small-24 large-2 columns">I</div>
 	  <div class="small-24 large-5 columns"> Email </div>
 	  <div class="small-24 large-5 columns"> TEL </div>
	</div>
	<div class="row"><!-- contact -->
 	  <div class="small-24 large-7 columns"> Name </div>
 	  <div class="small-24 large-5 columns"> Roll </div>
 	  <div class="small-24 large-2 columns">I</div>
 	  <div class="small-24 large-5 columns"> Email </div>
 	  <div class="small-24 large-5 columns"> TEL </div>
	</div>
	
 
  </div>
 
 
 </div>
 
 
 <script>
	fixVerticalSizing = true;
</script>
