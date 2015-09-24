<%@include file="/libs/foundation/global.jsp" %>
<%
    String imgPath = "";
	String retinaImgPath = null;
    try {
        imgPath = ((ValueMap)resource.getChild("image").adaptTo(ValueMap.class)).get("fileReference", "");
        retinaImgPath = imgPath.replaceAll("\\.(jpg|png|gif|bmp)$", "@2x\\.$1");
    } catch (Exception e) {}

%>
<img src="<%= imgPath %>" alt="Girl Scouts U.S.A. Home" aria-label="Girl Scouts U.S.A. Home"  data-at2x="<%=retinaImgPath%>" id="mainGSLogo"/>
