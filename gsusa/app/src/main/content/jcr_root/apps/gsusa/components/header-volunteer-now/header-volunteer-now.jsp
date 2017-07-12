<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode"%>

<%
String callToActionName = properties.get("callToActionName", "Volunteer");
String searchBtnName = properties.get("searchBtnName", "Go");
String title = properties.get("title", "Find Your Local Council");
Boolean isHidden = properties.get("isVolunteerHidden", false);
String source = properties.get("source", "not_set");

if (!isHidden) {
    %><form class="formHeaderVolunteer" id="tag_header_volunteer">
        <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="ZipVolunteer" placeholder="ZIP Code" />
        <input type="hidden" name="source" value="<%=source%>">
        <div class="button" tabindex="30">
            <%=callToActionName%>
            <div class="triangle"></div>
        </div>
    </form><%
} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>Click to edit the Volunteer component in the eyebrow<%
}
%>
