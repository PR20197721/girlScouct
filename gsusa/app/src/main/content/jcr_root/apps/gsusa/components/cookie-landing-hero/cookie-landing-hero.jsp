<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page session="false"%>

<%
String text = properties.get("text", "");
String facebookLink = properties.get("facebookLink", "");
String resultPage = properties.get("resultPage", currentPage.getPath());
String[] images = properties.get("images", String[].class);

if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == null || images.length == 0)) {
    %>Cookie Landing Component. Double click here to edit.<%
} else {
    %><div class="cookie-landing-hero hide-for-small">
        <div class="welcome-video-slider"><%
            for (String image : images) {
                %><div>
                    <img src="<%= getImageRenditionSrc(resourceResolver, image, "cq5dam.npd.top.") %>" data-at2x="<%= getImageRenditionSrc(resourceResolver, image, "cq5dam.npd.top@2x.") %>" alt="" />
                </div><%
            }
        %></div>
        <div class="cookie-header">
            <div class="wrapper clearfix">
                <div class="wrapper-inner clearfix">
                    <form class="find-cookies" name="find-cookies">
                        <label for="zip-code"><%=text%></label>
                        <div class="form-wrapper clearfix">
                            <div>
                                <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" class="zip-code" name="zip-code" placeholder="ZIP Code" />
                            </div>
                            <div>
                                <input type="submit" class="link-arrow" value="Go >" />
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="facebook-image">
            <a href="<%=facebookLink%>" title="follow on facebook">
                <img src="/etc/designs/gsusa/images/facebook-image.png" alt="facebook" />
            </a>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            bindSubmitHash({
                formElement: "form[name='find-cookies']",
                hashElement: "input[name='zip-code']",
                redirectUrl: "<%=resourceResolver.map(resultPage)%>",
                currentUrl: "<%=resourceResolver.map(currentPage.getPath())%>"
            });
        });
    </script><%
}
%>