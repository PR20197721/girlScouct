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
							try{
                                uri = rawItem.getParent().getParent().getPath()+"/"+id;
                            }catch(Exception e){
                            }
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
	
	Node globalResource = getTemplateResourceItems(resource, resourceResolver);
	if(globalResource != null){
		readRawItems(globalResource, items); 
    }

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
		request.setAttribute("VTK_RESOURCES_ITEMS", items);
		request.setAttribute("VTK_RESOURCES_ICON", icon);
		request.setAttribute("VTK_RESOURCES_TITLE", title);
		%><cq:include script="display.jsp" /><%
		request.removeAttribute("VTK_RESOURCES_ITEMS");
		request.removeAttribute("VTK_RESOURCES_ICON");
		request.removeAttribute("VTK_RESOURCES_TITLE");
	}
%>