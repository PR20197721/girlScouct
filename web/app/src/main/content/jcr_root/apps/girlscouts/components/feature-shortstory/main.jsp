<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>


<!-- apps/girlscouts/components/feature-shortstory/main.jsp -->
<%
	String designPath = currentDesign.getPath();
	String title = properties.get("title","");
	String linkTitle = properties.get("pathfield","");
	String featureIcon = properties.get("./featureiconimage/fileReference", "");
	
%>

<%if ((title.isEmpty()) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>
    <div style="text-align:center; height:300px;"> 
      <p style="text-align: center"> Click to Edit the Component</p>
    </div>
   <% }else{
%>

<div class="large-2 columns small-4 medium-2">
  <img src="<%= featureIcon %>" width="32" height="32" alt="feature icon"/>
</div>

<div class="column large-22 small-20 medium-22">
  <div class="row collapse">
  <h2 class="columns large-24 medium-24"><a href="<%= linkTitle %>"><%= title %></a></h2>
	<% /*for images to display left and right but on small always on top of text.*/%>
		<% if (properties.get("imageOnLeft", "off").equals("on")) { %>
		<div class="large-11 small-24 medium-11 column">
			<cq:include script="image.jsp" />
		</div>
		 <div class="large-12 medium-12 column"><cq:include script="text.jsp" /></div>
		<% } else {%>
		<div class="large-12 medium-12 large-push-12 medium-push-11 column"><cq:include script="image.jsp" /></div>
		<div class="large-11 small-24 medium-11 large-pull-13 medium-pull-13 column"><cq:include script="text.jsp" /></div>	
		<% } %>

	</div>
</div>
<%}%>
