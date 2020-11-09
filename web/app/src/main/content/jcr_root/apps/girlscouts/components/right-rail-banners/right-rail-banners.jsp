<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap, 
com.day.cq.wcm.api.WCMMode,
java.util.Set,
java.util.HashSet,
java.util.Arrays" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
final int DEFAULT_AD_COUNT = 2;
final String AD_ATTR = "apps.girlscouts.components.advertisement.currentAd";
%>
<%!public boolean isAd(Page currentAd){//return true if page is ad page
    if(currentAd==null) { return false; }
	if(currentAd.getProperties().get("sling:resourceType")!=null
       && currentAd.getProperties().get("sling:resourceType","").indexOf("girlscouts/components/ad-page")>=0){
        return true;
    }
    return false;
}
%>
<%
boolean customized = properties.get("customized", false);
String[] adPages = properties.get("pages", new String[] {});
String[] excludedPages = properties.get("excludedPages", new String[] {});

if(customized){
	if (adPages!=null) {
    	for(String itemUrl : adPages){
                try{
                	if(resourceResolver.getResource(itemUrl)!=null) {
	                	Page currentAd = resourceResolver.getResource(itemUrl).adaptTo(Page.class);
    	                if(isAd(currentAd)) {
							request.setAttribute(AD_ATTR, currentAd);
        					%>
                	        <div class="hide-for-small">
                    	    <cq:include script="display-ad.jsp"/>
                        	</div>
	                        <div class="show-for-small">
    	                    <div class="small-12 columns">
        	                <% request.setAttribute(AD_ATTR, currentAd);
								String hideForMobileCheck = currentAd.getProperties().get("hideForMobile", "false");
                    			if(!hideForMobileCheck.equals("true")){	%>
            	            <cq:include script="display-ad.jsp"/>
            	            <%} %>
                	        </div>
                    	    </div>
                        	<%
    	                }
                    }
              	}catch(Exception e){ e.printStackTrace();}
        }
	}else{
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
	}
}

//by default show all start
else{

    Set<String> excludedSet = new HashSet<String>(Arrays.asList(excludedPages));

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
            boolean adWasRendered = false;
            while(iter.hasNext()) {
                Page currentAd = iter.next();
                adWasRendered = false;
                if(isAd(currentAd) && !excludedSet.contains(currentAd.getPath())) {
                    request.setAttribute(AD_ATTR, currentAd);
                    request.setAttribute("adWasRendered", false);
                    %>
                    <div class="hide-for-small">
                    <cq:include script="display-ad.jsp"/>
                    </div>
                    <div class="show-for-small">
                    <div class="small-12 columns">
                    <% request.setAttribute(AD_ATTR, currentAd);
						String hideForMobileCheck = currentAd.getProperties().get("hideForMobile", "false");
                    			if(!hideForMobileCheck.equals("true")){
                    				%>
                    <cq:include script="display-ad.jsp"/>
                    <%} %>
                    </div>
                    </div>
                    <%
                    adWasRendered = Boolean.TRUE == request.getAttribute("adWasRendered");
                    if(adWasRendered){
						renderCount++;
                    }

                }

            }

            if(renderCount == 0){
                %><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
            }
        } 
    }catch(Exception e){
    	e.printStackTrace();
	}
}//show all end
%>


