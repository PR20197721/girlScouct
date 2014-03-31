<%--
  Copyright 1997-2010 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Includes the scripts and css to be included in the head tag

  ==============================================================================

--%><%@ page session="false" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><cq:includeClientLib categories="cq.foundation-main"/><%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/><%
    currentDesign.writeCssIncludes(pageContext); %>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<!-- Begin: Include Girl Scout clientlibs -->
<!-- Artifact Browser -->
<!--[if lt IE 9]>
        <link rel="stylesheet" href="/etc/designs/girlscouts/clientlibs.foundation/css/foundation-ie8.css" />
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
        <link rel="stylesheet" href="/etc/designs/girlscouts/clientlibs.foundation/css/foundation.css" />
<!--<![endif]-->

<cq:includeClientLib categories="apps.girlscouts" />
<!-- End: Include Girl Scout clientlibs -->
