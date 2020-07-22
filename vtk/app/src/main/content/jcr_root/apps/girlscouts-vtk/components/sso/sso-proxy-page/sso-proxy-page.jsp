<%@page session="false" %>
<%@ include file="/libs/foundation/global.jsp" %>
<%@ page import="org.apache.jackrabbit.api.security.user.*,
                 org.apache.commons.lang.StringUtils,
                 org.apache.sling.api.request.RequestParameter,
                 org.apache.sling.api.resource.ResourceResolver,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.jcr.api.SlingRepository" %>
<!DOCTYPE HTML>
<html>
<head>
    <!-- page category = DEFAULT -->
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="">
    <meta name="description" content="">
    <!-- apps/girlscouts/components/page/headlibs.jsp -->
    <script src="https://cdns.gigya.com/js/gigya.saml.js?apiKey=3_NLXR60j9urobE-Xha85pPHh1aIok8tcX3EleNguNPoil0e_8omSl6c4jE1Ip33E0">
    {
        loginURL:"http://localhost:4503/content/girlscouts-vtk/sso/login.html",
        logoutURL: "http://localhost:4503/content/girlscouts-vtk/sso/logout.html"
    }
	</script>
    <link rel="stylesheet" href="/etc.clientlibs/foundation/clientlibs/main.min.css" type="text/css"/>
    <script type="text/javascript" src="/etc.clientlibs/clientlibs/granite/jquery.min.js"></script>
    <script type="text/javascript" src="/etc.clientlibs/clientlibs/granite/utils.min.js"></script>
    <script type="text/javascript" src="/etc.clientlibs/clientlibs/granite/jquery/granite.min.js"></script>
    <script type="text/javascript" src="/etc.clientlibs/foundation/clientlibs/jquery.min.js"></script>
    <script type="text/javascript" src="/etc.clientlibs/foundation/clientlibs/shared.min.js"></script>
    <script type="text/javascript" src="/etc.clientlibs/foundation/clientlibs/main.min.js"></script>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <!-- Begin: Include Girl Scout clientlibs -->
    <!-- Artifact Browser -->
    <!--[if lt IE 9]>
    <link rel="stylesheet" href="/etc/designs/girlscouts/clientlibs.ie8.css" type="text/css">
    <script type="text/javascript" src="/etc/designs/girlscouts/clientlibs.ie8.js"></script>
    <![endif]-->
    <!-- Modern Browser -->
    <!--[if gt IE 8]><!-->
    <link rel="stylesheet" href="/etc/designs/girlscouts/clientlibs.modern.css" type="text/css">
    <script type="text/javascript" src="/etc/designs/girlscouts/clientlibs.modern.js"></script>
    <!--<![endif]-->
    <link href="/etc/designs/girlscouts-usa-green/static.css" rel="stylesheet" type="text/css">
    <link href="/etc/designs/girlscouts-usa-green.css" rel="stylesheet" type="text/css">
    <!-- End: Include Girl Scout clientlibs -->
    <!-- Begin: login logic -->
    <script type="text/javascript">
        var fixVerticalSizing = true;
    </script>
    <!-- End: login logic -->
    <title>SSO Proxy Page</title>
