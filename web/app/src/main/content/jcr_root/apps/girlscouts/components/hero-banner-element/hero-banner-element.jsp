<%@include file="/libs/granite/ui/global.jsp" %>

<%@ page import="com.adobe.granite.ui.components.*" %>
<ui:includeClientLib categories="girlscouts.components.hero-banner-element"/>
<%
    Tag tag = cmp.consumeTag();
    tag.setName("div");
%>

<div class="heroBannerElementContainer heroBannerElementContentDisplayed">
    <div class="heroBannerElementTriangle heroBannerElementCollapseTriangle" onclick="HeroBannerElementManager.hide(this)">
        <svg height="32" width="201">
            <polygon points="1,31 200,31, 100,1" class="heroBannerTriangleSvg"/>
        </svg>
    </div>
    <div class="heroBannerElementImagePreview" onclick="HeroBannerElementManager.show(this)"></div>
    <div class="heroBannerElementConfigContents" data-displayed="true">
        <%
            cmp.includeForLayout(resource, new ComponentHelper.Options().tag(tag));
        %>
    </div>
</div>