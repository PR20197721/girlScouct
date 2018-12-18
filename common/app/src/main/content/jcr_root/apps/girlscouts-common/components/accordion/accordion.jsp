<%@page import="	com.day.cq.wcm.api.WCMMode, 
				org.apache.commons.lang3.StringUtils,
				org.apache.sling.api.resource.Resource, 
				java.util.Iterator,
				java.util.Date,
				java.lang.StringBuilder,  
				javax.jcr.PathNotFoundException,
				javax.jcr.Node" 
%>
<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="common.components.accordion"/>

<%
   	Resource children = resource.getChild("children");

	String accordionIndex = "";
	String accordionName = resource.getName();
	if(accordionName.contains("_") && accordionName.length() > accordionName.indexOf('_') + 1){
		accordionIndex = accordionName.substring(accordionName.indexOf('_') + 1);
	}

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
				if (accordion.hasProperty("anchorField")) {
					achorField = accordion.getProperty("anchorField").getString();
				}
				if (accordion.hasProperty("idField")) {
					idField = accordion.getProperty("idField").getString();
				}
				if (accordion.hasProperty("nameField")) {
					nameField = accordion.getProperty("nameField").getString();
				}
	            	String parsys = "accordion";
	            	if(!StringUtils.isBlank(accordionIndex)){
	            		parsys += "_" + accordionIndex;
	            	}
	            	parsys += "_parsys_";
	            	
	            	// Check if the node exists for the index.  Otherwise fallback to the ID if available.
	            	if(!StringUtils.isBlank(idField)){
                    	parsys += idField;
                    }else{
                        try{
                            resource.adaptTo(Node.class).getNode(parsys + accordion.getName());
                            parsys += accordion.getName();
                        }catch(PathNotFoundException pnfe){
    
                        }
                    }
	            	
	            	String parsysIdentifier = resource.getPath() + "/" + parsys;
            	%>
	            	<dt class="accordionComponentHeader" style="clear:both" id="<%=achorField%>" data-parsys-identifier="<%=parsysIdentifier %>" >
	            		<h6 class="accordionComponentLabel"><%=nameField%></h6>
	            		<div class="accordionComponentSwitch"></div>
	            		<div class="clr" style="clear:both"></div>
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