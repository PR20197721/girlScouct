<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.helpers.CouncilMapper,
                com.day.cq.wcm.api.components.IncludeOptions,
                org.apache.sling.settings.SlingSettingsService" %>
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
	    Cookie[] cookies = request.getCookies();
	    String refererCouncil = null;
	    if (cookies != null) {
	    	for (Cookie cookie : cookies) {
	    	    if (cookie.getName().equals("vtk_referer_council")) {
	    	        refererCouncil = cookie.getValue();
	    	    }
	    	}
	    }

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

<!-- Google Tag Manager -->
<noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-PV9D8H"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-PV9D8H');</script>
<!-- End Google Tag Manager -->

		
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
					if( apiConfig.isDemoUser() ){
				%>
					<cq:include script="headerDemo.jsp"/>
				<% 
					}
					else{ 
				%>
					<cq:include script="header.jsp"/>
				<%
					}
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

		 <div id="gsDialog"></div>
		 
		 <!--  script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script -->
		 <!--  script src="http://fb.me/react-0.12.1.js"></script -->
		 
		 <!--  script src="http://fb.me/JSXTransformer-0.12.1.js"></script -->
		 <!--  script src="http://fb.me/react-with-addons-0.12.1.js"></script -->
		 
		 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
		 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
		  <script src="/etc/designs/girlscouts-vtk/clientlibs/js/vtk-global.js"></script>
		  
		  
		  <script>
(function(i,s,o,g,r,a,m){
    i['GoogleAnalyticsObject']=r;
    i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();
    a=s.createElement(o),m=s.getElementsByTagName(o)[0];
    a.async=1;
    a.src=g;
    m.parentNode.insertBefore(a,m)})
    (window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-2646810-36', 'auto', {'name': 'vtkTracker'});
</script>

<%
	boolean isProd = false;
	for (String runMode : sling.getService(SlingSettingsService.class).getRunModes()) {
		if ("prod".equals(runMode)) isProd = true;
	}
	if (isProd) { // begin prod walkme
%>
<script type="text/javascript">(function() {var walkme = document.createElement('script'); walkme.type = 'text/javascript'; walkme.async = true; walkme.src = 'https://cdn.walkme.com/users/b932d7eea0fd45f6bce2b56b3b01f72e/walkme_b932d7eea0fd45f6bce2b56b3b01f72e_https.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(walkme, s); window._walkmeConfig =
{smartLoad:true}
; })();</script>
<%
	} else { // end prod walkme and begin test walkme
%>
<script type="text/javascript">(function() {var walkme = document.createElement('script'); walkme.type = 'text/javascript'; walkme.async = true; walkme.src = 'https://cdn.walkme.com/users/b932d7eea0fd45f6bce2b56b3b01f72e/test/walkme_b932d7eea0fd45f6bce2b56b3b01f72e_https.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(walkme, s); window._walkmeConfig =
{smartLoad:true}
; })();</script>
<%
	} // end test walkme
%>

<% 

String thisFooterScript = (String)request.getAttribute("footerScript") ;
if (thisFooterScript!= null) {
    out.println(thisFooterScript);
}else{
    out.println("");
}
%>
	</body>
