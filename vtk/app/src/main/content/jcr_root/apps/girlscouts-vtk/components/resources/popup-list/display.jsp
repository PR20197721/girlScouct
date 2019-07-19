<%@page import="org.girlscouts.vtk.models.resources.Item,
                java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@page session="false" %>
<%
    List<Item> items = (List<Item>) request.getAttribute("VTK_RESOURCES_ITEMS");
    String title = (String) request.getAttribute("VTK_RESOURCES_TITLE");
    String icon = (String) request.getAttribute("VTK_RESOURCES_ICON");

    String level = resource.getParent().getName();
    String popupTitle = "";
    if ("daisy".equals(level)) {
        popupTitle = "Daisy Petal, Badge, and Journey Resources";
    } else if ("multi-level".equals(level)) {
        popupTitle = "Multi-Level Badge and Journey Resources";
    } else if (level != null && level.length() > 0) {
        popupTitle = level.substring(0, 1).toUpperCase() + level.substring(1) + " Badge and Journey Resources";
    }
%>
<div class="header clearfix">
	<h3 class="columns small-21"><%= popupTitle %></h3>
	<span style="position:absolute; top:-5px; right:9px; color:black; font-size:22px; cursor:pointer; font-family: 'Trefoil Sans Web', 'Open Sans', Arial, sans-serif;" onclick="(function(){$('#gsModal').dialog('close')})()">X</span>
</div>
<div class="scroll" style="max-height:601px">
    <!-- Content -->
    <div class="columns small-22 small-centered" style="padding:20px 0; font-family:'Open Sans', Arial, sans-serif">
        <h3 style="color:black"><%= title %> (<%= items.size() %>)</h3>
        <table>
            <%
                for (Item item : items) {
            %>
            <tr><%
                String script = item.type + ".jsp";
                request.setAttribute("VTK_RESOURCES_ITEM_TITLE", item.title);
                request.setAttribute("VTK_RESOURCES_ITEM_URI", item.uri);
            %> <cq:include script="<%= script %>"/> <%
                request.removeAttribute("VTK_RESOURCES_ITEM_TITLE");
                request.removeAttribute("VTK_RESOURCES_ITEM_URI");
            %></tr>
            <%
                }
            %>
        </table>
    </div>
    <!-- /Content  -->
</div>
<!--/scroll-->
</ul>