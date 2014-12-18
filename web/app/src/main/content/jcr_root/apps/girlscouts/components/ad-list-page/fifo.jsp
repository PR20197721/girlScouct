<%--

  AD List Page component.

  A page that displays Ads.

--%>
<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>

<%
final int DEFAULT_AD_COUNT = 2;
final String AD_ATTR = "apps.girlscouts.components.ad-list-page.currentAd";

String[] selectors = slingRequest.getRequestPathInfo().getSelectors();

int adCount = DEFAULT_AD_COUNT;
if (selectors.length != 0) {
	String adCountStr;
    adCountStr = selectors.length == 1 ? selectors[0] : selectors[1];
    try {
        adCount = Integer.parseInt(adCountStr);
    } catch (NumberFormatException e) {}
}

Iterator<Page> iter = currentPage.listChildren();
Boolean oddAdCount = false;
int renderCount = 0;
while(iter.hasNext() && adCount > 0) {
	if (adCount%2 == 1){
		oddAdCount = true;
    }
    else if(adCount%2 == 0){
        oddAdCount = false;
    }
    adCount--;
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