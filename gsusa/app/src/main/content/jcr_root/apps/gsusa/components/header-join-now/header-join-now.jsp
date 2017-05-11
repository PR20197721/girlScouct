<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode"%>

<%
String callToActionName = properties.get("callToActionName", "Join");
String searchBtnName = properties.get("searchBtnName", "Go");
String title = properties.get("title", "Find Your Local Council");
Boolean isHidden = properties.get("isJoinHidden", false);
String source = properties.get("source", "not_set");

if (!isHidden) {
    %><form class="formHeaderJoin" id="tag_header_join">
        <input type="text" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="ZipJoin" placeholder="ZIP Code" />
        <input type="hidden" name="source" value="<%=source%>">
        <div class="button" tabindex="25">
            <%=callToActionName%>
            <div class="triangle"></div>
        </div>
    </form><%
} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>Click to edit the Join component in the eyebrow<%
}
%>
