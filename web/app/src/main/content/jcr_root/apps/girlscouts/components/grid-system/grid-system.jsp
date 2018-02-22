<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Default parsys component

  Includes all child resources but respects the columns control resources and
  layouts the HTML accordingly.

--%><%@page import="java.util.HashSet,
                    java.util.Set,
                    com.day.cq.commons.jcr.JcrConstants,
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.api.components.IncludeOptions,
                    com.day.cq.wcm.foundation.Paragraph,
                    com.day.cq.wcm.foundation.ParagraphSystem" %><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%
    String gridStyle = "";
	String itemStyle = "";
	String cssClasses = properties.get("cssClasses", "");
	String rawGridStyle = properties.get("gridStyle", "");
	rawGridStyle = rawGridStyle.trim();
	String rawItemStyle = properties.get("itemStyle", "");
	rawItemStyle = rawItemStyle.trim();

	if(!rawGridStyle.isEmpty()){
		String[] gridTags = rawGridStyle.split(" ");
        boolean first = true;
        for(String styleItem : gridTags){
            if(!styleItem.isEmpty()){
                if(first){
                    gridStyle = gridStyle + styleItem;
                    first = false;
                } else{
    
                    gridStyle = gridStyle + "; " + styleItem;
                }
            }
        }
	}

	if(!rawItemStyle.isEmpty()){
		String[] itemTags = rawItemStyle.split(" ");
        boolean first = true;
        for(String styleItem : itemTags){
            if(!styleItem.isEmpty()){
                if(first){
                    itemStyle = itemStyle + styleItem;
                    first = false;
                } else{
    
                    itemStyle = itemStyle + "; " + styleItem;
                }
            }
        }
	}



	String largeBlocks = properties.get("largeBlocks", "4");
	if (!largeBlocks.isEmpty()) {
		largeBlocks = "large-block-grid-" + largeBlocks + " ";
    }

	String mediumBlocks = properties.get("mediumBlocks", "2");
	if (!mediumBlocks.isEmpty()) {
		mediumBlocks = "medium-block-grid-" + mediumBlocks + " ";
    }
	String smallBlocks = properties.get("smallBlocks", "1");
	if (!smallBlocks.isEmpty()) {
		smallBlocks = "small-block-grid-" + smallBlocks + " ";
    }

    ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);
    String newType = resource.getResourceType() + "/new";
    
    %>
<style>
	
</style>

<ul style= "<%= gridStyle%>" class="<%=largeBlocks%><%=mediumBlocks%><%=smallBlocks%>"><% 
    
    boolean hasColumns = false;
	int count = 0;

    for (Paragraph par: parSys.paragraphs()) {
        if (editContext != null) {
            editContext.setAttribute("currentResource", par);
        }

        switch (par.getType()) {
            case START:
                if (hasColumns) {
                    // close in case missing END
                    %></div></div><%
                }
                if (editContext != null) {
                    // draw 'edit' bar
                    Set<String> addedClasses = new HashSet<String>();
                    addedClasses.add("section");
                    addedClasses.add("colctrl-start");
                    IncludeOptions.getOptions(request, true).getCssClassNames().addAll(addedClasses);
                    setCssClasses(cssClasses, request);
                    %><li style="<%= itemStyle %>"><sling:include resource="<%= par %>"/></li><%
                }
                // open outer div
                %><div class="parsys_column <%= par.getBaseCssClass()%>"><%
                // open column div
                %><div class="parsys_column <%= par.getCssClass() %>"><%
                hasColumns = true;
                break;
            case BREAK:
                if (editContext != null) {
                    // draw 'new' bar
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                    setCssClasses(cssClasses, request);
                    %><li style="<%= itemStyle %>"><sling:include resource="<%= par %>" resourceType="<%= newType %>"/></li><%
                }
                // open next column div
                %></div><div class="parsys_column <%= par.getCssClass() %>"><%
                break;
            case END:
                if (editContext != null) {
                    // draw new bar
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                    setCssClasses(cssClasses, request);
                    %><li style="<%= itemStyle %>"><sling:include resource="<%= par %>" resourceType="<%= newType %>"/></li><%
                }
                if (hasColumns) {
                    // close divs and clear floating
                    %></div></div><div style="clear:both"></div><%
                    hasColumns = false;
                }
                if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
                    // draw 'end' bar
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                    setCssClasses(cssClasses, request);
                    %><li style="<%= itemStyle %>"><sling:include resource="<%= par %>"/></li><%
                }
                break;
            case NORMAL:
                // include 'normal' paragraph
                IncludeOptions.getOptions(request, true).getCssClassNames().add("section");

                // draw anchor if needed
                if (currentStyle.get("drawAnchors", false)) {
                    String path = par.getPath();
                	path = path.substring(path.indexOf(JcrConstants.JCR_CONTENT)
                            + JcrConstants.JCR_CONTENT.length() + 1);
                	String anchorID = path.replace("/", "_").replace(":", "_");
                    %><a name="<%= anchorID %>" style="visibility:hidden"></a><%
                }
                setCssClasses(cssClasses, request);

            %><li style="<%= itemStyle %>"><sling:include resource="<%= par %>"/></li><%

                break;
        }

    }
    if (hasColumns) {
        // close divs in case END missing. and clear floating
        %></div></div><%
    }
    // Fix for issue under foundation framework: the "new" bar misbehaves
    %><div style="clear:both"></div><%
    if (editContext != null) {
        editContext.setAttribute("currentResource", null);
        // draw 'new' bar
        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
        setCssClasses(cssClasses, request);
        %><li><cq:include path="*" resourceType="<%= newType %>"/></li><%
    }
%>
</ul>

