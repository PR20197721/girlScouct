<%--

  Resource Category component.

  A resource category contains a group of resource items..

--%><%
%><%@page import="java.util.*,
	org.girlscouts.vtk.models.resources.Item,
	com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/apps/girlscouts-vtk/components/resources/global.jsp"%><%
%><%@page session="false" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>                  
<cq:defineObjects />
<%!
	void readRawItems(String[] rawItems, List<Item> items) {
		if (rawItems != null) {
			for (String rawItem : rawItems) {
				String[] values = rawItem.split("\\|\\|\\|");

				Item item = null;
				if (values.length >= 4) {
					item = new Item(values[0], values[1], values[2], values[3]);
				} else if (values.length >= 3) {
					item = new Item(values[0], values[1], values[2]);
				} else if (values.length >= 2 && ("list".equals(values[1]) || "meeting-overview".equals(values[1]))) {
					item = new Item(values[0], values[1], "");
				}
				
				if (item != null && item.type != null && !"".equals(item.type)) {
					items.add(item);
				}
			}
		}
	}

	void readRawItems(Node rawItems, List<Item> items) {
		try{
			if (rawItems != null && rawItems.hasNodes()) {
				NodeIterator childNodes = rawItems.getNodes();
				while(childNodes.hasNext()){
					try{
						Node rawItem = (Node)childNodes.next();
						String title = rawItem.hasProperty("title")? rawItem.getProperty("title").getString():"";
						String type = rawItem.hasProperty("type")? rawItem.getProperty("type").getString():"";
						String uri = rawItem.hasProperty("uri")? rawItem.getProperty("uri").getString():"";	
						String id = rawItem.hasProperty("id")? rawItem.getProperty("id").getString():"";	
						Item item = null;
						if("list".equals(type) || "meeting-overview".equals(type)){
							item = new Item(title, type, uri, id);
						}else{
							item = new Item(title, type, uri, "");
						}				
						if (item != null && item.type != null && !"".equals(item.type)) {
							items.add(item);
						}
					}catch(Exception e){
						
					}
				}			
			}
		}catch(Exception e1){
			
		}
			
	}
%>
<%
	List<Item> items = new ArrayList<Item>();

	Node categoryNode = resource.adaptTo(Node.class);
	
	if(categoryNode.hasNode("items")){
		readRawItems(categoryNode.getNode("items"), items);
	}
	ValueMap templateProperties = getTemplateResourceProperties(resource, resourceResolver);

	String[] parentRawItems = templateProperties.get("items", String[].class);
	readRawItems(parentRawItems, items);
	
	String icon = properties.get("icon", templateProperties.get("icon", ""));
	String title = properties.get("title", templateProperties.get("title", ""));
	
	if ("".equals(icon) && "".equals(title) && items.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">Double Click Here To Edit This Category</div><%
	} else {
		String level = resource.getParent().getName();
		String popupTitle = "";
		if ("daisy".equals(level)) {
			popupTitle = "Daisy Petal, Badge, and Journey Resources";
		} else if ("multi-level".equals(level)){
			popupTitle = "Multi-Level Badge and Journey Resources";
		} else if (level != null && level.length() > 0){
			popupTitle = level.substring(0, 1).toUpperCase() + level.substring(1) + " Badge and Journey Resources";
		}%>
		<div class="header clearfix">
			<h3 class="columns small-21"><%= popupTitle %></h3>
			<i style="position:absolute; top:5px; right:5px;" class="icon-button-circle-cross" onclick="(function(){$('#gsModal').dialog('close')})()"></i>
		</div>
		<div class="scroll" style="max-height:601px">
			<!-- Content -->
			<div class="columns small-22 small-centered" style="padding:20px 0; font-family:'Open Sans', Arial, sans-serif">
				<h3 style="color:black"><%= title %> (<%= items.size() %>)</h3>
				<table>
					<% 
						for (Item item : items) {
							%><tr><%
							String script = item.type + ".jsp";
							request.setAttribute("VTK_RESOURCES_ITEM_TITLE", item.title);
							request.setAttribute("VTK_RESOURCES_ITEM_URI", item.uri);
							%> <cq:include script="<%= script %>" /> <%
							request.removeAttribute("VTK_RESOURCES_ITEM_TITLE");
							request.removeAttribute("VTK_RESOURCES_ITEM_URI");
							%></tr><%
						}
					%>
				</table>
			</div>
			<!-- /Content  -->
		</div>
		<!--/scroll-->
		<%
	}
%>