<%@page session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="java.util.Iterator" %>
<%
    final String PATH_TO_CONTENT = "/content/";
try{
	Resource contentResource = resourceResolver.resolve(PATH_TO_CONTENT);
	if(contentResource != null && !contentResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING) && contentResource.hasChildren()){
		Iterator<Resource> sites = contentResource.listChildren();
		while(sites.hasNext()){
			Resource site = sites.next();
            Resource contacts = site.getChild("en/contacts");
            Resource events = site.getChild("en/events-repository");
            Resource enPage = site.getChild("en");
            
					%>
				<div class="foundation-collection-action" data-council="<%=site.getName() %>" 
				data-foundation-collection-action="{&quot;activeCondition&quot;:&quot;site.scaffolding&quot;}">
					
					<%
                    if(contacts != null){
                    String contactBulkEditorPath = "/etc/importers/gsbulkeditor.html?rp=" + contacts.getPath() + "&cm=true&deep=true&cv=jcr%3Atitle&ec=title%2Cphone%2Cemail%2Cteam&hib=false&is=true&rt=girlscouts%2Fcomponents%2Fcontact-page&it=contacts&hpc=true";
                    %>
							<a is="coral-anchorlist-item" 
							class="foundation-collection-action coral-Link coral-BasicList-item coral-AnchorList-item" 
							href="<%=contactBulkEditorPath%>" target="_blank" icon="news" tabindex="0">
								<div class="coral-BasicList-item-outerContainer" handle="outerContainer">
								  <div class=" coral-BasicList-item-contentContainer" handle="contentContainer">
								  	<coral-list-item-content class="coral-BasicList-item-content">Edit Contacts in Bulk</coral-list-item-content> 
								  </div>
								</div>
							</a>
                    <%
                        }
                    if(events != null){
                    String eventBulkEditorPath = "/etc/importers/gsbulkeditor.html?rp=" + events.getPath() + "&cm=true&deep=false&cv=jcr:title&ec=data%2Fstart%2Cdata%2Fend%2Cdata%2Fregion%2Cdata%2FlocationLabel%2Cdata%2Faddress%2Cdata%2Fdetails%2Cdata%2Fsrchdisp%2Cdata%2Fcolor%2Cdata%2Fregister%2Ccq%3Atags%2Cdata%2Fimage%2FfileReference%2Cdata%2FregOpen%2Cdata%2FregClose%2Cdata%2Ftimezone%2Cdata%2FprogType%2Cdata%2Fgrades%2Cdata%2FgirlFee%2Cdata%2FadultFee%2Cdata%2FminAttend%2Cdata%2FmaxAttend%2Cdata%2FprogramCode&hib=false&is=true&rt=girlscouts/components/event-page&it=events&hpc=false&hy=false&hr=true&yr=2019";
                        %>
                        <a is="coral-anchorlist-item" 
							class="foundation-collection-action coral-Link coral-BasicList-item coral-AnchorList-item" 
							href="<%=eventBulkEditorPath%>" target="_blank" icon="news" tabindex="0">
								<div class="coral-BasicList-item-outerContainer" handle="outerContainer">
								  <div class=" coral-BasicList-item-contentContainer" handle="contentContainer">
								  	<coral-list-item-content class="coral-BasicList-item-content">Edit Events in Bulk</coral-list-item-content> 
								  </div>
								</div>
							</a>
                        
                        
                    <% } 
					String siteName = site.getName();
            		String documentsPath = "";
            		if(siteName.equals("gsnetx")){
						documentsPath = "/content/dam/NE_Texas/documents";
                    } else if (siteName.equals("gateway")){
						documentsPath = "/content/dam/gateway/forms-and-documents-";
                    } else if (siteName.equals("girlscoutcsa")){
						documentsPath = "/content/dam/southern-appalachian";
                    } else if (siteName.equals("girlscoutsnccp")){
						documentsPath = "/content/dam/nc-coastal-pines-images-/forms-and-documents-";
                    } else if (siteName.equals("gswcf")){
						documentsPath = "/content/dam/wcf-images/pdf-forms";
                    } else if (siteName.equals("gssem")){
						documentsPath = "/content/dam/gssem/documents";
                    } else if (siteName.equals("gssjc")){
						documentsPath = "/content/dam/gssjc/documents";
                    } else if (siteName.equals("gswestok")){
                        documentsPath = "/content/dam/gswestok/documents";
                    } else if (siteName.equals("girlscoutsaz")){
                        documentsPath = "/content/dam/girlscoutsaz/documents";
                    } else if (siteName.equals("kansasgirlscouts")){
                        documentsPath = "/content/dam/kansasgirlscouts/documents";
                    } else if (siteName.equals("gssnv")){
                        documentsPath = "/content/dam/gssnv/documents";
                    } else if (siteName.equals("gswo")){
                        documentsPath = "/content/dam/gswo/documents";
                    } else if (siteName.equals("girlscoutsosw")){
						documentsPath = "/content/dam/oregon-sw-washington-/forms";
                    } else if (siteName.equals("gskentuckiana")){
                        documentsPath = "/content/dam/gskentuckiana/documents";
                    } else if (siteName.equals("gssn")){
                        documentsPath = "/content/dam/gssn/documents";
                    } else if (siteName.equals("gsneo")){
                        documentsPath = "/content/dam/gsneo/documents";
                    } else if (siteName.equals("usagso")){
                        documentsPath = "/content/dam/usagso/documents";
                    } else if (siteName.equals("girlscoutsofcolorado")){
                        documentsPath = "/content/dam/girlscoutsofcolorado/documents";
                    } else if (siteName.equals("girlscoutstoday")){
                        documentsPath = "/content/dam/girlscoutstoday/documents";
                    } else if (siteName.equals("gsbadgerland")){
                        documentsPath = "/content/dam/gsbadgerland/documents";
                    } else if (siteName.equals("girlscoutsoc")){
                        documentsPath = "/content/dam/girlscoutsoc/documents";
                    } else if (siteName.equals("gscsnj")){
                        documentsPath = "/content/dam/gscsnj/documents";
                    } else{
                        documentsPath = "/content/dam/girlscouts-" + siteName + "/documents";
                    }

                    if(enPage != null){
                        String formsBulkEditorPath = "/etc/importers/gsbulkeditor.html?rp=" + documentsPath + "&cv=&cm=true&deep=true&ec=metadata%2Fdc%3Atitle%2Cmetadata%2Fdc%3Adescription%2Cmetadata%2Fcq%3Atags&hib=false&is=true&pt=dam%3AAsset&it=documents";
					%>
                        <a is="coral-anchorlist-item" 
							class="foundation-collection-action coral-Link coral-BasicList-item coral-AnchorList-item" 
							href="<%=formsBulkEditorPath%>" target="_blank" icon="news" tabindex="0">
								<div class="coral-BasicList-item-outerContainer" handle="outerContainer">
								  <div class=" coral-BasicList-item-contentContainer" handle="contentContainer">
								  	<coral-list-item-content class="coral-BasicList-item-content">Edit Forms in Bulk</coral-list-item-content> 
								  </div>
								</div>
							</a>
                        
                        
                    <%
                    }

            %>

			</div>
			<%

            }
			
		}						

}catch(Exception e){

}
%>
<script>
$(window).adaptTo("foundation-registry").register("foundation.collection.action.activecondition", {
	  name: "site.bulkeditor",
	  handler: function(name, el, config, collection, selections) {
	    return window.location.href.indexOf("/content/"+$(el).attr("data-council")) != -1;
	  }
	});
</script>
