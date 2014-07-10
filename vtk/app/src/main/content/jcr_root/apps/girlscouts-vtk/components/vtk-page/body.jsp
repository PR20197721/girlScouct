<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.helpers.CouncilMapper,
                com.day.cq.wcm.api.components.IncludeOptions" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts/components/page/body.jsp -->
<%
	CouncilMapper mapper = sling.getService(CouncilMapper.class);
	ApiConfig apiConfig = (ApiConfig)request.getSession(true).getAttribute(ApiConfig.class.getName());
	Page newCurrentPage = null;
	Design newCurrentDesign = null;

	String councilId = null;
	if (apiConfig != null) {
	    if (apiConfig.getTroops().size() > 0) {
	        councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
	    }
	}
   	String branch = mapper.getCouncilBranch(councilId);
   	// TODO: language
   	branch += "/en";
   	newCurrentPage = (Page)resourceResolver.resolve(branch).adaptTo(Page.class);
   	
   	// Get design
	   	String designPath = newCurrentPage.getProperties().get("cq:designPath", "");
   	if (!designPath.isEmpty()) {
   	    newCurrentDesign = (Design)resourceResolver.resolve(designPath).adaptTo(Design.class);
   	}
%>
	<body>
		<div class="off-canvas-wrap">
			<div class="inner-wrap">
				<%
					// Override currentPage and currentDesign according to councilId
					if (false){ //newCurrentPage != null) {
						request.setAttribute("newCurrentPage", newCurrentPage);
					}
					if (false) {//newCurrentDesign != null) {
						request.setAttribute("newCurrentDesign", newCurrentDesign);
					}
				%>
				<cq:include script="header.jsp"/>
				<%
					if (newCurrentPage != null) {
					    request.removeAttribute("newCurrentPage");
					}
					if (newCurrentDesign != null) {
					    request.removeAttribute("newCurrentDesign");
					}
				%>
				<cq:include script="content.jsp"/>
				<cq:include script="footer.jsp"/>
			</div>
		</div>
		<div id="gsModal"></div>
	</body>
