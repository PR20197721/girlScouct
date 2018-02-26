<%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils,
        com.day.cq.commons.Doctype,
        com.day.cq.commons.DiffInfo,
        com.day.cq.commons.DiffService,
        org.apache.sling.api.resource.ResourceUtil" %>
        <%@page import="com.day.cq.wcm.api.WCMMode" %>
        
     
<%
  String title = properties.get("title", String.class);

  if(title == null || title.equals(""))
    {
       title = properties.get("jcr:title", String.class);
    }
  if (title == null || title.equals("")) 
    {
       title = currentPage.getTitle();
    }    
  if (title==null)
  {
  %><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><% }else
  {
	  String type = properties.get("type", String.class);
    // Get Type -
      if(type==null || type.equals("")){%>
         <h1><%=title%></h1>
     <% } else{
        //adding h1 h2 h3 h4 etc to title
        String styledTitle = "<"+type+">"+title+"</"+type+">";
       %>
	      <%=styledTitle %>
	     
   <%}%>
  <%}
%>
