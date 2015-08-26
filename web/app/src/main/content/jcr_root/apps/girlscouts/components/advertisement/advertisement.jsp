<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap, 
com.day.cq.wcm.api.WCMMode,
 java.util.HashSet" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
final String AD_ATTR = "apps.girlscouts.components.advertisement.currentAd";
boolean customized = properties.get("customized", false);
String[] adPages = properties.get("pages", String[].class);
if(customized){
if (adPages!=null) {
    for(String itemUrl : adPages){
		 Page currentAd = resourceResolver.getResource(itemUrl).adaptTo(Page.class);

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
        
        }
}
}
else{


String rootPath = properties.get("path", "");
if (rootPath.isEmpty()) {
    rootPath = currentSite.get("adsPath", "");
}
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Advertisement: path not configured.<%
    }
    return;
}
//Setting adCount
String tempAdCount = currentDesign.getStyle("three-column-page/advertisement").get("adCount", "");
int adCount;
if (tempAdCount.isEmpty()) {
	int defaultAdCount = 2;
    adCount = defaultAdCount;
}
else {
    adCount = Integer.parseInt(tempAdCount);
}

Page adRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
if (adRoot != null) {


Iterator<Page> iter = adRoot.listChildren();
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



<% } 
}%>

