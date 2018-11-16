<%@page import="java.util.List,
                org.girlscouts.vtk.models.resources.Item" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	final int MAX_ITEM_SHOW = 6;
	final int MAX_ITEM_COUNT = 16;
	List<Item> items = (List<Item>)request.getAttribute("VTK_RESOURCES_ITEMS");
	// String title = (String)request.getAttribute("VTK_RESOURCES_TITLE");
	String level = (String)request.getAttribute("VTK_RESOURCES_LEVEL");
	level = (level == null) ? " " : level;
	int topCount = Math.min(items.size(), MAX_ITEM_SHOW);
	int bottomCount = Math.min(items.size(), MAX_ITEM_COUNT) - MAX_ITEM_SHOW;
	
	boolean shouldShowMore = items.size() > MAX_ITEM_SHOW;
%>
<div class="column small-24 medium-8">
	<p>
		<i style="margin:0 0 0 15px" class="icon-logo-<%=level%>"></i>
		<span><%=level.substring(0, 1).toUpperCase() + level.substring(1)%></span> 

	</p>		
</div>
<div class="column small-24 medium-8">
	<ul class="no-bullet">
<% 
	for (int i = 0; i < topCount; i++) {
		Item item = items.get(i);
		String script = item.type + ".jsp";
		request.setAttribute("VTK_RESOURCES_ITEM_URI", item.uri);
		request.setAttribute("VTK_RESOURCES_ITEM_TITLE", item.title);
		request.setAttribute("VTK_RESOURCES_ITEM_ID", item.id);
		if("list".equals(item.type)){
			%> 
			<cq:include path="<%= item.id %>" resourceType="girlscouts-vtk/components/resources/list"/>  
			<% 
		}else{
				%><cq:include script="<%= script %>" /> <% 
		}
		
		request.removeAttribute("VTK_RESOURCES_ITEM_URI");
		request.removeAttribute("VTK_RESOURCES_ITEM_TITLE");
		request.removeAttribute("VTK_RESOURCES_ITEM_ID");
		
		if (i == topCount / 2 - 1) {
			%></ul></div><div class="column small-24 medium-8"><ul class="no-bullet"><%
		}
	}
%>
	</ul>
</div>

<%
	if (shouldShowMore) {
%>
		<div class="__more_detail_level column" style="display:none">

				<div class="column small-24 medium-8 medium-offset-8">
					<ul class="no-bullet">
<%
		for (int i = 0; i < bottomCount; i++) {
			if (i >= MAX_ITEM_COUNT) break;
			Item item = items.get(MAX_ITEM_SHOW + i);

			String script = item.type + ".jsp";
			request.setAttribute("VTK_RESOURCES_ITEM_URI", item.uri);
			request.setAttribute("VTK_RESOURCES_ITEM_TITLE", item.title);
			request.setAttribute("VTK_RESOURCES_ITEM_ID", item.id);
			if("list".equals(item.type)){
				%> 
				<cq:include path="<%= item.id %>" resourceType="girlscouts-vtk/components/resources/list"/>  
				<% 
			}else{ 				
				%><cq:include script="<%= script %>" /> <% 
			}
			request.removeAttribute("VTK_RESOURCES_ITEM_URI");
			request.removeAttribute("VTK_RESOURCES_ITEM_TITLE");
			request.removeAttribute("VTK_RESOURCES_ITEM_ID");
		
			if (i == bottomCount / 2 - 1) {
				%></ul></div><div class="column small-24 medium-8"><ul class="no-bullet"><%
			}
		}
%>
					</ul>
				</div>
			</div>

		<div class="__more-level column small-24 medium-8 medium-offset-16" style="margin-top:20px;">
			<span class="__text" style="margin:0 0 0 30px">more</span>
			<span class="arrow-small arrow-close" style="margin:0 0 0 10px"></span>
		</div>
<%
	}
%>