<%@page import="java.lang.StringBuilder,
				java.util.*, 
				com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
	final String[] navs = properties.get("navs", String[].class);
	List<String> labels = new ArrayList<String>();
	List<String> mediumLabels = new ArrayList<String>();
	List<String> smallLabels = new ArrayList<String>();
	List<String> links = new ArrayList<String>();

	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";

	StringBuilder sb = new StringBuilder();
	
	request.setAttribute("fromHeaderNav", "true");
	if (navs != null) {
%>
	<nav class="top-bar show-for-medium-up columns small-24" data-topbar role="navigation">
		<section class="top-bar-section">
			<ul>
<%
		for (int i = 0; i < navs.length; i++) {
			String[] split = navs[i].split("\\|\\|\\|");
			String label = split.length >= 1 ? split[0] : "";
			String link = split.length >= 2 ? split[1] : "";
			String mediumLabel = split.length >= 4 ? split[3] : label;
			boolean hideInDesktop = split.length >= 6 ? Boolean.parseBoolean(split[5]) : false;
			boolean openInNewWindow = false;
			String target = "";
			int headerNavTabindex = 40 + i;
			String activeClass = "";

			mediumLabel = mediumLabel.isEmpty() ? label : mediumLabel;

			//We are hardcoding openInNewWindow for "For Cookies", "Shop" and Cookies"
			//TODO: Please make it customizable, like the eyebrow-nav components
			if (i == 2) {
				openInNewWindow = true;
				target = "target=\"_blank\"";
			}
						
			String topPath = rePath(split[1],4);
						
			Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
			if(!resourceResolver.resolve(link).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				if (linkPage != null && !link.contains(".html")) {
					link += ".html";
				}
				
				// if (currentPage.getPath().startsWith(linkPage.getPath() + "/")) {
				if (currentPage.getPath().startsWith(topPath)) {
					activeClass = "active";					
				}
			}

			if (!label.isEmpty() && !hideInDesktop) {
				if (activeClass.equals("")) { %>
					<li id="tag_topnav_<%= linkifyString(label, 25)%>">
				<% } else {%>
					<li id="tag_topnav_<%= linkifyString(label, 25)%>" class="<%=activeClass%>">
				<%}
%>
                 <% if (link.indexOf("http:") != -1 || link.indexOf("https:") != -1) { %>
                   <a <%= target %> x-cq-linkchecker="skip" class="show-for-large-up" href="<%= link %>" title="<%= label %>" tabindex="<%= headerNavTabindex %>"><%= label %></a>
                   <a <%= target %> x-cq-linkchecker="skip" class="show-for-medium-only" href="<%= link %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
                 <% } else { %>
                   <a <%= target %> class="show-for-large-up" href="<%= link %>" title="<%= label %>" tabindex="<%= headerNavTabindex %>"><%= label %></a>
                   <a <%= target %> class="show-for-medium-only" href="<%= link %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
                 <% } %>
			  </li>
<%
			}
		}
%>
			</ul>
		</section>
		<%= sb.toString() %>
	</nav>


<!-- OFF CANVAS MENU BAR -->
	<nav class="tab-bar hide-for-medium-up"><%
        String imgAlt = properties.get("imageAlt", "");
        String headerNavPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/header-nav";  
        String logoPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/logo";
        Boolean sticky = false;
        try {
            ValueMap headerNavProps = resourceResolver.resolve(headerNavPath).adaptTo(ValueMap.class);
            sticky = headerNavProps.get("displayStickyNav", false);
            if (sticky) {
                String stickyImgPath = "";
                try {
                    Resource logo = resourceResolver.resolve(logoPath);
                    stickyImgPath = ((ValueMap)logo.getChild("stickyNavImage").adaptTo(ValueMap.class)).get("fileReference", "");
                    %><div class="logo">
                        <img class="sticky-nav-GS-logo" src="<%= stickyImgPath %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" aria-label="<%=imgAlt%>"  />
                    </div><%
                } catch (Exception e) {}
            }
        } catch(Exception e) {}
        %><section class="search-section">
		   <cq:include path="<%= headerPath + "/search" %>" resourceType="gsusa/components/search-box" />
		</section>
		<section class="toggle-section right-small">
			<a class="right-off-canvas-toggle menu-icon" role="button" href="#"><span></span></a>
		</section>
	</nav>
    <div class="tab-bar-placeholder"></div>
<!-- END NAV.TAB-BAR HIDE-FOR-LARGE-UP -->

	<!--  OFF CANVAS -->
	<cq:include path="./off-canvas-nav" resourceType="gsusa/components/off-canvas-nav" />
<%
	request.removeAttribute("fromHeaderNav");

	} else if (WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>Double click here to edit header navigation.<%
	}
%>

<%! 

	public String rePath(String path, int level) {
		
		String[] array = path.split("/");
		level++;
		
		if ((level > array.length) || (level <= 0)) {
			level = array.length;
		}
		
		StringBuilder newPath = new StringBuilder();    	
		for (int i = 1; i < level; i++) {    		
			newPath.append("/"+array[i]);	    		
		}  	    	
		
		return newPath.toString();
	}

%>
