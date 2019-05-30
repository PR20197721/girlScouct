<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                java.util.List,
                java.util.ArrayList,
                com.day.cq.wcm.api.PageFilter,
                javax.jcr.Value,
                org.apache.sling.api.resource.ResourceResolver,
                com.day.cq.wcm.api.Page " %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    final String siteRootPath = currentPage.getAbsoluteParent(2).getPath();
    final String headerNavPath = siteRootPath + "/jcr:content/header/global-nav";
    Page homepage = currentPage.getAbsoluteParent(2);
    String homepagePath = homepage.getPath();
    String searchPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/search-box";
    NodeIterator headerNavs = null;
    final Node headerNav = resourceResolver.resolve(headerNavPath).adaptTo(Node.class);
    if(headerNav != null && headerNav.hasNode("links")){
    	headerNavs = headerNav.getNode("links").getNodes();
    }
	StringBuilder sb = new StringBuilder();

	sb.append("<div class=\"global-navigation global-nav\">");
	    sb.append("<div id=\"left-canvas-menu\">");
			sb.append("<ul class=\"side-nav\" style=\"padding:0px; background-color:#e6e7e8;\">");
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
<div id="left-canvas-menu" class="mobileSearch">
<ul class="side-nav search-bar" >
    <li style="background: none; border-right: none;">
        <div class="side-nav-header"><%
        if(homepagePath.equals(currentPage.getPath())){%>
             <button id="searchBtnHome"class="searchHome"  onclick="location.href='<%= homepagePath %>.html'"type="side-nav-button"><span class="mobile-search-btn-text">HOME</span></button>
        <%}else{%>
             <button id="searchBtn" onclick="location.href='<%= homepagePath %>.html'"type="side-nav-button"><span class="mobile-search-btn-text">HOME</span></button>
         <%}%>
           <cq:include path="<%= searchPath %>" resourceType="girlscouts/components/search-box" />
        </div>
    </li>
</ul>

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
			boolean parent = false;
			boolean isPlaceholder;
            try{
                isPlaceholder = rr.resolve(path).adaptTo(Node.class).getNode("jcr:content").getProperty("sling:resourceType").getString().equals("girlscouts/components/placeholder-page");
            }catch(Exception e){
                isPlaceholder = false;
            }
            if (active) {
                parent = true;
				sb.append("<li class='side-nav-el parentEl' id=\"sub-active\">");
			}else{
				sb.append("<li class='side-nav-el parentEl'>");
			}
			if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) {
			    if(parent){
			          sb.append("<div class='side-nav-wrapper'><a style='padding-left: 0px;' x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\">"+ label + "</a></div><hr>");
			    }else{
			          sb.append("<div class='side-nav-wrapper'><a x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\">" + label + "</a></div><hr>");
			    }

		    } else {

		     if(parent){
		        if(isPlaceholder){
		            sb.append("<div class='side-nav-wrapper'><span id='side-nav-active-parent' style='display: inline-block; padding-left: 0px; margin-left: 6px;' title=\"" + label + "\">" + label + "</span><span class='side-nav-expand'>></span></div><hr>");
		        }else{
		            sb.append("<div class='side-nav-wrapper'><a style='padding-left: 0px;' href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a></div><hr>");
		        }
		     }else{
		        Node nodePath = rr.resolve(path).adaptTo(Node.class);
		        if(isPlaceholder){
		            sb.append("<div class='side-nav-wrapper'><span style='display: inline-block;' title=\"" + label + "\">" + label + "</span><span class='side-nav-expand'>></span></div><hr>");
		        }else{
		            sb.append("<div class='side-nav-wrapper'><a href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a></div><hr>");
		        }


		     }

		    }
            String topMenuPath = getTopMenuPath(path);
            if(!topMenus.contains(topMenuPath)){
                topMenus.add(topMenuPath);
                Page topMenuPathPage = rr.resolve(topMenuPath).adaptTo(Page.class);
                buildMenu(topMenuPathPage, currentPath, sb, rr);
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

    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb, ResourceResolver rr) {

        Iterator<Page> iter = rootPage.listChildren();

        boolean hasChild = false;
        while(iter.hasNext()) {
            Page page = iter.next();
            String path = page.getPath();
            boolean isCurrent = currentPath.equals(path);
            boolean isActive = (currentPath + "/").startsWith(path + "/");
            if (page.isHideInNav()) {
                continue;
            }
            if (hasChild == false) {
                if(isActive && isCurrent){
                    sb.append("<ul>");
                }else{
                    sb.append("<ul style='display: none;'>");
                }
                hasChild = true;
            }

            String title = "";//page.getTitle();
            if (page.getNavigationTitle() != null && !"".equals(page.getNavigationTitle())) {
            	title = page.getNavigationTitle();
            } else {
            	title = page.getTitle();
           	}
            if (title != null && !title.isEmpty()) {

                String activeCls = isActive ? "active" : "";

                String currentCls = isCurrent ? " current" : "";
                if (isActive || isCurrent) {
                    sb.append("<li class=\"side-nav-el " + activeCls + currentCls + "\">");
                } else {
                    sb.append("<li class='side-nav-el'>");
                }
                sb.append("<div class='side-nav-wrapper' style='position: relative;'><a href=\"" + page.getPath() + ".html\">");
                sb.append(title);
                sb.append("</a>");
                Page currPage = rr.resolve(path).adaptTo(Page.class);
                if(currPage != null && currPage.listChildren(new PageFilter()).hasNext()){
                     sb.append("<span class='side-nav-expand-child'>></span>");
                }
                sb.append("</div>");
                sb.append("<hr>");
                buildMenu(page, currentPath, sb, rr);
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
