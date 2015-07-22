<%@page import="java.util.*,
                com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
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
		    int headerNavTabindex = 40 + i;
		    
		    mediumLabel = mediumLabel.isEmpty() ? label : mediumLabel;

            Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
                    
            if (linkPage != null && !link.contains(".html")) {
                link += ".html";
            }
            if (!label.isEmpty()) {
%>
               <li>
                   <a class="show-for-large-up" href="<%= link %>" title="<%= label %>" tabindex="<%= headerNavTabindex %>"><%= label %></a>
                   <a class="show-for-medium-only" href="<%= link %>" title="<%= mediumLabel %>" tabindex="<%= headerNavTabindex %>" ><%= mediumLabel %></a>
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
    </nav> <!-- END NAV.TAB-BAR HIDE-FOR-LARGE-UP -->

    <!--  OFF CANVAS -->
    <cq:include path="./off-canvas-nav" resourceType="gsusa/components/off-canvas-nav" />
<%
	request.removeAttribute("fromHeaderNav");

    } else if (WCMMode.fromRequest(request) == WCMMode.EDIT){
        %>Double click here to edit header navigation.<%
    }
%>
