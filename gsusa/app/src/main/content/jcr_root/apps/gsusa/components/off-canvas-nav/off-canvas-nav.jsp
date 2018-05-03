<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                java.util.List,
                java.util.ArrayList,
                javax.jcr.Value,
                org.apache.sling.api.resource.ResourceResolver,
                com.day.cq.wcm.api.Page " %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    final String siteRootPath = currentPage.getAbsoluteParent(2).getPath();
    final String headerNavPath = siteRootPath + "/jcr:content/header/header-nav";
    final String eyebrowNavPath = siteRootPath + "/jcr:content/header/eyebrow-nav";    
    NodeIterator headerNavs = null;
    NodeIterator eyebrowNavs = null;
    final Node headerNav = resourceResolver.resolve(headerNavPath).adaptTo(Node.class);
    if(headerNav != null && headerNav.hasNode("navs")){
    	headerNavs = headerNav.getNode("navs").getNodes();    	 			
    }
    final Node eyebrowNav = resourceResolver.resolve(eyebrowNavPath).adaptTo(Node.class);    	
	if(eyebrowNav != null && eyebrowNav.hasNode("navs")){
		eyebrowNavs = eyebrowNav.getNode("navs").getNodes();
	}
	StringBuilder sb = new StringBuilder();
    sb.append("<nav class=\"right-off-canvas-menu\" tabindex=\"-1\">");
	sb.append("<ul class=\"off-canvas-list\">");
    if(headerNavs != null && headerNavs.getSize() > 0){
    	while(headerNavs.hasNext()){
    		Node nav = headerNavs.nextNode();    		
			Boolean hideInMobile = nav.hasProperty("hide-in-mobile") ? nav.getProperty("hide-in-mobile").getBoolean() : false;			
			if(!hideInMobile) {				
				buildTopMenu(nav, currentPage.getPath(), resourceResolver, sb);
			}
    	}
    }
	if(eyebrowNavs != null && eyebrowNavs.getSize() > 0){
		while(eyebrowNavs.hasNext()){
			Node nav = eyebrowNavs.nextNode();    
			buildTopMenu(nav, currentPage.getPath(), resourceResolver, sb);
    	}
    }    
    	sb.append("</ul>");
	sb.append("</nav>");
%>

<%= sb.toString() %>


    

<%!
	public void buildTopMenu(Node nav, String currentPath, ResourceResolver rr, StringBuilder sb) {
		try{
			String label = nav.hasProperty("label") ? nav.getProperty("label").getString() : "";
			String largeLabel = nav.hasProperty("large-label") ? nav.getProperty("large-label").getString() : "";
			String mediumLabel = nav.hasProperty("medium-label") ? nav.getProperty("medium-label").getString() : "";
			String smallLabel = nav.hasProperty("small-label") ? nav.getProperty("small-label").getString() : "";		
			String path = nav.hasProperty("path") ? nav.getProperty("path").getString() : "";			
			Boolean rootLandingPage = nav.hasProperty("root-landing-page") ? nav.getProperty("root-landing-page").getBoolean() : false;
			Boolean newWindow = nav.hasProperty("new-window") ? nav.getProperty("new-window").getBoolean() : false;
			if(!smallLabel.isEmpty()){
				label = smallLabel;
			}else{
				if(!mediumLabel.isEmpty()){
					label = mediumLabel;
				}else{
					if(!largeLabel.isEmpty()){
						label = largeLabel;
					}
				}
			}
			String target = newWindow ? "target=\"_blank\"" : "target=\"_self\"";
			boolean active = currentPath.startsWith(path);
            if (active) {
				sb.append("<li class=\"active\" tabindex=\"-1\">");
			}else{
				sb.append("<li tabindex=\"-1\">");
			}
			if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) {
		        sb.append("<a x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\" tabindex=\"-1\" "+target+">" + label + "</a>");
		    } else {
		        sb.append("<a href=\"" + genLink(rr, path) + "\" title=\"" + label + "\" tabindex=\"-1\" "+target+">" + label + "</a>");
		    }
			if (active) {
				String topMenuPath = path.substring(0,path.lastIndexOf("/"));
	            if(isRootLandingPage(path)) {
	        		topMenuPath = path;	// if root page is landing page then no need to get one above
	        	}
	            Page topMenuPathPage = rr.resolve(topMenuPath).adaptTo(Page.class);
	            buildMenu(topMenuPathPage, currentPath, sb);
			}
			sb.append("</li>");  
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isRootLandingPage(String path){
        boolean result=false;
        if(path != null){
			String[] array = path.split("/");
            result = (array.length == 5);
        }
        return result;
	}

    public String genLink(ResourceResolver rr, String link) {
        // This is a Page resource but yet not end with ".html": append ".html"
        if (rr.resolve(link).getResourceType().equals("cq:Page") && !link.contains(".html")) {
            return link + ".html";
        // Well, do nothing
        } else {
            return link;
        }
    }

    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb) {

        Iterator<Page> iter = rootPage.listChildren();

        boolean hasChild = false;
        while(iter.hasNext()) {
            Page page = iter.next();
            if (page.isHideInNav()) {
                continue;
            }
            if (hasChild == false) {
                sb.append("<ul>");
                hasChild = true;
            }
            
            String title = "";//page.getTitle();
            if (page.getNavigationTitle() != null && !"".equals(page.getNavigationTitle())) {
            	title = page.getNavigationTitle();
            } else {
            	title = page.getTitle();
           	}
            if (title != null && !title.isEmpty()) {
                String path = page.getPath();
                boolean isActive = (currentPath + "/").startsWith(path + "/");
                String activeCls = isActive ? "active" : "";
                boolean isCurrent = currentPath.equals(path);
                String currentCls = isCurrent ? " current" : "";
                
                if (isActive || isCurrent) {
                    sb.append("<li class=\"" + activeCls + currentCls + "\">");
                } else {
                    sb.append("<li>");
                }
                sb.append("<a href=\"" + page.getPath() + ".html\">");
                sb.append(title);
                sb.append("</a>");

                if (isActive) {
                    buildMenu(page, currentPath, sb);
                }
                
                sb.append("</li>");
            }
        }
        
        if (hasChild) {
            sb.append("</ul>");
        }
    }
    
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
