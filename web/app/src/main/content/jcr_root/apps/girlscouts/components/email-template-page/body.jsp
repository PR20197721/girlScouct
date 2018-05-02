<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
Boolean displayPageBanner = Boolean.FALSE;
try {
	   ValueMap globalNavProps = resourceResolver.getResource(headerPath + "/global-nav").adaptTo(ValueMap.class);
	   if(globalNavProps != null){
		   displayPageBanner = globalNavProps.get("./displayPageBanner", Boolean.FALSE);
		}
}catch(Exception e){}
%>
<body data-grid-framework="f4" data-grid-color="darksalmon" data-grid-opacity="0.5" data-grid-zindex="10" data-grid-gutterwidth="10px" data-grid-nbcols="24">
    <div class="off-canvas-wrap">
        <div class="inner-wrap<%if(displayPageBanner){%> round_corners<%}%>">            
            <div id="main" class="content row">
                <div class="large-24 medium-24 small-24 columns">
                    <h1><%=currentPage.getTitle()%></h1>
                    <%= properties.get("data/content","") %>
                    <hr>
                    Please use <a href="#" onclick="top.window.location = '/cf#<%= currentPage.getPath() + ".scaffolding.html" %>'">scaffolding</a> to edit
                </div>
        	</div>         
        </div>
    </div>
</body>