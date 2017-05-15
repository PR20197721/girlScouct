<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp"%>

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
                %><a class="button form button-form">
                    <%=title%>
                    <form class="formDonate clearfix button-form-target hide">
                        <!-- <label for="zipcode">Enter ZIP Code: </label> -->
                        <div>
                            <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="zip-code" placeholder="Enter ZIP Code" />
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
    </div>
    
    <script type="text/javascript">
        $(document).ready(function () {
            bindSubmitHash({
                formElement: ".formDonate",
                ajax: {
                    url: '/invest/ajax_CouncilFinder.asp',
                    success: function (resp) {
                        if (resp == null || resp == "") {
                            alert("The council you have searched for does not exist");
                        } else {
                            //console.log("Data: " + this.data);
                            var url = resp.split(',', 3);
                            window.open(url[2], '_blank');
                        }
                    }
                },
                edit: <%=WCMMode.fromRequest(request) == WCMMode.EDIT%>
            });
        });
    </script><%
}
%>