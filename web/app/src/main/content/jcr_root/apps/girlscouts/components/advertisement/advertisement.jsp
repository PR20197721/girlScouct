<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap, 
com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>

<%
final int DEFAULT_AD_COUNT = 2;
final String AD_ATTR = "apps.girlscouts.components.advertisement.currentAd";
//Setting adCount
String tempAdCount = currentDesign.getStyle("three-column-page/advertisement").get("adCount", "");
int adCount = DEFAULT_AD_COUNT;
if (!tempAdCount.isEmpty()) {
    try {
        adCount = Integer.parseInt(tempAdCount);
    } catch (NumberFormatException e) {}
}

//render ad start
boolean customized = properties.get("customized", false);
String[] adPages = properties.get("pages", String[].class);

if(customized){
	if (adPages!=null) {
    	for(String itemUrl : adPages){
            if(adCount > 0){
                try{
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
                }catch(Exception e){ e.printStackTrace();}
                adCount--;
            }else{ break;}
        }
	}
}
//by default show all start
else{

    String rootPath = properties.get("path", "");
    if (rootPath.isEmpty()) {
        rootPath = currentSite.get("adsPath", "");
    }
    if (rootPath.isEmpty()) {
        if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
            %>Advertisement: Path not configured.<%
        }
        return;
    }
    try{
        Page adRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
        if (adRoot != null) {
            Iterator<Page> iter = adRoot.listChildren();
            int renderCount = 0;
            while(iter.hasNext() && adCount > 0) {
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
    
            if(renderCount == 0 && WCMMode.fromRequest(request) == WCMMode.EDIT){
                %><h2>No Ads Available To Render</h2> <%
            }
        } 
    }catch(Exception e){
    	e.printStackTrace();
	}
}//show all end
%>

