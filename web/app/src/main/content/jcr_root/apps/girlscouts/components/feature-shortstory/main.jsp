<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/main.jsp -->
<%
	String designPath = currentDesign.getPath();
	String title = properties.get("title","");
	//use genlink to get href of cq:page
	String linkTitle = genLink(resourceResolver, properties.get("pathfield",""));
	String featureIcon = properties.get("./featureiconimage/fileReference", "");

%>

<%if ((title.isEmpty()) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>
    <div style="text-align:center; height:300px;"> 
      <p style="text-align: center"> Click to Edit the Component</p>
    </div>
   <% }else{
%>

<div class="large-1 columns small-2 medium-1">
  <img src="<%= featureIcon %>" width="32" height="32" alt="feature icon"/>
</div>

<div class="column large-23 small-22 medium-23">
  <div class="row collapse">
  <h2 class="columns large-24 medium-24"><a href="<%= linkTitle %>"><%= title %></a></h2>
	<% /*for images to display left and right but on small always on top of text.*/%>
		<% if (properties.get("imageOnLeft", false)) { %>
		<div class="large-11 medium-11 small-22 column">
			<cq:include script="image.jsp" />
		</div>
		 <div class="large-11 medium-11 small-22 small-pull-2 column large-pull-1 medium-pull-1"><cq:include script="text.jsp" /></div>
		<% } else {%>
		<div class="large-11 medium-11 large-push-12 medium-push-12 small-22 column"><cq:include script="image.jsp" /></div>
		<div class="large-11 small-22 medium-11 large-pull-13 medium-pull-13 small-pull-2 column"><cq:include script="text.jsp" /></div>	
		<% } %>

	</div>
</div>
<%}%>
