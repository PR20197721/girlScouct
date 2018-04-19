<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>

<%
String callToActionName = properties.get("callToActionName", "Volunteer Now");
String searchBtnName = properties.get("searchBtnName", "Go");
String title = properties.get("title", "Find Your Local Council");
String mainTitle = properties.get("mainTitle", "");
String source = properties.get("source", "not_set");

String text = properties.get("text", "");
String bg = "";
String fileName = "";
try {
    bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
    fileName = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileName", "");
} catch (Exception e) {}

boolean bgExists = !bg.equals("") || !fileName.equals("");
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
	if (bgExists) { %> 
		<gsusa:image relativePath="bg" />
	<%} else if (textExists) {
        %><h5><%=mainTitle%></h5>
       <p><%=text%></p><%
    }
    %><div class="<%=wrapper%>">
        <a title="Volunteer Now" class="vol button arrow button-form">
            <%=callToActionName%>
            <form class="formVol button-form-target hide">
                <label><%=title%></label>
                <div>
                    <input type="tel" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="ZipVolunteer" placeholder="Enter ZIP Code" />
                </div>
                <input type="hidden" name="source" value="<%=source%>">
                <div>
                    <input class="button" type="submit" value="<%=searchBtnName%>">
                </div>
            </form>
        </a>
    </div>
</div>