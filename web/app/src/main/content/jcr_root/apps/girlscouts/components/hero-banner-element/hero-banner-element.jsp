<%@include file="/libs/granite/ui/global.jsp" %>

<%@ page import="com.adobe.granite.ui.components.*" %>

<%

    Config cfg = cmp.getConfig();
    String linkUrl = cfg.get("linkURL", String.class);

    Tag tag = cmp.consumeTag();
    tag.setName("div");

    String backgroundImage = ""; // TODO: Get background image to show.

%>

<div class="heroBannerElementContainer">
    <div class="heroBannerElementImagePreview" onclick="$(this).parent().find('.heroBannerElementConfigContents').slideToggle();" style="background-image: '<%= backgroundImage %>'">
        <h1><%= linkUrl %></h1>
    </div>
    <div class="heroBannerElementConfigContents" style="display: none">
        <%
            cmp.includeForLayout(resource, new ComponentHelper.Options().tag(tag));
        %>
    </div>
</div>