<%@page import="	com.day.cq.wcm.api.WCMMode, 
				org.apache.sling.api.resource.Resource, 
				java.util.Iterator,
				java.util.Date,
				java.lang.StringBuilder,  
				javax.jcr.Node" 
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<cq:includeClientLib categories="common.components.accordion"/>

<%
   	Resource children = resource.getChild("children");

	if (children != null && !children.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		Iterator<Resource>	items = children.listChildren(); 
		if(items != null && items.hasNext()){
			%>
			<dl class="accordion accordionComponent" data-accordion>
			<%
			StringBuilder script = new StringBuilder();
			while(items.hasNext()){
				Node accordion = items.next().adaptTo(Node.class);
				String achorField = "";
				String idField = "";
				String nameField = "";
				if (accordion.hasProperty("achorField")) {
					achorField = accordion.getProperty("achorField").getString();
				}
				if (accordion.hasProperty("idField")) {
					idField = accordion.getProperty("idField").getString();
				}
				if (accordion.hasProperty("nameField")) {
					nameField = accordion.getProperty("nameField").getString();
				}
	            	String parsys = "accordion_parsys_" + accordion.getName();
	            	String parsysIdentifier = resource.getPath() + "/" + parsys;
            	%>
	            	<dt class="accordionComponentHeader" style="clear:both" id="<%=achorField%>" data-parsys-identifier="<%=parsysIdentifier %>" >
	            		<h6 class="accordionComponentLabel"><%=nameField%></h6>
	            		<div class="accordionComponentSwitch"></div>
	            	</dt>
	            	<dd class="accordion-navigation">
	            		<div class="content" id="<%=parsys%>">
	            			<cq:include path="<%=parsys%>" resourceType="foundation/components/parsys" />
	            		</div>
	            	</dd>
			<%		
			}
			%>
			</dl>
		<%
		}else{
	%>
		<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
	<%
		}
	}else{
%>
	<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%
	}
%>