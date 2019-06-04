<%@page import="com.day.cq.wcm.api.WCMMode, 
                java.util.Random"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<%@page session="false"%>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="apps.girlscouts.components.standalonecampfinder" />

<%!
public String generateId() {
	Random rand = new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 6; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
}
%>

<%
final String text = properties.get("text", "");
final String resultPath = properties.get("results", "https://www.girlscouts.org/en/our-program/ways-to-participate/camp-and-outdoors/camp-finder/camp-finder-results.html");
String relativeCurrentPath;
try{
    relativeCurrentPath = currentPage.getPath().substring(currentPage.getPath().indexOf("/en"));
}catch(Exception e){
    relativeCurrentPath = currentPage.getPath();
}
Resource image = resource.getChild("image");
if (image == null) {
	if(WCMMode.fromRequest(request) == WCMMode.EDIT){
		%> *** Please select an image *** <%
	}
} else {
	String id = generateId();
	final String resPath = resource.getPath();
    String imageRendition = gsImagePathProvider.getImagePathByLocation(image);
                                             
	%><script>
        $(document).ready(function () {
            bindSubmitHash({
                formElement: ".find-camp",
                hashElement: "input[name='zip-code']",
                redirectUrl: "<%=resultPath%>",
                currentUrl: "<%=relativeCurrentPath%>"
            });
        });

        // Add important to stretch properly on mobile-camp-finder, overrule Retina.js
        document.styleSheets[0].insertRule("#<%=id%> { background: url('<%=imageRendition%>') no-repeat 0% 0%/100% 100% transparent !important; }", 0);
    </script>

    <div class="row">
        <div class="wrapper clearfix" id="<%=id%>" data-at2x="<%=get2xPath(imageRendition)%>">
            <div class="wrapper-inner clearfix">
                <form class="find-camp clearfix" name="find-camp">
                    <div class="caption">
                        <label for="zip-code"><%=text%></label>
                    </div>
                    <div class="form-wrapper clearfix">
                        <div>
                            <input type="tel" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" class="zip-code" name="zip-code" placeholder="ZIP Code" />
                        </div>
                        <div>
                            <input type="submit" class="link-arrow" value="Go >" />
                        </div>
                        <!-- <span>Please enter a valid zip code</span> -->
                    </div>
                </form>
            </div>
        </div>
    </div><%
}
%>

