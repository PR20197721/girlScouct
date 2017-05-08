<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp"%>

<script type="text/javascript">
    var completeAndRedirectDonate = function (data) {
        <% 
        if (WCMMode.fromRequest(request) != WCMMode.EDIT) {
            %>var toPost = $('.formDonate').serialize();
            $(document).ready(function () {
                $.ajax({
                    method: "POST",
                    url: '/invest/ajax_CouncilFinder.asp',
                    data: toPost,
                    async: false,
                    success: function (resp) {
                        if (resp == null || resp == "") {
                            alert("The council you have searched for does not exist");
                        } else {
                            //console.log(resp);
                            var url = resp.split(',', 3);
                            //console.log(url[2]);
                            window.open(url[2], '_blank');
                        }
                    }
                });
            });<% 
        } else { 
            %>alert("This tool can only be used on a live page");<%
        } 
        %>
    }
</script>

<%
String title = properties.get("title", "Donate");
Boolean zip = (properties.get("zip", "Yes")).equals("Yes");
String href = properties.get("href", "");
if (!href.startsWith("http://") && !href.startsWith("https://")) {
    href = "http://" + href;
}

if (!zip && href.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>****** If you do not use the zip code option, you must enter a URL *******<%
} else if (zip || !href.isEmpty()) {
    String bg = "";
    try {
        bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
    } catch (Exception e) {}
    boolean bgExists = !bg.equals("");

    String styles = bgExists ? "max-width: " + properties.get("maxWidth", 210) + "px;" : "";
    String classes = bgExists ? "" : "form-no-image";
    String wrapper = bgExists ? "button-wrap" : "";


    %><div class="standalone-donate donate-block <%=classes%>" style="<%=styles%>"><%
        if (bgExists) {
            slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
            %><div class="bg-image">
                <cq:include path="bg" resourceType="gsusa/components/image" />
            </div><%
            slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
        }
        %><div class="<%=wrapper%>"><%
            if (zip) {
                %><a class="button form">
                    <%=title%>
                    <form class="formDonate clearfix hide" onsubmit="completeAndRedirectDonate(); return false;" method="POST">
                        <!-- <label for="zipcode">Enter ZIP Code: </label> -->
                        <div>
                            <input type="text" name="zipcode" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
                        </div>
                        <div>
                            <button type="submit" class="button">GO</button>
                        </div>
                    </form>
                </a><%
            } else {
                %><a class="button" href="<%=href%>" target="_blank"><%=title%></a><%
            }
        %></div>
    </div><%
}
%>