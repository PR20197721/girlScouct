<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->

<%
String[] links = properties.get("links", String[].class);
request.setAttribute("globalNavigation", links);
for (int i = 0; i < links.length; i++) 
{
        String[] values = links[i].split("\\|\\|\\|");
        String label = values[0];
        String path = values.length >= 2 ? values[1] : "";
        String clazz = values.length >= 3 ? " "+ values[2] : "";
        String mLabel = values.length >=4 ? " "+values[3] : "";
        String sLabel = values.length >=5 ? " "+values[4] : "";
        %><li>
           <a class="<%= clazz %>" href="<%= path %>.html"><%= mLabel %></a>
         </li>
           
           <%
    }
    %>
    
    
    
 