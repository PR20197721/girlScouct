<%@include file="/libs/foundation/global.jsp" %>
<%
    final String FOOTER_PROPS_KEY = "gsusa.eyebrow.items.data";
    ValueMap extProperties = (ValueMap)request.getAttribute(EYEBROW_PROPS_KEY);
    if (extProperties != null) {
        properties = extProperties;
    }

    final String[] navs = properties.get("navs", String[].class);
    
    if (navs != null) {
        for (int i = 0; i < navs.length; i++) {
            String[] split = navs[i].split("\\|\\|\\|");
            String label = split.length >= 1 ? split[0] : "";
            String link = split.length >= 2 ? split[1] : "";
            
            Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
            if (linkPage != null && !link.contains(".html")) {
                link += ".html";
            }
            %><li><a href="<%= link %>" title="<%= label %>"><%= label %></a></li><%
        }
    }
%>