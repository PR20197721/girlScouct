
<%@ page
    import="java.util.Arrays,
    java.util.List"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<% 
 String[] links = (String[])(request.getAttribute("globalNavigation"));
  

for (int i = 0; i < links.length; i++) {
    String[] values = links[i].split("\\|\\|\\|");
    String label = values[0];
    String path = values.length >= 2 ? values[1] : "";
    path = genLink(resourceResolver, path);
    String clazz = values.length >= 3 ? " "+ values[2] : "";
    String mLabel = values.length >=4 ? " "+values[3] : "";
    String sLabel = values.length >=5 ? " "+values[4] : "";
    
    %>
       
       <li>
          <a class="hide-for-medium hide-for-small menu <%= clazz %>" href="<%= path %>"><%= label %></a>
          <a class="hide-for-large hide-for-small hide-for-xlarge hide-for-xxlarge menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
       </li> 
     
     
     
       
       <%
}
%>