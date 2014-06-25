<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>


<!-- apps/girlscouts/components/feature-shortstory/main.jsp -->
<%
	String designPath = currentDesign.getPath();
	String title = properties.get("title","");
	String linkTitle = properties.get("pathfield","");
	String featureIcon = properties.get("./featureiconimage/fileReference", "");
	System.out.println("featureIcon" +featureIcon);
%>

<%if ((title.isEmpty()) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>
    <div style="text-align:center; height:300px;"> 
           <p style="text-align: center"> Click to Edit the Component</p>
    </div>
   <% }else{
%>
<div class="small-24 medium-24 large-24 columns">
	<div class="row">
		<div class="hide-for-small hide-for-medium large-24 columns">
			<div class="feature-icon">
				<img src="<%= featureIcon %>" width="50" height="50" />
			</div>
			<div class="feature-title">
				<h2><a href="<%= linkTitle %>"><%= title %></a></h2>
			</div>
		</div>
		<div class="medium-8 show-for-medium columns">&nbsp;</div>
		<div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
			<div class="feature-icon">
				<img src="<%= designPath %>/images/arrow-down.png" width="30" height="30" />
			</div>
			<div class="feature-title">
				<h2><a href="<%= linkTitle %>"><%= title %></a></h2>
			</div>
		</div>
		<div class="medium-4 show-for-medium columns">&nbsp;</div>
	</div>
	<div class="row content">
		<% if (properties.get("imageOnLeft", "off").equals("on")) { %>
		<cq:include script="image.jsp" />
		<cq:include script="text.jsp" />
		<% } else {%>
		 <cq:include script="text.jsp" />
		<cq:include script="image.jsp" />
		<% } %>
	</div>
</div>
<%}%>