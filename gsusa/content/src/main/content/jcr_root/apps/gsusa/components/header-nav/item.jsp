<%@include file="/libs/foundation/global.jsp" %>

<!-- header-nav item -->
<%
    final String LABEL_KEY = "gsusa.header-nav.item.label";
    final String LINK_KEY = "gsusa.header-nav.item.link";

    String label = (String)request.getAttribute(LABEL_KEY);
    String link = (String)request.getAttribute(LINK_KEY);
    
    Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
    if (linkPage != null && !link.contains(".html")) {
        link += ".html";
    }

    if (!label.isEmpty()) {
%>
	    <li class="has-submenu">
	      <a href="<%= link %>" title="<%= label %>"> <%= label %></a>
	      <ul class="right-submenu">
	      	<!--  TODO: list children pages -->
	        <li class="back"><a href="#">Back</a></li>
	        <li><a href="#">Link 1</a></li>
	        <li><a href="#">Link 2</a></li>
	        <li><a href="#">Link 1</a></li>
	        <li><a href="#">Link 2</a></li>
	        <li><a href="#">Link 1</a></li>
	        <li><a href="#">Link 2</a></li>
	      </ul>
	    </li>
<%
    }
%>
<!--/of header-nav item -->