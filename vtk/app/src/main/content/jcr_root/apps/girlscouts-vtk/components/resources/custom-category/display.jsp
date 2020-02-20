<%@page import="com.day.cq.wcm.api.WCMMode,
                org.apache.jackrabbit.commons.JcrUtils,
                org.girlscouts.vtk.models.resources.Item,
                java.util.List" %>
<%@include file="/apps/girlscouts-vtk/components/resources/global.jsp" %>
<%@page session="false" %>
<%

    final int MAX_ITEM_SHOW = 4;
    final int MAX_ITEM_COUNT = 8;

    List<Item> items = (List<Item>) request.getAttribute("VTK_RESOURCES_ITEMS");
    boolean shouldShowMore = items.size() > MAX_ITEM_SHOW;
    String title = (String) request.getAttribute("VTK_RESOURCES_TITLE");
    String icon = (String) request.getAttribute("VTK_RESOURCES_ICON");
%>
<p>
    <i class="<%= icon %>"></i> <%= title %>
</p>
<ul class="no-bullet">
    <%
        for (int i = 0; i < items.size(); i++) {
            if (i == MAX_ITEM_SHOW - 1 && shouldShowMore) { // -1 for the place of "more" button
    %>
    <ul class="__more_content hide no-bullet"><%
        }
        if (i >= MAX_ITEM_COUNT) break;
        Item item = items.get(i);
        String script = item.type + ".jsp";
        request.setAttribute("VTK_RESOURCES_ITEM_TITLE", item.title);
        request.setAttribute("VTK_RESOURCES_ITEM_URI", item.uri);
    %> <cq:include script="<%= script %>"/> <%
            request.removeAttribute("VTK_RESOURCES_ITEM_TITLE");
            request.removeAttribute("VTK_RESOURCES_ITEM_URI");
        }
        if (shouldShowMore) {
    %>
    </ul>
    <div class="__more">
        <span class="__text">more</span>
        <span class="arrow-small arrow-close"></span>
    </div>
    <%
        }
    %>
</ul>