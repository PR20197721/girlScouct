<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
    final String[] navs = properties.get("navs", String[].class);
	if (navs != null) {
%>
		<ul class="inline-list">
<% 
	    for (int i = 0; i < navs.length; i++) {
		    String[] split = navs[i].split("\\|\\|\\|");
		    String label = split.length >= 1 ? split[0] : "";
		    String link = split.length >= 2 ? split[1] : "";
		    String mediumLabel = split.length >= 4 ? split[3] : label;
		    mediumLabel = mediumLabel.isEmpty() ? label : mediumLabel;
		    int footerTabindex = 150 + i ;

            Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
                    
            if (linkPage != null && !link.contains(".html")) {
                link += ".html";
            }
            if (!label.isEmpty()) {%>
				<li id="tag_footer_<%= linkifyString(label, 25)%>"><a href="<%= link %>" title="<%= label %>" tabindex=<%=footerTabindex%>><%= label %></a></li>
<%			}
        } 
%>
        </ul>
<%
	}
%>
