<%@page import="com.day.cq.wcm.api.WCMMode, 
                java.util.Random"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<%@page session="false"%>

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
final String resultsPath = properties.get("results", "");
Resource image = resource.getChild("image");
String filePath = "";
if (image == null) {
	if(WCMMode.fromRequest(request) == WCMMode.EDIT){
		%> *** Please select an image *** <%
	}
} else {
	if (image != null) {
		filePath = ((ValueMap)image.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	String id = generateId();
	final String resPath = resource.getPath();
    String imageRendition = getImageRenditionSrc(resourceResolver, filePath, getResourceLocation(resource));
                                             
	%><script>
        $(document).ready(function () {
            bindSubmitHash({
                formElement: ".find-camp",
                hashElement: "input[name='zip-code']",
                redirectUrl: "<%=resourceResolver.map(resultsPath)%>",
                currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>"
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
                            <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" class="zip-code" name="zip-code" placeholder="ZIP Code" />
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

