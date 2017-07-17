<%@ page import="com.day.cq.wcm.api.WCMMode,
                org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,
                java.text.SimpleDateFormat,java.util.*,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.dao.*,
                org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
<%!
public String buildFlyOutMenu(Page parent, String flyRight) throws RepositoryException {
	try{
		if(hasVisibleChildren(parent)){
			Iterator<Page> children = parent.listChildren();
			StringBuilder menuBuilder = new StringBuilder();
			menuBuilder.append("<ul class=\"fly-down" + flyRight + "\">");
			while (children.hasNext()) {
				Page page = children.next();
				if (!page.isHideInNav()) {
					if(hasVisibleChildren(page)){
						menuBuilder.append("<li class=\"has-children\">");
						menuBuilder.append(createHref(page));
						Iterator<Page> grandChildren = page.listChildren();
						menuBuilder.append("<ul class=\"fly-horizontal\">");
						while (grandChildren.hasNext()) {
							Page p = grandChildren.next();
							if (!p.isHideInNav()) {
								menuBuilder.append("<li>");
								menuBuilder.append(createHref(p));
								menuBuilder.append("</li>");
							}					
						}
						menuBuilder.append("</ul>");
					}else{
						menuBuilder.append("<li>");
						menuBuilder.append(createHref(page));
					}
					menuBuilder.append("</li>");
				}
			}
			menuBuilder.append("</ul>");
			return menuBuilder.toString();
		}
	}catch(Exception e){}
    return "";
}

public boolean hasVisibleChildren(Page page){
	try{
		Iterator<Page> children = page.listChildren();
		while (children.hasNext()) {
			Page child = children.next();
			if (!child.isHideInNav()) {
				return true;
			}
		}
	}catch(Exception e){}
	return false;
}
%>
<%
final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
//Force currentPage from request
Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
if (newCurrentPage != null) {
 currentPage = newCurrentPage;
}

Boolean displaySecondaryNavFlyOut = properties.get("displaySecondaryNavFlyOut", Boolean.FALSE);
String flyoutClass = displaySecondaryNavFlyOut ? "flyout-nav" : "";
String[] links = properties.get("links", String[].class);
if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>##### Global Navigation #####<%
} else if (links != null){
%>

    <ul class="<%=flyoutClass%> inline-list">
        <%
        String currPath = currentPage.getPath();
        String rootPath = currentPage.getAbsoluteParent(2).getPath();
        String eventLeftNavRoot = currentSite.get("leftNavRoot", String.class);
        String eventPath = currentSite.get("eventPath", String.class);
        String contentResourceType="";
        Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
        String designPath = currentDesign == null ? "/" : currentDesign.getPath();
        for (int i = 0; i < links.length; i++) {
            String[] values = links[i].split("\\|\\|\\|");
            String label = values[0];
            String path = values.length >= 2 ? values[1] : "";
            String menuPath = values.length >= 2 ? values[1] : "";
            path = genLink(resourceResolver, path);
            String clazz = values.length >= 3 ? " "+ values[2] : "";
            String mLabel = values.length >=4 ? " "+values[3] : "";
            String sLabel = values.length >=5 ? " "+values[4] : "";
            Iterator <Page> slingResourceIter;
            String slingResourceType = "girlscouts/components/placeholder-page";
            contentResourceType="";
            try {
                contentResourceType = resource.getResourceResolver().getResource(menuPath+"/jcr:content").getResourceType();
                if(contentResourceType.equals(slingResourceType)){
                    slingResourceIter = resource.getResourceResolver().getResource(menuPath).adaptTo(Page.class).listChildren();
                    if(slingResourceIter.hasNext()){
                        Page firstChild =  slingResourceIter.next();
                        path = genLink(resourceResolver, firstChild.getPath());
                    }
                }
             }catch(Exception e){}

            String activeStatus = "";
            if(!currPath.equals(rootPath) && (
                (currPath.startsWith(eventPath) && eventLeftNavRoot.startsWith(menuPath))
                || menuPath.indexOf(currPath) == 0
                || currPath.startsWith(menuPath))
            ) {
                activeStatus = "active";
            }
        	if( path.toLowerCase().contains("vtk")){
        		%>
                <li>
                    <a data-dropdown="drop1" aria-controls="drop1" class="<%= clazz %> show-for-large-up menuHighlight" aria-expanded="false"><%= label %></a>
                    <a data-dropdown="drop1" aria-controls="drop1" class="<%= clazz %> show-for-medium-only menuHighlight" aria-expanded="false"><%= mLabel %></a>
                    <a data-dropdown="drop1" aria-controls="drop1" class="<%= clazz %> show-for-small-only menuHighlight" aria-expanded="false"><%= sLabel %></a>
                    <ul id="drop1" class="f-dropdown right" data-options="right_align:true" data-dropdown-content aria-hidden="true" tabindex="-1">
                      <li><a href="<%= currentPage.getAbsoluteParent(1).getPath() + "/en.html" %>">Home</a></li>
                       <%if( configManager.getConfig("isDemoSite")!=null && configManager.getConfig("isDemoSite").equals("true")){ %>
                         <li style="opacity:0.5;"><a href="#" onclick="javascript:void(0)" disabled="true">Member Profile</a></li>
                         <li><a href="/content/girlscouts-demo/en.html">Demo</a></li>                         
                      <%}else{ %>
                         <li><a href="<%= configManager.getConfig("communityUrl")%>">Member Profile</a></li>
                         <li><a href="<%= path %>">Volunteer Toolkit</a></li>
                      <%} %>
                    </ul>
                </li>
            <%
            }
        	else{
               Boolean displayFlyout = displaySecondaryNavFlyOut && menuPath != null && menuPath.startsWith("/content");
               Resource linkResource = null;
               Page flyPage = null;
               String hasChildren = "";
               if(displayFlyout){
	               try{
	            	   linkResource = resourceResolver.getResource(menuPath);
	            	   if(linkResource != null && "cq:Page".equals(linkResource.getResourceType())){
	                       	flyPage = linkResource.adaptTo(Page.class);
	                       	if(hasVisibleChildren(flyPage)) {
								hasChildren = "has-children ";
							}
	            	   }
	               }catch (Exception e){}
               }
                %>					
                <li class="<%=hasChildren%><%=activeStatus%>">
                    <a class="show-for-large-up menu <%= clazz %>" href="<%= path %>"><%= label %></a>
                    <a class="show-for-medium-only menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
                    <%
                	try{
                 		if(flyPage != null){
                            String flyRight = (i < links.length/2) ? " right" : "";
                 			out.print(buildFlyOutMenu(flyPage, flyRight));
                 		}
                	}catch (Exception e){}
                     %>
                </li>
                <%
        	}
        } %>
    </ul>
<% } %>


<script>
$(document).foundation({
    dropdown: {
        // specify the class used for active dropdowns
        active_class : 'open',
        opened : function () {
            $('#drop1').parent('li').addClass('on');
        },
        closed : function () {
            $('#drop1').parent('li').removeClass('on');
        }
      }
});
$(document).ready(function(){
  $(window).resize(function() {
    if($('#drop1').hasClass('open')) {
        $('#drop1').css('left', 'auto !important');
    }
  });
});
</script>
