<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.helpers.CouncilMapper,
                com.day.cq.wcm.api.components.IncludeOptions" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts/components/page/body.jsp -->
<%
        HttpSession session = request.getSession(true);

	CouncilMapper mapper = sling.getService(CouncilMapper.class);
	ApiConfig apiConfig = (ApiConfig)session.getAttribute(ApiConfig.class.getName());
	Page newCurrentPage = null;
	Design newCurrentDesign = null;

	String councilId = null;

	String branch = "";
	try {
	    councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
   		branch = mapper.getCouncilBranch(councilId);
	} catch (Exception e) {
	    String refererCouncil = (String)session.getAttribute("refererCouncil");
	    if (refererCouncil != null && !refererCouncil.isEmpty()) {
	        branch = "/content/" + refererCouncil;
	    } else {
	        branch = mapper.getCouncilBranch();
	    }
	}
	    

   	// TODO: language
   	branch += "/en";
   	newCurrentPage = (Page)resourceResolver.resolve(branch).adaptTo(Page.class);
   	
   	// Get design
	   	String designPath = newCurrentPage.getProperties().get("cq:designPath", "");
   	if (!designPath.isEmpty()) {
   	    newCurrentDesign = (Design)resourceResolver.resolve(designPath).adaptTo(Design.class);
   	}
%>
	<body class="vtk-body" data-grid-framework="f4" data-grid-color="darksalmon" data-grid-opacity="0.5" data-grid-zindex="10" data-grid-gutterwidth="10px" data-grid-nbcols="24">
		<div class="off-canvas-wrap">
			<div class="inner-wrap">
				<%
					// Override currentPage and currentDesign according to councilId
					if (newCurrentPage != null) {
						request.setAttribute("newCurrentPage", newCurrentPage);
					}
					if (newCurrentDesign != null) {
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
				<%
					if (newCurrentPage != null) {
						request.setAttribute("newCurrentPage", newCurrentPage);
					}
					if (newCurrentDesign != null) {
						request.setAttribute("newCurrentDesign", newCurrentDesign);
					}
				%>
				<cq:include script="footer.jsp"/>
				<%
					if (newCurrentPage != null) {
					    request.removeAttribute("newCurrentPage");
					}
					if (newCurrentDesign != null) {
					    request.removeAttribute("newCurrentDesign");
					}
				%>
			</div>
		</div>
		 <div id="gsModal"></div>
		 
		 <!--  script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script -->
		 <!--  script src="http://fb.me/react-0.12.1.js"></script -->
		 
		 <!--  script src="http://fb.me/JSXTransformer-0.12.1.js"></script -->
		 <!--  script src="http://fb.me/react-with-addons-0.12.1.js"></script -->
		 
		 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
		 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
		  <script src="/etc/designs/girlscouts-vtk/clientlibs/js/vtk-global.js"></script>
	</body>
