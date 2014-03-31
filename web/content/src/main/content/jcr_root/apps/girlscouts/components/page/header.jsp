<%@include file="/libs/foundation/global.jsp" %>
<%
	String contentPath = currentPage.getAbsoluteParent(2).getContentResource().getPath();
	String parPath = contentPath + "/header/par";
%>
        <div class="off-canvas-wrap">
            <div class="inner-wrap">
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
                <aside class="right-off-canvas-menu">
                    <ul class="off-canvas-list">
                        <li><label class="first">Foundation</label></li>
                        <li><a href="index.html">Home</a></li>
                    </ul>
                    <hr>
                    <ul class="off-canvas-list">
                        <li><label class="first">Learn</label></li>
                        <li><a href="learn/features.html">Features</a></li>
                        <li><a href="learn/faq.html">FAQ</a></li>
                    </ul>
                    <hr>
                    <ul class="off-canvas-list">
                        <li><label>Develop</label></li>
                        <li><a href="templates.html">Add-ons</a></li>
                        <li><a href="docs">Docs</a></li>
                    </ul>
                    <hr>
                    <div class="zurb-links">
                        <ul class="top">
                            <li><a href="http://zurb.com/about">About</a></li>
                            <li><a href="http://zurb.com/blog">Blog</a></li>
                            <li><a href="http://zurb.com/contact">Contact</a></li>
                        </ul>
                    </div>
                </aside>
<!--<![endif]-->

<!--PAGE STRUCTURE: HEADER-->
                <div id="header" class="row">
                    <div class="large-4 medium-5 small-24 columns">
<!-- Artifact Browser -->
<!--[if lt IE 9]>
                        <nav class="logoLarge">
<%@include file="logo.jsp"%>
                        </nav>
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
                        <nav class="hide-for-small logoLarge">
<%@include file="logo.jsp"%>
                        </nav>
                        <nav class="show-for-small logoSmall debug">
                            <img src="images/gateway-logo-small.png" width="293" height="51" class="debug"/>
                            <a class="right-off-canvas-toggle menu-icon debug"><img src="images/hamburger.png" width="22" height="28" class="debug"/></a>
                        </nav>
<!--<![endif]-->
                    </div>
                    <div class="large-20 medium-19 small-24 columns">
                        <div class="row">
                            <div class="large-17 medium-17 hide-for-small columns toplinks">
                                <ul class="inline-list">
                                    <li><a href="/about">About Our Council</a></li>
                                    <li><a href="/shop">Shop</a></li>
                                    <li><a href="/forms">Forms</a></li>
                                    <li><a href="/calendar">Calendar</a></li>
                                    <li><a href="/contact">Contact</a></li>
                                    <li><a href="/espanol">Espanol</a></li>
                                </ul>
                            </div>
                            <div class="large-7 medium-7 small-24 columns searchBar">
                                <form action="/search" method="get">
                                    <input type="text" class="searchField"/>
                                </form>
                            </div>
                        </div>
                        <div class="row">
                            <div class="message large-24 medium-24 small-24 columns">
                                <span>Hello Sandy.</span> <a href="/signout" class="signout">SIGN OUT</a>
                            </div>
                        </div>
                        <div class="row">
                            <div class="large-24 medium-24 small-24 columns">
<cq:include path="<%= parPath %>" resourceType="foundation/components/parsys" />
                            </div>
                        </div>
                    </div>
                </div>
<!--PAGE STRUCTURE: HEADER BAR-->
                <div id="headerBar" class="row">
                    <div class="large-5 medium-5 hide-for-small columns">&nbsp;</div>
                    <div class="large-19 medium-19 hide-for-small columns" >
                        <ul class="inline-list">
                            <li><a class="menu" href="/aboutgs">About Girl Scouts</a></li>
                            <li><a class="menu" href="/events">Events &amp; Activites</a></li>
                            <li><a class="menu" href="/camp">Camp</a></li>
                            <li><a class="menu" href="/cookies">Cookies</a></li>
                            <li><a class="menu" href="/volunteers">For Volunteers</a></li>
                            <li><a class="menu menuHighlight" href="/leader">Leader</a></li>
                        </ul>
                    </div>
                </div>
