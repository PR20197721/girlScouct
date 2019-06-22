<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                java.util.List,
                java.util.Collections,
                java.util.Comparator,
                java.util.ArrayList,
                com.day.cq.wcm.api.PageFilter,
                javax.jcr.Value,
                org.apache.sling.api.resource.ResourceResolver,
                com.day.cq.wcm.api.Page " %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="apps.gsusa.components.off-canvas" />
<%
    final String siteRootPath = currentPage.getAbsoluteParent(2).getPath();
    final String headerNavPath = siteRootPath + "/jcr:content/header/header-nav";
    Page sitePage = currentPage.getAbsoluteParent(1);
    String spanishPath = sitePage.getPath() + "/es";
    String englishPath = sitePage.getPath() + "/en";
    String searchPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/search";
    NodeIterator headerNavs = null;
    NodeIterator headerNavLinks = null;
    final Node headerNav = resourceResolver.resolve(headerNavPath).adaptTo(Node.class);
    if(headerNav != null && headerNav.hasNode("navs")){
    	headerNavs = headerNav.getNode("navs").getNodes();
    	headerNavLinks = headerNav.getNode("navs").getNodes();
    }
    ArrayList<String> linksList = new ArrayList<String>();
	StringBuilder sb = new StringBuilder();
	while(headerNavLinks.hasNext()){
        Node pagePathNode = headerNavLinks.nextNode();
        String pagePath = pagePathNode.hasProperty("path") ? pagePathNode.getProperty("path").getString() : "";
        boolean activePageVal = currentPage.getPath().startsWith(rePath(pagePath, 4)) && currentPage.getPath().contains(pagePath);
        if(activePageVal){
            linksList.add(pagePath);
        }
    }
    Collections.sort(linksList, new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if(o1.length()>o2.length()){
                return 1;
            }else{
                return o1.compareTo(o2);
            }
        }
    });
	sb.append("<ul class=\"off-canvas-list\">");
	List<String> topMenus = new ArrayList<String>();
    if(headerNavs != null && headerNavs.getSize() > 0){
    	while(headerNavs.hasNext()){
    		Node nav = headerNavs.nextNode();
			Boolean hideInMobile = nav.hasProperty("hide-in-mobile") ? nav.getProperty("hide-in-mobile").getBoolean() : false;
		    buildTopMenu(nav, currentPage.getPath(), resourceResolver, sb, topMenus, linksList);
    	}
    }
    	sb.append("</ul>");
	sb.append("</nav>");
%>
<nav class="left-off-canvas-menu closed" tabindex="-1">
<div id="left-canvas-menu" class="mobileSearch">
<ul class="off-canvas-list" style="background: #bcbec0">
    <li style="background: none; padding-top: 10px;">
        <div class="side-nav-header"><%
        if(spanishPath.equals(currentPage.getPath())){%>
             <button id="searchBtn" onclick="location.href='<%= englishPath %>.html'"type="side-nav-button"><span class="mobile-search-btn-text">English</span></button>
        <%}else{%>
             <button id="searchBtn" onclick="location.href='<%= spanishPath %>.html'"type="side-nav-button"><span class="mobile-search-btn-text">Espa&#241ol</span></button>
         <%}%>
           <cq:include path="<%= searchPath %>" resourceType="girlscouts/components/search-box" />
        </div>
        <hr>
    </li>
</ul>
<%= sb.toString() %>




