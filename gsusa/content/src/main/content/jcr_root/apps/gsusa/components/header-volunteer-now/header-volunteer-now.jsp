<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", String.class);
    String searchBtnName = properties.get("searchBtnName", String.class);
    String title = properties.get("title", String.class);
	Boolean isHidden = properties.get("isVolunteerHidden", false);
	
	if (!isHidden) {%>


  <div>
    <a href="#" title="Volunteer"><%= callToActionName %>></a>
    <form class="formVolunteer">
	  <%= title %>
	  <input type="text" name="ZipVolunteer" maxlength="5">
	  <input class="button" type="submit" value="<%= searchBtnName %>">
    </form>
  </div>


	<%} else {%>
		Click to edit the Volunteer component in the eyebrow
	<%}
	%>