<%@page session="false" %><%--
  Copyright 1997-2010 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Default head script.

  Draws the HTML head with some default content:
  - includes the WCML init script
  - includes the head libs script
  - includes the favicons
  - sets the HTML title
  - sets some meta data

  ==============================================================================

--%>
<%@include file="/libs/foundation/global.jsp" %>
<%
%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@ page import="com.day.cq.commons.Doctype,
                 org.apache.sling.api.SlingHttpServletRequest,
                 org.apache.sling.settings.SlingSettingsService" %>
<%
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    String ogTitle = properties.get("ogTitle", "");
    String ogSiteName = properties.get("ogSiteName", "Girl Scouts");
    String ogUrl = properties.get("ogUrl", "");
    String ogDescription = properties.get("ogDescription", "");
    String ogImage = properties.get("ogImage", "");
    String reqProtocol = request.getHeader("X-Forwarded-Proto");
    Page parentPage = currentPage.getAbsoluteParent(2);
    String canonicalUrl = properties.get("canonicalUrl", "");
    if ("".equals(canonicalUrl) == false) {
        // resolve only if this is relative path
        if (canonicalUrl.startsWith("/")) {
            Page canonicalUrlPage = resourceResolver.resolve(canonicalUrl).adaptTo(Page.class);
            if (canonicalUrlPage != null && !canonicalUrl.contains(".html")) {
                canonicalUrl += ".html";
            }
            Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
            canonicalUrl = externalizer.absoluteLink((SlingHttpServletRequest) request, reqProtocol, canonicalUrl);
            canonicalUrl = canonicalUrl.replace(":80/", "/");
        }
    }
    String fbAppId = parentPage.getProperties().get("facebookId", "");
    if (!"".equals(properties.get("fbAppId", ""))) {
        fbAppId = properties.get("fbAppId", "");
    }
    if (reqProtocol == null) {
        reqProtocol = "http";
    }
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    } else {
        if (favIcon.startsWith("/")) {
            Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
            favIcon = externalizer.absoluteLink((SlingHttpServletRequest) request, reqProtocol, favIcon);
            favIcon = favIcon.replace(":80/", "/");
        }
    }
%><head>
    <%
        String pageCategory = "DEFAULT";
        Object pageCategoryObject = request.getAttribute("PAGE_CATEGORY");
        if (pageCategoryObject != null) {
            pageCategory = (String) pageCategoryObject;
        }
    %>
    <!-- page category = <%= pageCategory%> -->
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:site" content="@girlscouts"/>
    <% if (ogTitle.length() > 0) {%>
    <meta property="og:title" content="<%=ogTitle %>"/>
    <meta name="twitter:title" content="<%=ogTitle %>"/>
    <%} %>
    <% if (ogSiteName.length() > 0) {%>
    <meta property="og:site_name" content="<%=ogSiteName %>"/>
    <%} %>
    <% if (ogUrl.length() > 0) {%>
    <meta property="og:url" content="<%=ogUrl%>"/>
    <%} %>
    <% if (ogDescription.length() > 0) {%>
    <meta property="og:description" content="<%=ogDescription %>"/>
    <meta name="twitter:description" content="<%=ogDescription %>"/>
    <%} %>
    <% if (ogImage.length() > 0) {
        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
        ogImage = externalizer.absoluteLink((SlingHttpServletRequest) request, reqProtocol, ogImage);
        ogImage = ogImage.replace(":80/", "/");
    %>
    <meta property="og:image" content="<%=ogImage %>"/>
    <meta name="twitter:image" content="<%=ogImage %>"/>
    <%} %>
    <% if (fbAppId.length() > 0) {%>
    <meta property="fb:app_id" content="<%=fbAppId %>"/>
    <%} %>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>"<%=xs%>>
    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
    <meta name="twitter:description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"/>
    <% if (canonicalUrl.length() > 0) {%>
    <link rel="canonical" href="<%=canonicalUrl%>"/>
    <% } %>
    <cq:include script="headlibs.jsp"/>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:include script="stats.jsp"/>
    <%
        if (favIcon != null) {
    %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= favIcon %>"<%=xs%>>
    <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= favIcon %>"<%=xs%>>
    <%
        }
        String title = "";
        try {
            title = currentPage.getContentResource().adaptTo(ValueMap.class).get("seoTitle", "");
            if (title.isEmpty()) {
                title = currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle();
            }
        } catch (Exception e) {
        }
        title = xssAPI.encodeForHTML(title);
        if ("VTK".equals(pageCategory)) {
    %>
    <link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs.css" type="text/css"/>
    <script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs.js"></script>
    <%
        }
    %>
    <!--//loading a font file for all the small icons on the site-->
    <link rel="stylesheet" href="/etc/designs/girlscouts/fonts/style.css" type="text/css"/>
    <title><%= title %>
    </title>
    <%
        Boolean googleOptimize = currentSite.get("./googleOptimize", Boolean.FALSE);
        String googleOptimizeId = currentSite.get("googleOptimizeId", "");
        if (googleOptimize) {
    %>
    <style>.async-hide {
        opacity: 0 !important
    } </style>
    <script>(function (a, s, y, n, c, h, i, d, e) {
        s.className += ' ' + y;
        h.start = 1 * new Date;
        h.end = i = function () {
            s.className = s.className.replace(RegExp(' ?' + y), '')
        };
        (a[n] = a[n] || []).hide = h;
        setTimeout(function () {
            i();
            h.end = null
        }, c);
        h.timeout = c;
    })(window, document.documentElement, 'async-hide', 'dataLayer', 4000,
        {'<%=googleOptimizeId%>': true});</script>
    <%
        }
    %>
    <%
        String id = currentSite.get("gtmId", "");
        if (!id.isEmpty()) {
    %>
    <!-- Google Tag Manager -->
    <script>(function (w, d, s, l, i) {
        w[l] = w[l] || [];
        w[l].push({
            'gtm.start':
                new Date().getTime(), event: 'gtm.js'
        });
        var f = d.getElementsByTagName(s)[0],
            j = d.createElement(s), dl = l != 'dataLayer' ? '&l=' + l : '';
        j.async = true;
        j.src =
            '//www.googletagmanager.com/gtm.js?id=' + i + dl;
        f.parentNode.insertBefore(j, f);
    })(window, document, 'script', 'dataLayer', '<%= id %>');</script>
    <!-- End Google Tag Manager -->
    <% } %>
    <%-- <GSWP-2256> --%>
    <!-- Google Tag Manager -->
    <script>(function (w, d, s, l, i) {
        w[l] = w[l] || [];
        w[l].push({
            'gtm.start':
                new Date().getTime(), event: 'gtm.js'
        });
        var f = d.getElementsByTagName(s)[0],
            j = d.createElement(s), dl = l != 'dataLayer' ? '&l=' + l : '';
        j.async = true;
        j.src =
            'https://www.googletagmanager.com/gtm.js?id=' + i + dl;
        f.parentNode.insertBefore(j, f);
    })(window, document, 'script', 'dataLayer', 'GTM-TGGWNNL');</script>
    <!-- End Google Tag Manager -->
    <%-- </GSWP-2256> --%>
    <% if (!googleOptimizeId.isEmpty()) { %>
        <!-- Google Optimize code -->
        	<script src="https://www.googleoptimize.com/optimize.js?id=<%=googleOptimizeId%>"</script>
    	<!-- Google Optimize code -->
    <% } %>
</head>
