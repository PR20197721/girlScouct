<%@include file="/libs/foundation/global.jsp"%>

<%
String callToActionName = properties.get("callToActionName", "Volunteer Now");
String searchBtnName = properties.get("searchBtnName", "Go");
String title = properties.get("title", "Find Your Local Council");
String mainTitle = properties.get("mainTitle", "");
String source = properties.get("source", "not_set");

String text = properties.get("text", "");
String bg = "";
try {
    bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
} catch (Exception e) {}
boolean bgExists = !bg.equals("");
boolean textExists = !text.equals("");

String classes = "";
if (bgExists) {
   classes = "bg-image";
} else if (textExists) {
   classes = "text-version";
} else {
   classes = "form-no-image";
}
String styles = bgExists ? "max-width: " + properties.get("maxWidth", 210) + "px;" : "";
String border = properties.get("showverticalrule", false) ? "border" : "";
String wrapper = bgExists || textExists ? "wrapper" : "";
%>

<div class="standalone-volunteer join-volunteer-block <%=classes%> <%=border%>" style="<%=styles%>"><%
    if (bgExists) { 
        slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
        %><cq:include path="bg" resourceType="gsusa/components/image"/><%
        slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
    } else if (textExists) {
        %><h5><%=mainTitle%></h5>
       <p><%=text%></p><%
    }
    %><div class="<%=wrapper%>">
        <a href="#" title="Volunteer Now" class="vol button arrow">
            <%=callToActionName%>
            <form class="formVol hide">
                <label><%=title%></label>
                <div>
                    <input type="text" name="ZipVolunteer" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
                </div>
                <input type="hidden" name="source" value="<%=source%>">
                <div>
                    <input class="button" type="submit" value="<%=searchBtnName%>">
                </div>
            </form>
        </a>
    </div>
</div>