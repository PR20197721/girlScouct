<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"%>

<%
String text = properties.get("text", "");
String resultPath = properties.get("resultPage", currentPage.getPath());
String formBgImage = properties.get("formbgimages", currentPage.getPath());
String images = properties.get("images", "");
String relativeResultPath;
String relativeCurrentPath;
try{
    relativeResultPath = resourceResolver.map(resultPath).substring(resourceResolver.map(resultPath).indexOf("/en"));
}catch(Exception e){
    relativeResultPath = resourceResolver.map(resultPath);
}
try{
    relativeCurrentPath = currentPage.getPath().substring(currentPage.getPath().indexOf("/en"));
}catch(Exception e){
    relativeCurrentPath = currentPage.getPath();
}
if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == "")) {
   %>Camp Landing Hero. Double click here to edit.<%
} else {
   %><script>
        $(document).ready(function () {
            bindSubmitHash({
                formElement: ".find-camp",
                hashElement: "input[name='zip-code']",
                redirectUrl: "<%=relativeResultPath%>",
                currentUrl: "<%=relativeCurrentPath%>"
            });
        });
    </script>
    
    <div class="camp-landing-hero hide-for-small ">
        <div class="bg-image">
            <img src="<%=images%>" alt="" />
        </div>
        <div class="camp-header" style="background: url('<%=formBgImage%>') no-repeat 0 0 transparent;">
            <div class="wrapper clearfix">
                <div class="wrapper-inner clearfix">
                    <form class="find-camp clearfix" name="find-camp">
                        <label for="zip-code"><%=text%></label>
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
        </div>
    </div><%
}
%>