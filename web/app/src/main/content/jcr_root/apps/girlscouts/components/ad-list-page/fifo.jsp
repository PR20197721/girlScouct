<%--

  AD List Page component.

  A page that displays Ads.

--%>
<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>

<%
final String AD_ATTR = "apps.girlscouts.components.ad-list-page.currentAd";
Iterator<Page> iter = currentPage.listChildren();
int renderCount = 0;
while(iter.hasNext()) {
    Page currentAd = iter.next();
    request.setAttribute(AD_ATTR, currentAd);
    %>
<div class="hide-for-small">
<cq:include script="display-ad.jsp"/>
</div>
<div class="show-for-small">
<div class="small-12 columns">
<% request.setAttribute(AD_ATTR, currentAd); %>
<cq:include script="display-ad.jsp"/>
</div>
</div>

<%
    renderCount ++;
}

if(renderCount == 0){
    %><h2>No Ads Available To Render</h2> <%
}

%>