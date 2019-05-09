<%@page import="com.day.cq.wcm.api.WCMMode, 
                com.day.cq.wcm.foundation.Placeholder,
                java.util.Random"%>
<%@ page import="org.girlscouts.common.wcm.foundation.Image" %>
<%@ page import="org.girlscouts.common.util.ImageUtil" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<%@page session="false"%>

<%!
public String generateId() {
	Random rand = new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for (int i = 0; i < 6; i++) {
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
    }
	return sb.toString();
}
%>

<%
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
}
final String bgcolor = properties.get("bgcolor", "6e298d"); //the default purple color
final String mainText = properties.get("maintext", "");
final boolean hasRightShareSection = properties.get("shareSection", false);
final boolean disableInMobile = properties.get("disableinmobile", false);
final boolean disableInDesktop = properties.get("disableindesktop", false);
final String shareSectionIcon = properties.get("icon", "");
final String shareSectionText = properties.get("sharetext", "");
String shareSectionLink = properties.get("sharelink", "");
final String cookieBoothLink = properties.get("cookieboothlink", "");
String relativeResultPath;
String relativeCurrentPath;
if(cookieBoothLink.contains("/en")){
    relativeResultPath = resourceResolver.map(cookieBoothLink).substring(resourceResolver.map(cookieBoothLink).indexOf("/en"));
}else{
    relativeResultPath = resourceResolver.map(cookieBoothLink);
}
try{
    relativeCurrentPath = currentPage.getPath().substring(currentPage.getPath().indexOf("/en"));
}catch(Exception e){
    relativeCurrentPath = currentPage.getPath();
}
final String id = generateId();
Page shareSectionLinkPage = resourceResolver.resolve(shareSectionLink).adaptTo(Page.class);
if (shareSectionLinkPage != null && !shareSectionLink.contains(".html")) {
	shareSectionLink += ".html";
}
                                                                  
boolean isHomepage = currentPage.getPath().equals(currentPage.getAbsoluteParent(2).getPath());
String homepage = isHomepage ? "homepage" : "";
String mobileImageElement = id + (isHomepage ? " form label" : "") + ":before";
String share = hasRightShareSection ? "share" : "noshare";

Image fullImage = ImageUtil.getImage(resourceResolver, resource, "thumbnail");
Image mobileImage = ImageUtil.getImage(resourceResolver, resource, "mobileimage");
String fullImageSrc = "", mobileImageSrc = "";
if(fullImage != null){
    fullImage.setSelector("img");
	fullImageSrc = fullImage.getSrc();
}
if(mobileImage != null){
	mobileImage.setSelector("img");
	mobileImageSrc = mobileImage.getSrc();
}
%>

<script>
    document.styleSheets[0].insertRule("@media only screen and (min-width: 48.1225em) { #<%=id%>:before { background: url('<%= fullImageSrc %>') no-repeat 0% 0%/contain transparent; } }", 0);
    document.styleSheets[0].insertRule("@media only screen and (max-width: 48em) { #<%=mobileImageElement%> { background: url('<%= mobileImageSrc %>') no-repeat 0% 0%/contain transparent; } }", 0);
    $(document).ready(function () {
        bindSubmitHash({
            formElement: "form[name='find-cookies']",
            hashElement: "input[name='zip-code']",
            redirectUrl: "<%=relativeResultPath%>",
            currentUrl: "<%=relativeCurrentPath%>"
        });
    });
</script>

<div class="row <%=homepage%>">
    <!--img src="/etc/designs/gsusa/clientlibs/images/zip-cookie-bg.png" alt="cookie zip code image" /-->
    <div class="wrapper clearfix" style="background: #<%=bgcolor%>">
        <div class="wrapper-inner clearfix" id="<%=id%>">
            <form class="find-cookies-<%=share%>" name="find-cookies">
                <div class="caption">
                    <label for="zip-code"><%=mainText%></label>
                </div>
                <div class="form-wrapper clearfix">
                    <div>
                        <input type="tel" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" class="zip-code" name="zip-code" placeholder="ZIP Code" />
                    </div>
                    <div>
                        <input type="submit" class="link-arrow" value="Go >" onclick="location.href='#'"/>
                    </div>
                </div>
            </form><%
            if (hasRightShareSection) { 
                %><div class="share">
                    <a href="<%=shareSectionLink%>" title="cookies on facebook" target="_blank">
                        <span>
                            <%=shareSectionText%><i class="<%=shareSectionIcon%>"></i>
                        </span>
                    </a>
                </div><%
            } 
        %></div>
    </div>
</div>