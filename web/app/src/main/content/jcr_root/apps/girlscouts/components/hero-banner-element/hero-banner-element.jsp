<%@include file="/libs/granite/ui/global.jsp" %>

<%@ page import="com.adobe.granite.ui.components.*" %>
<%@ page import="org.apache.sling.api.resource.Resource" %>

<%

    Config cfg = cmp.getConfig();
    String linkUrl = cmp.getValue().getContentValue("linkURL");

    Tag tag = cmp.consumeTag();
    tag.setName("div");
%>

<div class="heroBannerElementContainer">
    <div class="heroBannerElementHeader" onclick="$(this).parent().find('.heroBannerElementConfigContents').slideToggle();">
        <h1><%= linkUrl %></h1>
    </div>
    <div class="heroBannerElementImagePreview"></div>
    <div class="heroBannerElementConfigContents" style="display: none">
        <%
            cmp.includeForLayout(resource, new ComponentHelper.Options().tag(tag));
        %>
    </div>
</div>