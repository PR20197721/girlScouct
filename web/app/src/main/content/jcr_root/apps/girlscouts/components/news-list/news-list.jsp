<%@ page import="com.day.cq.wcm.api.WCMMode,
                   com.day.cq.wcm.api.components.DropTarget,
                   com.day.cq.wcm.foundation.List,
                   java.util.Iterator"%><%
%><%@include file="/libs/foundation/global.jsp"%><%

    // initialize the list
    %><cq:include script="init.jsp"/><%
    String offset = "";
    offset = request.getParameter("offset");
  
    List list = (List)request.getAttribute("list");
    if(offset!=null && !offset.isEmpty()){
      // Do nothing
    }else{
    	offset = "";
    	
    }
    
    if (!list.isEmpty() && offset.isEmpty()) {
        String cls = list.getType();
        cls = (cls == null) ? "" : cls.replaceAll("/", "");

        %><%= list.isOrdered() ? "<ol" : "<ul" %> class="<%= xssAPI.encodeForHTML(cls) %>"><%
        Iterator<Page> items = list.getPages();
        String listItemClass = null;
        while (items.hasNext()) {
            request.setAttribute("listitem", items.next());

            if (null == listItemClass) {
                listItemClass = "first";
            } else if (!items.hasNext()) {
                listItemClass = "last";
            } else {
                listItemClass = "item";
            }
            request.setAttribute("listitemclass", " class=\"" + listItemClass + "\"");

            String script = "listitem_" + cls + ".jsp";
            %><cq:include script="<%= script %>"/><%
        }
        %><%= list.isOrdered() ? "</ol>" : "</ul>" %><%
        if (list.isPaginating()) {
            %><cq:include script="pagination.jsp"/><%
        }
    } else {
        %><cq:include script="empty.jsp"/><%
    }
    if(!list.isEmpty()){
    	%><cq:include script="list-news.jsp"/>
    <%}
   
%>
