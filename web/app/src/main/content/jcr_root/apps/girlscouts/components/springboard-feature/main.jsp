<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/main.jsp -->
<%
	String designPath = currentDesign.getPath();
	String title = properties.get("title","");
	String linkTitle = properties.get("pathfield","");
	String featureIcon = properties.get("featureiconimage", "");
%>
<div class="small-12 medium-12 large-12 columns">
	<div class="row">
		<div class="small-24 medium-24 large-24 columns text-center">
			<div class="feature-title">
				<h2><a href="<%= linkTitle %>"><%= title %></a></h2>
			</div>
		</div>
	</div>	
		
</div>
