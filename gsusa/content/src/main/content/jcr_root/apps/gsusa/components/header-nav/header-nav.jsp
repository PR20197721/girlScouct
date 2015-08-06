<%@page import="java.util.*, com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
	final String[] navs = properties.get("navs", String[].class);
	List<String> labels = new ArrayList<String>();
	List<String> mediumLabels = new ArrayList<String>();
	List<String> smallLabels = new ArrayList<String>();
	List<String> links = new ArrayList<String>();
	
	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";

	request.setAttribute("fromHeaderNav", "true");
	if (navs != null) {
%>
	<nav class="top-bar show-for-medium-up large-19 medium-23 columns small-24 large-push-5" data-topbar role="navigation">
		<section class="top-bar-section">
			<ul>
<% 
		for (int i = 0; i < navs.length; i++) {
			String[] split = navs[i].split("\\|\\|\\|");
			String label = split.length >= 1 ? split[0] : "";
			String link = split.length >= 2 ? split[1] : "";
			String mediumLabel = split.length >= 4 ? split[3] : label;
			boolean openInNewWindow = false;
			String target = "";
			int headerNavTabindex = 40 + i;
			String activeClass = "";
			
			mediumLabel = mediumLabel.isEmpty() ? label : mediumLabel;
			
			//We are hardcoding openInNewWindow for "For Cookies", "Shop" and Cookies"
			//TODO: Please make it customizable, like the eyebrow-nav components
			if (i == 2 || i == 3 || i == 5) {
				openInNewWindow = true;
				target = "target=\"_blank\"";
			}
			
			Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
			if(!resourceResolver.resolve(link).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {		
				if (linkPage != null && !link.contains(".html")) {
					link += ".html";
				}
				
				if (currentPage.getPath().startsWith(linkPage.getPath() + "/")) {
					activeClass = "active";
				}
			}

			if (!label.isEmpty()) {
				if (activeClass.equals("")) { %>
					<li id="tag_topnav_<%= linkifyString(label, 25)%>">
				<% } else {%>
					<li id="tag_topnav_<%= linkifyString(label, 25)%>" class="<%=activeClass%>">
				<%}
%>
				   <a <%= target %> class="show-for-large-up" href="<%= link %>" title="<%= label %>" tabindex="<%= headerNavTabindex %>"><%= label %></a>
				   <a <%= target %> class="show-for-medium-only" href="<%= link %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
			  </li>
<%
			}
		} 
%>
			</ul>
		</section>
	</nav>


<!-- OFF CANVAS MENU BAR -->
	<nav class="tab-bar hide-for-medium-up">
		<section>
		   <cq:include path="<%= headerPath + "/search" %>" resourceType="gsusa/components/search-box" />
		</section>
		<section class="right-small">
			<a class="right-off-canvas-toggle menu-icon" role="button" href="#"><span></span></a>
		</section>
	</nav>
<!-- END NAV.TAB-BAR HIDE-FOR-LARGE-UP -->

	<!--  OFF CANVAS -->
	<cq:include path="./off-canvas-nav" resourceType="gsusa/components/off-canvas-nav" />
<%
	request.removeAttribute("fromHeaderNav");

	} else if (WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>Double click here to edit header navigation.<%
	}
%>
