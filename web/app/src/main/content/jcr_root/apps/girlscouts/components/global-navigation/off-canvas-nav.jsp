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
    final String headerNavPath = siteRootPath + "/jcr:content/header/global-nav";  
    NodeIterator headerNavs = null;
    final Node headerNav = resourceResolver.resolve(headerNavPath).adaptTo(Node.class);
    if(headerNav != null && headerNav.hasNode("links")){
    	headerNavs = headerNav.getNode("links").getNodes();    	 			
    }
	StringBuilder sb = new StringBuilder(); 
	
	sb.append("<div class=\"global-navigation global-nav\">");
	    sb.append("<div id=\"right-canvas-menu\">");
			sb.append("<ul class=\"side-nav\" style=\"padding:0px; background-color:#6b6b6b;\">");
			List<String> topMenus = new ArrayList<String>();
		    if(headerNavs != null && headerNavs.getSize() > 0){
		    	while(headerNavs.hasNext()){
		    		Node nav = headerNavs.nextNode();    					
					buildTopMenu(nav, currentPage.getPath(), resourceResolver, sb, topMenus);
		    	}
		    }
		    sb.append("</ul>");
		sb.append("</div>");
	sb.append("</div>");
%>

<%= sb.toString() %>


    

<%!
	public void buildTopMenu(Node nav, String currentPath, ResourceResolver rr, StringBuilder sb, List<String> topMenus) {
		try{
			String largeLabel = nav.hasProperty("large") ? nav.getProperty("large").getString() : "";
			String mediumLabel = nav.hasProperty("medium") ? nav.getProperty("medium").getString() : "";
			String smallLabel = nav.hasProperty("small") ? nav.getProperty("small").getString() : "";		
			String path = nav.hasProperty("url") ? nav.getProperty("url").getString() : "";			
			String label = smallLabel;
			if(label == null || label.isEmpty()){
				if(!mediumLabel.isEmpty()){
					label = mediumLabel;
				}else{
					if(!largeLabel.isEmpty()){
						label = largeLabel;
					}
				}
			}
			boolean active = currentPath.startsWith(rePath(path, 4));
            if (active) {
				sb.append("<li id=\"sub-active\">");
			}else{
				sb.append("<li>");
			}
			if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) {
		        sb.append("<div><a x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\">" + label + "</a></div>");
		    } else {
		        sb.append("<div><a href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a></div>");
		    }
			if (active) {
				String topMenuPath = getTopMenuPath(path);		
				if(!topMenus.contains(topMenuPath)){
					topMenus.add(topMenuPath);
					Page topMenuPathPage = rr.resolve(topMenuPath).adaptTo(Page.class);
		            buildMenu(topMenuPathPage, currentPath, sb);
				}	            
			}
			sb.append("</li>");  
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getTopMenuPath(String path){
	    String result="";
	    if(path != null){
			String[] array = path.split("/");
	        if(array.length <= 5){
	        	result = path;
	        }else{
	        	StringBuffer sb = new StringBuffer();
	        	for(int i = 0; i<=4; i++){
                    if(i>0){
                    	sb.append("/");
                    }
	        		sb.append(array[i]);
	        	}
	        	result=sb.toString();
	        }
	    }
	    return result;
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
                sb.append("<div><a href=\"" + page.getPath() + ".html\">");
                sb.append(title);
                sb.append("</a></div>");

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
