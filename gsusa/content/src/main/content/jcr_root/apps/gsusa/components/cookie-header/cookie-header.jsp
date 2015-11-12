<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@page session="false" %>

<%if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
}
final String bgcolor = properties.get("bgcolor", "6e298d"); //the default purple color
final String mainText = properties.get("maintext", "");
final boolean hasRightShareSection = properties.get("shareSection", false);
final String shareSectionIcon = properties.get("icon", "");
final String shareSectionText = properties.get("sharetext", "");
final String shareSectionLink = properties.get("sharelink", "");
final String image = properties.get("file", "");
//TODO share link to 
%>

<div class="row">
  <!--img src="/etc/designs/gsusa/clientlibs/images/zip-cookie-bg.png" alt="cookie zip code image" /-->
  <div class="wrapper clearfix" style="background: #<%= bgcolor%>">
    <div class="wrapper-inner clearfix">
      <form class="find-cookies" name="find-cookies">
        <label for="zip-code"><%= mainText %></label>
        <div class="form-wrapper clearfix">
          <input type="text" placeholder="ZIP Code" class="zip-code" name="zip-code">
          <input type="submit" class="link-arrow" value="Go >"/>
        </div>
      </form>
      <div class="share">
        <a href="<%=shareSectionLink %>" title="cookies on facebook"><span><%= shareSectionText %></span> <i class="<%= shareSectionIcon %>"></i></a>
      </div>
    </div>
  </div>
</div>


