<%@include file="/libs/foundation/global.jsp" %>
<%
    String imgAlt = properties.get("imageAlt", "");
    String imgPath = "";
	String retinaImgPath = null;
    try {
        imgPath = ((ValueMap)resource.getChild("image").adaptTo(ValueMap.class)).get("fileReference", "");
        retinaImgPath = imgPath.replaceAll("\\.(jpg|png|gif|bmp)$", "@2x\\.$1");
    } catch (Exception e) {}
    
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String headerNavPath = headerPath + "/header-nav";   
    ValueMap headerNavProps = resourceResolver.resolve(headerNavPath).adaptTo(ValueMap.class);
    Boolean sticky = headerNavProps.get("displayStickyNav", false);

%>
<img src="<%= imgPath %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" aria-label="<%=imgAlt%>"  data-at2x="<%=retinaImgPath%>" id="mainGSLogo"/>
<%
if(sticky){
    String stickyImgPath = "";
	String stickyRetinaImgPath = null;
    try {
    	stickyImgPath = ((ValueMap)resource.getChild("stickyNavImage").adaptTo(ValueMap.class)).get("fileReference", "");
    	stickyRetinaImgPath = stickyImgPath.replaceAll("\\.(jpg|png|gif|bmp)$", "@2x\\.$1");
    } catch (Exception e) {}
	%>
	<div class="logo">
	    <img class="sticky-nav-GS-logo" src="<%= stickyImgPath %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" aria-label="<%=imgAlt%>"  data-at2x="<%=stickyRetinaImgPath%>" />
	</div>
	<%
}
%>