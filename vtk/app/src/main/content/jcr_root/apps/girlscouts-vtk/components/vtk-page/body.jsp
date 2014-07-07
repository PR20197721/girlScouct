<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.helpers.CouncilMapper" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts/components/page/body.jsp -->
<%
	CouncilMapper mapper = sling.getService(CouncilMapper.class);
	ApiConfig apiConfig = (ApiConfig)request.getSession(true).getAttribute(ApiConfig.class.getName());
	Page oldCurrentPage = null;
	Page newCurrentPage = null;
	if (apiConfig != null) {
	    String councilId = null;
	    if (apiConfig.getTroops().size() > 0) {
	        councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
	    }
	   	String branch = mapper.getCouncilBranch(councilId);
	   	// TODO: language
	   	branch += "/en";
	   	// Backup currentPage
		oldCurrentPage = currentPage;
	   	newCurrentPage = (Page)resourceResolver.resolve(branch).adaptTo(Page.class);
	}
%>
	<body>
		<div class="off-canvas-wrap">
			<div class="inner-wrap">
				<%
					// Override currentPage according to councilId
					if (newCurrentPage != null) {
					    currentPage = newCurrentPage;
					}
				%>
				<cq:include script="header.jsp"/>
				<%
					// Revert currentPage back
					currentPage = oldCurrentPage;
				%>
				<cq:include script="content.jsp"/>
				<cq:include script="footer.jsp"/>
			</div>
		</div>
		<div id="gsModal"></div>
	</body>
