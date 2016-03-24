<%@include file="/libs/foundation/global.jsp" %>
<%
    String imgAlt = properties.get("imageAlt", "");
    String imgPath = "";
	String retinaImgPath = null;
    try {
        imgPath = ((ValueMap)resource.getChild("image").adaptTo(ValueMap.class)).get("fileReference", "");
        retinaImgPath = imgPath.replaceAll("\\.(jpg|png|gif|bmp)$", "@2x\\.$1");
    } catch (Exception e) {}

%>
<img src="<%= imgPath %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" aria-label="<%=imgAlt%>"  data-at2x="<%=retinaImgPath%>" id="mainGSLogo"/>
