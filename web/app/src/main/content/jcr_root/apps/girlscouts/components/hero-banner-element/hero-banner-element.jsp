<%@include file="/libs/granite/ui/global.jsp" %>

<%@ page import="com.adobe.granite.ui.components.*" %>

<%
    Tag tag = cmp.consumeTag();
    tag.setName("div");
%>

<div class="heroBannerElementContainer">
    <div class="heroBannerElementTriangle heroBannerElementCollapseTriangle hideTriangle" onclick="HeroBannerElementManager.hide(this)">
        <svg height="32" width="201">
            <polygon points="1,31 200,31, 100,1" class="heroBannerTriangleSvg"/>
        </svg>
    </div>
    <div class="heroBannerElementImagePreview" onclick="HeroBannerElementManager.show(this)"></div>
    <div class="heroBannerElementConfigContents">
        <%
            cmp.includeForLayout(resource, new ComponentHelper.Options().tag(tag));
        %>
    </div>
</div>