<%!
	public void buildTopMenu(Node nav, String currentPath, ResourceResolver rr, StringBuilder sb, List<String> topMenus, ArrayList<String> list) {
		try{
			String label = nav.hasProperty("label") ? nav.getProperty("label").getString() : "";
			String largeLabel = nav.hasProperty("large-label") ? nav.getProperty("large-label").getString() : "";
			String mediumLabel = nav.hasProperty("medium-label") ? nav.getProperty("medium-label").getString() : "";
			String smallLabel = nav.hasProperty("small-label") ? nav.getProperty("small-label").getString() : "";
			String path = nav.hasProperty("path") ? nav.getProperty("path").getString() : "";
			Boolean rootLandingPage = nav.hasProperty("root-landing-page") ? nav.getProperty("root-landing-page").getBoolean() : false;
			Boolean newWindow = nav.hasProperty("new-window") ? nav.getProperty("new-window").getBoolean() : false;
			boolean isPlaceholder;
			Page currPage = rr.resolve(path).adaptTo(Page.class);
			boolean isParent = currPage != null && currPage.listChildren(new PageFilter()).hasNext();
			boolean parent = false;
            try{
                String resourceTypeStr = rr.resolve(path).adaptTo(Node.class).getNode("jcr:content").getProperty("sling:resourceType").getString();
                isPlaceholder = resourceTypeStr.equals("girlscouts/components/placeholder-page") || resourceTypeStr.equals("gsusa/components/placeholder-page");
            }catch(Exception e){
                isPlaceholder = false;
            }
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
			boolean active = currentPath.startsWith(rePath(path, 4)) && currentPath.contains(path);
			String sideNavClass = "side-nav-wrapper";
            if (active) {
                parent = false;
                boolean activePage = false;
                String activeP = list.get(list.size()-1);
                if(list.size() > 1){
                    if(path.equals(activeP)){
                        parent = true;
                        sb.append("<li class='side-nav-el parentEl active' tabindex=\"-1\">");
                    }else{
                        sb.append("<li class='side-nav-el parentEl' tabindex=\"-1\">");
                    }
                }else{
                    parent = true;
                    sb.append("<li class='side-nav-el parentEl active' tabindex=\"-1\">");
                }
			}else{
				sb.append("<li class='side-nav-el parentEl' tabindex=\"-1\">");
			}
			if (path.indexOf("http:") != -1 || path.indexOf("https:") != -1) {
		        if(parent){
		            sb.append("<div class="+sideNavClass+"><a style='font-weight: bold; margin-left: 7px;'x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\" tabindex=\"-1\" "+target+">" +"  "+ label + "</a></div><hr>");
                }else{
		            sb.append("<div class="+sideNavClass+"><a x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, path) + "\" title=\"" +label + "\" tabindex=\"-1\" "+target+">" + label + "</a></div><hr>");
		        }
		    } else {
		        if(parent){
		            if(isPlaceholder){
		                String childPath = rr.resolve(path).adaptTo(Page.class).listChildren().next().getPath();
		                sb.append("<div class="+sideNavClass+"><a style='font-weight: bold; margin-left: 10px; padding-left: 0px;' href=\"" + genLink(rr, childPath) + "\" title=\"" + label + "\">" + label + "</a><span class='side-nav-expand'>></span></div><hr>");
		            }else if(isParent){
		                sb.append("<div class="+sideNavClass+"><a style='font-weight: bold; margin-left: 10px; padding-left: 0px;' href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a><span class='side-nav-expand'>></span></div><hr>");
		            }else{
                        sb.append("<div class="+sideNavClass+"><a style='font-weight: bold; padding-left: 0px; margin-left: 10px;' href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a></div><hr>");
		            }
		        }else{
		            Node nodePath = rr.resolve(path).adaptTo(Node.class);
                    if(isPlaceholder){
                        String childPath = rr.resolve(path).adaptTo(Page.class).listChildren().next().getPath();
                        sb.append("<div class="+sideNavClass+"><a href=\"" + genLink(rr, childPath) + "\" title=\"" + label + "\">" + label + "</a><span class='side-nav-expand'>></span></div><hr>");
                    }else if(isParent){
                        sb.append("<div class="+sideNavClass+"><a href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a><span class='side-nav-expand'>></span></div><hr>");
                    }else{
                        sb.append("<div class="+sideNavClass+"><a href=\"" + genLink(rr, path) + "\" title=\"" + label + "\">" + label + "</a></div><hr>");
                    }
		        }

		    }
            Page topMenuPathPage = rr.resolve(path).adaptTo(Page.class);
            buildMenu(topMenuPathPage, currentPath, sb, rr, isPlaceholder);
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

    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb, ResourceResolver rr, boolean isPlaceholder) {

        Iterator<Page> iter = rootPage.listChildren();
        boolean hasChild = false;
        while(iter.hasNext()) {
            Page page = iter.next();
            String path = page.getPath();
            Boolean hidePage;
            try{
                hidePage = rr.resolve(path).adaptTo(Page.class).getProperties().get("hideInMobile", Boolean.class).booleanValue();
            }catch(Exception e){
                hidePage = false;
            }

            boolean isActive = (currentPath + "/").startsWith(path + "/");
            boolean isCurrent = currentPath.equals(path);
            if (page.isHideInNav() || hidePage) {
                continue;
            }
            if (hasChild == false) {
                if(isActive && isCurrent){
                    sb.append("<ul >");
                }
                else{
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
                    sb.append("<li class='side-nav-el' style='background: #bcbec0'>");
                }
                Page currPage = rr.resolve(path).adaptTo(Page.class);

                sb.append("<div class='menu-wrapper-el' ><a href=\"" + page.getPath() + ".html\">");
                sb.append(title);
                sb.append("</a>");
                if(currPage != null && currPage.listChildren(new PageFilter()).hasNext()){
                    sb.append("<span class='side-nav-expand-child'>></span>");
                }
                sb.append("</div>");
                sb.append("<hr>");
                buildMenu(page, currentPath, sb, rr, false);

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
