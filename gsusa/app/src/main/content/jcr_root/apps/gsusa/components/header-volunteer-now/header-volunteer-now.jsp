<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode"%>

<%
// All pages share the same header from the site root
String callToActionName = currentNode.hasProperty("callToActionName") ? currentNode.getProperty("callToActionName").getString():"Volunteer";
String searchBtnName = currentNode.hasProperty("searchBtnName") ? currentNode.getProperty("searchBtnName").getString():"Go";
String title = currentNode.hasProperty("title") ? currentNode.getProperty("title").getString():"Find Your Local Council";
Boolean isHidden = currentNode.hasProperty("isVolunteerHidden") ? currentNode.getProperty("isVolunteerHidden").getBoolean():false;
String source = currentNode.hasProperty("source") ? currentNode.getProperty("source").getString():"not_set";

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
