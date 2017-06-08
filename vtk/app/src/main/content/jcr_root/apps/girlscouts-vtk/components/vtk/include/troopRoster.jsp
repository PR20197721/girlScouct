<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="session.jsp"%>

<%
java.util.List<org.girlscouts.vtk.models.Contact> contacts =
	contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");

%>

<div class="row">
  <div class="small-12 large-12 columns"><%=troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) %> <%=troop.getSfTroopName()%></div>
</div>

<div class="row">
  <div class="small-12 large-12 columns"><%=VtkUtil.formatDate( VtkUtil.FORMAT_MMM_dd_yyyy, new java.util.Date())%></div>
</div>

<div class="row">
  <div class="small-3 large-3 columns"><%=contacts==null ? "" : contacts.size()%> GIRLS</div>
  <div class="small-9 large-9 columns"></div>
</div>

<div class="row">
  <div class="small-3 large-3 columns">GIRL SCOUT</div>
  <div class="small-3 large-3 columns">PARENT GUARDIAN</div>
  <div class="small-3 large-3 columns">PARENT EMAIL</div>
  <div class="small-3 large-3 columns">PARENT PHONE</div>
</div>


<% 
  if( contacts!=null)
   for(int contact =0 ;contact< contacts.size(); contact++){
   	Contact _contact = contacts.get(contact);
   	if( ! "Girl".equals( _contact.getRole() ) ) continue;
   	
     Contact caregiver = VtkUtil.getSubContact( _contact, 1);
     	
   	//check permission again:must be TL
   	if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
              user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }
   
      
      
    %>
	<div class="row">
	  <div class="small-3 large-3 columns"><%=_contact.getFirstName() %> <%=_contact.getRole() %></div>
	  <div class="small-3 large-3 columns"><%= caregiver==null ? "" : ((caregiver.getFirstName()==null ? "" : caregiver.getFirstName()) +" "+ (caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ))%></div>
	  <div class="small-3 large-3 columns"><%=_contact.getEmail() %></div>
	  <div class="small-3 large-3 columns"><%=_contact.getPhone() ==null ? "" : _contact.getPhone()%></div>
	</div>
   <%}%>