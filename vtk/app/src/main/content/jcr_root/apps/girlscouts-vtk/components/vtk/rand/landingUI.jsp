	<!DOCTYPE HTML>
<html >
<head>

<!-- page category = DEFAULT -->
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="keywords" content="">
<meta name="description" content="">







<!-- apps/girlscouts/components/page/headlibs.jsp -->
<link rel="stylesheet" href="/etc/clientlibs/foundation/main.css" type="text/css">
<script type="text/javascript" src="/etc/clientlibs/granite/jquery.js"></script>
<script type="text/javascript" src="/etc/clientlibs/granite/utils.js"></script>
<script type="text/javascript" src="/etc/clientlibs/granite/jquery/granite.js"></script>
<script type="text/javascript" src="/etc/clientlibs/foundation/jquery.js"></script>
<script type="text/javascript" src="/etc/clientlibs/foundation/main.js"></script>



<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
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
$(document).ready(function() {
girlscouts.components.login.init('en', 'null?action=signin', 'null?action=signout');
if (window.location.href.indexOf('isSignOutSalesForce=true') != -1) {
$.removeCookie('girl-scout-name');
}
var name = $.cookie('girl-scout-name');
if (name) {
girlscouts.components.login.sayHello('signedin', name);
} else {
girlscouts.components.login.genCode('null');
}
});
</script>
<!-- End: login logic -->



<title>login_jump</title>
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
<a href="/content/girlscouts-template/en.html">
<img src="/content/dam/logos/GSUSA_servicemark.png" alt="Home" id="logoImg"/>
</a>
</nav>
<nav class="show-for-small logoSmall">
<a href="/content/girlscouts-template/en.html">
<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" alt="Home" width="38" height="38"/>
</a>
</nav>

<!--<![endif]-->
</div>

<div class="placeholder"></div>

</div>
<div class="large-19 medium-14 hide-for-small columns topMessage">
<div class="noLeftPadding eyebrow-nav navigation-bar eyebrow-navigation columns">


<ul class="inline-list eyebrow-fontsize">









<li>
<a href="#">Shop</a></li>
<li>
<a href="#">Forms</a></li>
<li>
<a href="#">Calendar</a></li>
<li>
<a href="#">Contact</a></li>


</ul>
</div>

<div class="row">
<div class="large-18 columns small-24 login medium-18">


&nbsp;</div>

<div class="searchBar columns medium-6 small-24 search-box large-6">







<form action="/content/girlscouts-template/en/site-search.html" method="get">
<input type="text" name="q" placeholder="" class="searchField" />
</form>

</div>

</div>
<div class="emptyrow">&nbsp;</div>
</div>
<div class="show-for-small small-24 columns topMessage alt">
<div class="row vtk-login">
<div class="small-12 columns login">


&nbsp;</div>

<div class="small-12 columns">
<div class="small-search-hamburger">
<a class="search-icon"><img src="/etc/designs/girlscouts-usa-green/images/search_white.png" width="21" height="21" alt="search icon"/></a>
<a class="right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts-usa-green/images/hamburger.png" width="22" height="28" alt="toggle hamburger side menu icon"/></a>
</div>
</div>
</div>
<div class="row hide srch-box">
<div class="hide small-20 srch-box columns search-box">







<form action="/content/girlscouts-template/en/site-search.html" method="get">
<input type="text" name="q" placeholder="" class="searchField" />
</form>

</div>

<div class="small-4 columns">
<a class="right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts-usa-green/images/hamburger.png" width="22" height="28" alt="right side menu hamburger icon"/></a>
</div>
</div>
</div>
</div>
<!--PAGE STRUCTURE: HEADER BAR-->
<div id="headerBar" class="row collapse hide-for-small">
<div class="global-navigation large-19 medium-23 global-nav columns small-24 large-push-5">






<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->

<ul class="inline-list">

<li class="">
<a class="show-for-large-up menu " href="/content/girlscouts-template/en/about-girl-scouts/who-we-are.html">ABOUT GIRL SCOUTS</a>
<a class="show-for-medium-only menu " href="/content/girlscouts-template/en/about-girl-scouts/who-we-are.html"> ABOUT</a>
</li>

<li class="">
<a class="show-for-large-up menu " href="#">EVENTS </a>
<a class="show-for-medium-only menu " href="#"> EVENTS</a>
</li>

<li class="">
<a class="show-for-large-up menu " href="#">CAMP</a>
<a class="show-for-medium-only menu " href="#"> CAMP</a>
</li>

<li class="">
<a class="show-for-large-up menu " href="/content/girlscouts-template/en/cookies/about-girl-scout-cookies.html">COOKIES</a>
<a class="show-for-medium-only menu " href="/content/girlscouts-template/en/cookies/about-girl-scout-cookies.html"> COOKIES</a>
</li>

<li class="">
<a class="show-for-large-up menu " href="/content/girlscouts-template/en/for-volunteers/why-volunteer.html">VOLUNTEER</a>
<a class="show-for-medium-only menu " href="/content/girlscouts-template/en/for-volunteers/why-volunteer.html"> VOLUNTEER</a>
</li>

