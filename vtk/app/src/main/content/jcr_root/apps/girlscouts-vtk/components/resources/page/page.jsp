<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Default page component.

  Is used as base for all "page" components. It basically includes the "head"
  and the "body" scripts.

  ==============================================================================

--%><%@page session="false"
            contentType="text/html; charset=utf-8"
            import="com.day.cq.commons.Doctype,
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.foundation.ELEvaluator" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%

    request.setAttribute("PAGE_CATEGORY", "VTK");

    // read the redirect target from the 'page properties' and perform the
    // redirect if WCM is disabled.
    String location = properties.get("redirectTarget", "");
    // resolve variables in path
    location = ELEvaluator.evaluate(location, slingRequest, pageContext);
    boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
    boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;
    if ( (location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview)) ) {
        // check for recursion
        if (currentPage != null && !location.equals(currentPage.getPath()) && location.length() > 0) {
            // check for absolute path
            final int protocolIndex = location.indexOf(":/");
            final int queryIndex = location.indexOf('?');
            final String redirectPath;
            if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
                redirectPath = location;
            } else {
                redirectPath = slingRequest.getResourceResolver().map(request, location) + ".html";
            }
            response.sendRedirect(redirectPath);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return;
    }
    // set doctype
    if (currentDesign != null) {
        currentDesign.getDoctype(currentStyle).toRequest(request);
    }

%><%= Doctype.fromRequest(request).getDeclaration() %>

<style>
    @font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 300;
  src: local('Open Sans Light'), local('OpenSans-Light'), url('../../../etc/designs/girlscouts/open-sans-light.woff') format('woff');
}
@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 400;
  src: local('Open Sans'), local('OpenSans'), url('../../../etc/designs/girlscouts/open-sans.woff') format('woff');
}
@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 600;
  src: local('Open Sans SemiBold'), local('OpenSans-SemiBold'), url('../../../etc/designs/girlscouts/open-sans-semibold.woff') format('woff');
}
@font-face {
  font-family: 'Open Sans';
  font-style: italic;
  font-weight: 600;
  src: local('Open Sans SemiBold'), local('OpenSans-SemiBoldIt'), url('../../../etc/designs/girlscouts/open-sans-semiboldItalic.woff') format('woff');
}
@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 700;
  src: local('Open Sans Bold'), local('OpenSans-Bold'), url('../../../etc/designs/girlscouts/open-sans-bold.woff') format('woff');
}

</style>

<html <%= wcmModeIsPreview ? "class=\"preview\"" : ""%>>
    <div id="panelWrapper" class="row  content meeting-detail">
            <!-- <div class="one-column-page-no-breadcrumb page" style="margin:0 5px"> -->
                <cq:include script="head.jsp"/>
                <cq:include script="body.jsp"/>
            <!-- </div> -->
    </div>
</html>
