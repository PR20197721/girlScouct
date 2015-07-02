<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", String.class);
    String searchBtnName = properties.get("searchBtnName", String.class);
    String title = properties.get("title", String.class);
	Boolean isHidden = properties.get("isJoinHidden", false);
	
	if (!isHidden) {%>


  <div>
    <a href="#" title="Join"><%= callToActionName %>></a>
    <form class="formJoin">
	  <%= title %>
	  <input type="text" name="ZipJoin" maxlength="5">
	  <input class="button" type="submit" value="<%= searchBtnName %>">
    </form>
  </div>


	<%} else {%>
		Click to edit the join component
	<%}
	%>