<li class="">
<a class="show-for-large-up menu " href="#">OUR COUNCIL</a>
<a class="show-for-medium-only menu " href="#"> OUR COUNCIL</a>
</li>

<li class="">
<a class="show-for-large-up menu menuHighlight" href="/content/girlscouts-vtk/en/vtk.html">My GS</a>
<a class="show-for-medium-only menu menuHighlight" href="/content/girlscouts-vtk/en/vtk.html"> My GS</a>
</li>

</ul>

</div>

<div class="small-search-hamburger show-for-medium medium-1 columns">
<a class="show-for-medium right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts-usa-green/images/hamburger.png" width="19" height="28" alt="side menu icon"></a>
</div>
</div>
<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->

<aside class="right-off-canvas-menu">

<div class="global-navigation global-nav">






<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->



<div id="right-canvas-menu">
<ul class="side-nav" style="padding:0px">

<li>
<div><a href="/content/girlscouts-template/en/about-girl-scouts/who-we-are.html"> ABOUT</a></div>
</li>

<li>
<div><a href="#"> EVENTS</a></div>
</li>

<li>
<div><a href="#"> CAMP</a></div>
</li>

<li>
<div><a href="/content/girlscouts-template/en/cookies/about-girl-scout-cookies.html"> COOKIES</a></div>
</li>

<li>
<div><a href="/content/girlscouts-template/en/for-volunteers/why-volunteer.html"> VOLUNTEER</a></div>
</li>

<li>
<div><a href="#"> OUR COUNCIL</a></div>
</li>

<li>
<div><a href="/content/girlscouts-vtk/en/vtk.html"> My GS</a></div>
</li>

</ul>
</div>
</div>


<div class="eyebrow-nav navigation-bar eyebrow-navigation">

<div id="right-canvas-menu-bottom">
<ul>









<li>
<a href="#">Shop</a></li>
<li>
<a href="#">Forms</a></li>
<li>
<a href="#">Calendar</a></li>
<li>
<a href="#">Contact</a></li>


</ul>
</div>




</div>

</aside>
<a class="exit-off-canvas"></a>


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
$(document).ready(function() {
$('#main .side-nav li.active.current').parent().parent().find(">div>a").css({"font-weight":"bold", "color":"#414141"});
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

<span class="breadcrumbCurrent">login_jump</span>

</nav>
</div>

</div>
<div class="row mainRightBottom">
<div class="large-18 medium-18 small-24 columns rightBodyLeft">
<!--PAGE STRUCTURE: MIDDLE CONTENT START-->





<!-- apps/girlscouts/components/three-column-page/middle.jsp -->
<div id="mainContent">
<div class="par parsys"><div class="text parbase section">
<h1>Welcome.</h1>



</div>
<div class="text parbase section">
<p><br />
<br />
</p>



</div>
<div class="grid-system nopadding section">





<ul class="large-block-grid-2 medium-block-grid-2 small-block-grid-1 "><li><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td><img src="/content/dam/buttons/btn_VTK.jpg"/></td>
</tr><tr style="background-color: white;border: none;"><td>If you&rsquo;re a Daisy, Brownie, or Junior troop leader, go here for access to an action-packed year of activities. You&rsquo;ll find everything you need for a fun-filled year all in one place&mdash;including meeting-by-meeting breakdowns of what to do, resources, meeting aids, and more!</td>
</tr></tbody></table>



</div>
</li><li><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td><img src="/content/dam/buttons/btn_member_profile.jpg"/></td>
</tr><tr style="background-color: white;border: none;"><td>Do you want to change your member profile or contact details? Do you need to renew a membership? Go to the Girl Scout Member Community for access to your member profile.</td>
</tr></tbody></table>



</div>
</li><div style="clear:both"></div>
</ul></div>
<div class="text parbase section">

</div>

</div>

</div>

<!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
</div>
<!--PAGE STRUCTURE: RIGHT CONTENT START-->
<div id="rightContent" class="large-6 medium-6 small-24 columns">





<!-- apps/girlscouts/components/three-column-page/right.jsp -->
<div class="advertisement">










<script type="text/javascript">
$('.advertisement').load('/content/girlscouts-template/en/ads.10.html');
</script>

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

<a class="menu" href="/content/girlscouts-usa/en/website/privacy-policy">Privacy Policy</a>

<a class="menu" href="/content/girlscouts-usa/en/website/terms-and-conditions">Terms and Conditions</a>

</div>

</div>

</div>
<div id="mobile-nav-footer" class="show-for-small collapse">

<div class="footer-navigation navigation-bar nav">






<div class="footerLinks">

<ul id="smallFooterLinks" class="small-block-grid-2">

<li>
<a class="text-center menu" href="/content/girlscouts-usa/en/website/privacy-policy">Privacy Policy</a>
</li>

<li>
<a class="text-center menu" href="/content/girlscouts-usa/en/website/terms-and-conditions">Terms and Conditions</a>
</li>

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
<img src="/content/dam/logos/GSUSA_servicemark.png" alt="Home" width="188" height="73"/>
</nav>

<!--<![endif]-->
</div>

</div>

</div>
</div>
<div id="gsModal"></div>
</body>

</html>