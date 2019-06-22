<%@page import="java.lang.StringBuilder,
				java.util.*,
				com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="apps.gsusa.components.header-nav" />
<%
if(currentNode.hasNode("navs")){
	Node navs = currentNode.getNode("navs");
    NodeIterator iter = navs.getNodes();
    List<String> labels = new ArrayList<String>();
    List<String> mediumLabels = new ArrayList<String>();
    List<String> smallLabels = new ArrayList<String>();
    List<String> links = new ArrayList<String>();
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
	StringBuilder sb = new StringBuilder();	
	request.setAttribute("fromHeaderNav", "true");
	%>
	<nav class="top-bar show-for-medium-up columns small-24" data-topbar role="navigation">
		<section class="top-bar-section">
			<ul>
			<%
		    while(iter.hasNext()){
		    	try{
					Node linkNode = iter.nextNode();
					String largeLabel = linkNode.hasProperty("large-label") ? linkNode.getProperty("large-label").getString() : "";
					String mediumLabel = linkNode.hasProperty("medium-label") ? linkNode.getProperty("medium-label").getString() : "";
					String smallLabel = linkNode.hasProperty("small-label") ? linkNode.getProperty("small-label").getString() : "";
					String clazz = linkNode.hasProperty("class") ? linkNode.getProperty("class").getString() : "";
					String path = linkNode.hasProperty("path") ? linkNode.getProperty("path").getString() : "";
					Boolean hideInDesktop = linkNode.hasProperty("hide-in-desktop") ? linkNode.getProperty("hide-in-desktop").getBoolean() : false;
					Boolean hideInMobile = linkNode.hasProperty("hide-in-mobile") ? linkNode.getProperty("hide-in-mobile").getBoolean() : false;
					Boolean rootLandingPage = linkNode.hasProperty("root-landing-page") ? linkNode.getProperty("root-landing-page").getBoolean() : false;
					Boolean newWindow = linkNode.hasProperty("new-window") ? linkNode.getProperty("new-window").getBoolean() : false;
					Iterator <Page> slingResourceIter;
                    String slingResourceType = "girlscouts/components/placeholder-page";
            		String contentResourceType="";
                    try {
                        contentResourceType = resource.getResourceResolver().getResource(path+"/jcr:content").getResourceType();
                        if(contentResourceType.equals(slingResourceType)){
                            slingResourceIter = resource.getResourceResolver().getResource(path).adaptTo(Page.class).listChildren();
                            if(slingResourceIter.hasNext()){
                                Page firstChild =  slingResourceIter.next();
                                path = genLink(resourceResolver, firstChild.getPath());
                            }
                        }
                    }catch(Exception e){
						log.error("Unable to create placeholder page downlink for GSUSA header-nav component. " +
                                  "Due to exception: " + e.getClass().getName() + " with message: " + e.getMessage());
                    }
					
					mediumLabel = mediumLabel.isEmpty() ? largeLabel : mediumLabel;
					
					String target = newWindow ? "target=\"_blank\"" : "target=\"_self\"";
					long headerNavTabindex = 40 + iter.getPosition();
					String topPath = rePath(path,4);
					String activeClass = "";
					Page linkPage = resourceResolver.resolve(path).adaptTo(Page.class);
					if(!resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						if (linkPage != null && !path.contains(".html")) {
							path += ".html";
						}
						if (currentPage.getPath().startsWith(topPath) && currentPage.getPath().contains(topPath)) {
							activeClass = "active";					
						}
					}
					if (!largeLabel.isEmpty() && !hideInDesktop) {
						if (activeClass.equals("")) { %>
							<li id="tag_topnav_<%= linkifyString(largeLabel, 25)%>">
						<% } else {%>
							<li id="tag_topnav_<%= linkifyString(largeLabel, 25)%>" class="<%=activeClass%>">
						<%}
						if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) { %>
		                   <a <%= target %> x-cq-linkchecker="skip" class="show-for-large-up" href="<%= path %>" title="<%= largeLabel %>" tabindex="<%= headerNavTabindex %>"><%= largeLabel %></a>
		                   <a <%= target %> x-cq-linkchecker="skip" class="show-for-medium-only" href="<%= path %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
		                <% } else { %>
		                   <a <%= target %> class="show-for-large-up" href="<%= path %>" title="<%= largeLabel %>" tabindex="<%= headerNavTabindex %>"><%= largeLabel %></a>
		                   <a <%= target %> class="show-for-medium-only" href="<%= path %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
		                <% } %>
					  </li>
					<%
					}
		    	}catch(Exception e){
		    		log.error("Creation of component GSUSA header-nav failed. " +
                                  "Due to exception: " + e.getClass().getName() + " with message: " + e.getMessage());
		    	}
		    }
			%>
			</ul>
		</section>
		<%= sb.toString() %>
	</nav>
	<!-- OFF CANVAS MENU BAR -->
	<section id="mobileSearch" style="display: none;">
        <cq:include path="<%= headerPath + "/search" %>" resourceType="gsusa/components/search-box" />
    </section>
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

                Resource logo = resourceResolver.resolve(logoPath);
                stickyImgPath = ((ValueMap)logo.getChild("stickyNavImage").adaptTo(ValueMap.class)).get("fileReference", "");
                %>
                <div id="iconContainer">
                <span id="sideMenuIcon"class="mobileIcons">
                    <a class="side-nav-toggle menu-icon" role="button"><span></span></a>
                </span>

                <div class="logo">
        		    <img class="sticky-nav-GS-logo" src="<%= stickyImgPath %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" aria-label="<%=imgAlt%>"  />
        		</div>
        		<section id="mobileSearchSection"class="search-section">
                    <cq:include script="/apps/gsusa/components/search-box/mobile.jsp" />
                </section>
                </div>

        		<%

            }
            if(currentNode.hasNode("navs")){
            	Node navss= currentNode.getNode("navs");
                NodeIterator iterr = navss.getNodes();
            %><section class="top-bar-section mobile">
            			<ul>
            			<%
            		    while(iterr.hasNext()){
            		    	try{
            					Node linkNode = iterr.nextNode();
            					String smallLabel = linkNode.hasProperty("small-label") ? linkNode.getProperty("small-label").getString() : "";
            					String clazz = linkNode.hasProperty("class") ? linkNode.getProperty("class").getString() : "";
            					String path = linkNode.hasProperty("path") ? linkNode.getProperty("path").getString() : "";
            					Boolean hideInDesktop = linkNode.hasProperty("hide-in-desktop") ? linkNode.getProperty("hide-in-desktop").getBoolean() : false;
            					Boolean hideInMobile = linkNode.hasProperty("hide-in-mobile") ? linkNode.getProperty("hide-in-mobile").getBoolean() : false;
            					Boolean rootLandingPage = linkNode.hasProperty("root-landing-page") ? linkNode.getProperty("root-landing-page").getBoolean() : false;
            					Boolean newWindow = linkNode.hasProperty("new-window") ? linkNode.getProperty("new-window").getBoolean() : false;
            					Iterator <Page> slingResourceIter;
                                String slingResourceType = "girlscouts/components/placeholder-page";
                        		String contentResourceType="";
                                try {
                                    contentResourceType = resource.getResourceResolver().getResource(path+"/jcr:content").getResourceType();
                                    if(contentResourceType.equals(slingResourceType)){
                                        slingResourceIter = resource.getResourceResolver().getResource(path).adaptTo(Page.class).listChildren();
                                        if(slingResourceIter.hasNext()){
                                            Page firstChild =  slingResourceIter.next();
                                            path = genLink(resourceResolver, firstChild.getPath());
                                        }
                                    }
                                }catch(Exception e){
            						log.error("Unable to create placeholder page downlink for GSUSA header-nav component. " +
                                              "Due to exception: " + e.getClass().getName() + " with message: " + e.getMessage());
                                }


            					String target = newWindow ? "target=\"_blank\"" : "target=\"_self\"";
            					long headerNavTabindex = 40 + iter.getPosition();
            					String topPath = rePath(path,4);
            					String activeClass = "";
            					Page linkPage = resourceResolver.resolve(path).adaptTo(Page.class);
            					if(!resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            						if (linkPage != null && !path.contains(".html")) {
            							path += ".html";
            						}
            						if (currentPage.getPath().startsWith(topPath)) {
            							activeClass = "active";
            						}
            					}
            					if (!smallLabel.isEmpty() && !hideInMobile) {
            						if (activeClass.equals("")) { %>
            							<li id="tag_topnav_<%= linkifyString(smallLabel, 25)%>">
            						<% } else {%>
            							<li id="tag_topnav_<%= linkifyString(smallLabel, 25)%>" class="<%=activeClass%>">
            						<%
            						}
            						if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) { %>
            		                   <a <%= target %> x-cq-linkchecker="skip" class="show-for-small-only" href="<%= path %>" title="<%= smallLabel %>" tabindex="<%= headerNavTabindex %>"><%= smallLabel %></a>
            		                <% } else { %>
            		                   <a <%= target %> class="show-for-small-only" href="<%= path %>" title="<%= smallLabel %>" tabindex="<%= headerNavTabindex %>"><%= smallLabel %></a>
            		                <% }
            		                %>
            					  </li>
            					<%
            					}
            		    	}catch(Exception e){
            		    		log.error("Creation of component GSUSA header-nav failed. " +
                                              "Due to exception: " + e.getClass().getName() + " with message: " + e.getMessage());
            		    	}
            		    }
            			%>
            			</ul>
            		</section><%
            		}

        } catch(Exception e) {
			log.error("GSUSA header-nav component mobile version image rendition failed." +
                                  "Due to exception: " + e.getClass().getName() + " with message: " + e.getMessage());
        }
        %>
        <cq:include path="content/middle/pdf-print" resourceType="girlscouts/components/pdf-print" />
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