</head>
<!-- apps/girlscouts/components/page/body.jsp -->
<body>
<div class="off-canvas-wrap">
    <div class="inner-wrap">
        <!-- apps/girlscouts/components/page/header.jsp -->
        <!-- Modern Browser -->
        <!--[if gt IE 8]><!-->
        <!--<![endif]-->
        <!--PAGE STRUCTURE: HEADER-->
        <div id="header" class="row">
            <div class="large-5 medium-10 small-24 columns logoContainer">
                <div class="logo">
                    <!-- Artifact Browser -->
                    <!--[if lt IE 9]>
                    <nav class="logoLarge">
                        <img src="/content/dam/logos/GSUSA_servicemark.png" alt="Home"/>
                    </nav>
                    <![endif]-->
                    <!-- Modern Browser -->
                    <!--[if gt IE 8]><!-->
                    <nav class="hide-for-small logoLarge logoLargePadding">
                        <img src="/content/dam/girlscouts-shared/images/logo/medium/GSUSA_servicemark.png"
                             alt="Home" id="logoImg"/>
                    </nav>
                    <nav class="show-for-small logoSmall">
                        <img src="/content/dam/girlscouts-shared/images/logo/medium/GSUSA_servicemark.png"
                             alt="Home" width="38" height="38"/>
                    </nav>
                    <!--<![endif]-->
                </div>
                <div class="placeholder"></div>
            </div>
            <div class="large-19 medium-14 hide-for-small columns topMessage">
                <div class="noLeftPadding eyebrow-nav navigation-bar eyebrow-navigation columns">
                    <ul class="inline-list eyebrow-fontsize">
                    </ul>
                </div>
                <div class="row">
                    <div class="large-18 columns small-24 login medium-18">
                        &nbsp;
                    </div>
                    <div class="searchBar columns medium-6 small-24 search-box large-6">
                    </div>
                </div>
                <div class="emptyrow">&nbsp;</div>
            </div>
        </div>
        <!--PAGE STRUCTURE: HEADER BAR-->
        <div id="headerBar" class="row collapse hide-for-small">
            <div class="global-navigation large-19 medium-23 global-nav columns small-24 large-push-5">
                <!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
                <ul class="inline-list">
                </ul>
            </div>
        </div>
        <!-- apps/girlscouts/components/three-column-page/content.jsp -->
        <!--PAGE STRUCTURE: MAIN-->
        <div id="main" class="row">
            <!--PAGE STRUCTURE: LEFT CONTENT START-->
            <div class="large-5 hide-for-medium hide-for-small columns mainLeft">
                <div id="leftContent">
                    <!-- apps/girlscouts/components/three-column-page/left.jsp -->
                    <div class="cascading-menus">
                        </ul>
                        <script>
                            $(document).ready(function () {
                                $('#main .side-nav li.active.current').parent().parent().find(">div>a").css({
                                    "font-weight": "bold",
                                    "color": "#414141"
                                });
                            });
                        </script>
                    </div>
                    <div class="par parsys">
                    </div>
                </div>
            </div>
            <!--PAGE STRUCTURE: LEFT CONTENT STOP-->
            <!--PAGE STRUCTURE: MAIN CONTENT START-->
            <div class="large-19 medium-24 small-24 columns mainRight">
                <div class="breadcrumbWrapper">
                    <div class="breadcrumb-trail breadcrumb">
                        <nav class="breadcrumbs">
                            <span class="breadcrumbCurrent"></span>
                        </nav>
                    </div>
                </div>
                <div class="row mainRightBottom">
                    <div class="large-18 medium-18 small-24 columns rightBodyLeft">
                        <!--PAGE STRUCTURE: MIDDLE CONTENT START-->
                        <!-- apps/girlscouts/components/three-column-page/middle.jsp -->
        <div id="mainContent"><br/><br/><br/><br/><br/>
        					<p>
                            Please wait...
        					</p><br/><br/><br/><br/><br/>
                        </div>
                        <!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
                    </div>
                    <!--PAGE STRUCTURE: RIGHT CONTENT START-->
                    <div id="rightContent" class="large-6 medium-6 small-24 columns">
                        <!-- apps/girlscouts/components/three-column-page/right.jsp -->
                        <div class="advertisement">
                        </div>
                    </div>
                    <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
                </div>
            </div>
            <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
        </div>
        <!-- apps/girlscouts/components/page/footer.jsp -->
        <!-- web/app/src/main/content/jcr_root/apps/girlscouts/components/page/footer.jsp -->
        <div id="footer" class="hide-for-small row">
            <div class="large-24 footer-navigation medium-24 navigation-bar columns nav small-24">
              <div class="footerLinks">
                Privacy Policy
                
                Terms and Conditions
                
                </div>
            </div>
        </div>
        <div id="mobile-nav-footer" class="show-for-small collapse">
            <div class="footer-navigation navigation-bar nav">
                <div class="footerLinksMobile">
                    <ul id="smallFooterLinks" class="small-block-grid-2">
                    </ul>
                </div>
            </div>
        </div>
        <div id="mobile-footer" class="centered-table show-for-small">
            <div class="logo">
                <!-- Artifact Browser -->
                <!--[if lt IE 9]>
                <nav class="logoLarge">
                    <img src="/content/dam/logos/GSUSA_servicemark.png" alt="Home"/>
                </nav>
                <![endif]-->
                <!-- Modern Browser -->
                <!--[if gt IE 8]><!-->
                <nav class="show-for-small mobileFooterLogo">
                    <img src="/content/dam/girlscouts-shared/images/logo/medium/GSUSA_servicemark.png"
                         alt="Home" width="188" height="73"/>
                </nav>
                <!--<![endif]-->
            </div>
        </div>

    </div>
</div>
<div id="gsModal"></div>
</body>
</html>