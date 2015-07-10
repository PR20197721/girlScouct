<%@include file="/libs/foundation/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>
<%
	String[] emptyArray = new String[1];
    String content = properties.get("content", "Join Now");
    String btnName = properties.get("button", "Explore Girl Scouts");
    String title = properties.get("title", "Introduce girls to");
    String[] imagePathArray = properties.get("imagePath", emptyArray);
    
    String[] content2 = properties.get("content2", emptyArray);
    String[] imagePath2 = properties.get("imagePath2", emptyArray);
    String[] subtitle2 = properties.get("subtitle2", emptyArray);
    String title2 = properties.get("title2", "");
    
    String[] content3 = properties.get("content3", emptyArray);
    String[] imagePath3 = properties.get("imagePath3", emptyArray);
    String[] subtitle3 = properties.get("subtitle3", emptyArray);
    String title3 = properties.get("title3", "");
    
    String title4 = properties.get("title4", "");
    
    String title5 = properties.get("title5", "");
    String videoType51 = properties.get("videoType51", "");
    String videoType52 = properties.get("videoType52", "");
    String videoType53 = properties.get("videoType53", "");
    String videoType54 = properties.get("videoType54", "");
    String[] videoType5 = {videoType51, videoType52, videoType53, videoType54};
    String[] videoThumbNail;
    
    String title6 = properties.get("title6", "");
    String content6 = properties.get("content6", "");
    String imagePath6 = properties.get("imagePath6", "");
    
   
%>


<div class="hero-feature">
    <div class="overlay"></div>
    <ul class="main-slider">
<% 	for (int i = 0 ; i < imagePathArray.length; i++) {%>
        <li>
            <img src="<%=imagePathArray[i] %>" class="slide-thumb"/>
        </li>
    <%}
%>
    </ul>
    <div class="hero-text first">
        <section>
            <img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon" />
            <h2><%= title %></h2>
            <p><%= content %></p>
            <a href="#" class="button explore"><%= btnName %>=</a>
        </section>
    </div>
    <div class="position">
        <div class="inner-sliders">
            <ul class="inner">
                <li>
                    <ul class="slide-1"> 
                    <% 	for (int i = 0 ; i < imagePath2.length; i++) {%>
                        <li>
                            <h3><%= title2 %></h3>
                            <div class="text white">
                                <h4><%= subtitle2[i] %></h4>
                                <p><%= content2[i] %></p>
                            </div>
                            <img src="<%= imagePath2[i] %>" alt="" class="slide-thumb"/>
                        </li>
                        <%} %>
                    </ul>
                </li>
                <li>
                    <ul class="slide-2">
                    <% 	for (int i = 0 ; i < imagePath3.length; i++) {%>
                        <li>
                            <h3><%= title3 %></h3>
                            <div class="text white">
                                <h4><%= subtitle3[i] %></h4>
                                <p><%= content3[i] %></p>
                            </div>
                            <img src="<%= imagePath3[i] %>" alt="" class="slide-thumb"/>
                        </li>
                    <%} %>
                    </ul>
                    <li>
                        <ul class="slide-3">
                            <li>
                                <h3><%= title4 %></h3>
                                <cq:include path="content/facebook-feed" resourceType="gsusa/components/facebook-feed" />
                            </li>
                        </ul>
                    </li>
                    <li>
                        <ul class="slide-4">
                        <li>
                            <h3>And people are always talking agout us</h3>
                            <div class="video-wrapper">
                                <div class="video">
                                
                                  <iframe width="420" height="315" src="https://www.youtube.com/embed/WK7hb5S3Fig" frameborder="0" allowfullscreen></iframe>
                                
                                </div>
                                <div class="video-article">
                                    <h4>Splash, Paddle, and Sail</h4>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                </div>
                            </div>

                        </li>
                        <li>
                            <h3>And people are always talking agout us</h3>
                             <div class="video-wrapper">
                                <div class="video"><img src="/etc/designs/gsusa/clientlibs/images/6.png" alt="" class="slide-thumb"/></div>
                                <div class="video-article">
                                    <h4>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut</h4>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All
                                </div>
                            </div>

                        </li>
                        <li>
                            <h3>And people are always talking agout us</h3>
                             <div class="video-wrapper">
                                <div class="video"><img src="/etc/designs/gsusa/clientlibs/images/7.png" alt="" class="slide-thumb"/></div>
                                <div class="video-article">
                                    <h4>Splash, Paddle, and Sail</h4>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                </div>
                            </div>

                        </li>
                        <li>
                            <h3>And people are always talking agout us</h3>
                            <div class="video-wrapper">
                                <div class="video"><img src="/etc/designs/gsusa/clientlibs/images/8.png" alt="" class="slide-thumb"/></div>
                                <div class="video-article">
                                    <h4>Splash, Paddle, and Sail</h4>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                    <p>Learn how to launch, paddle a canoe and pilot a sailboat around the lake on an aquatic adventure. Spend a night tent camping out and cooking out. Ages 11 and up. All campers must pass a swim test and water safety training. Ages 10 and up.</p>
                                </div>
                            </div>

                        </li>
                    </ul>
                    </li>
                </li>
            </ul>
        </div>
    </div>
    <div class="final-comp">
        <div class="hero-text">
                <section>
                    <img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon">
                    <h2><%= title6%></h2>
                    <p><%= content6 %> </p>
                    <form action="#" name="join-now" class="join-now-form clearfix">
                        <input type="text" class="join-text hide" placeholder="Enter Zip code">
                        <a href="#" class="button join-now">Join Now</a>
                    </form>
                </section>
            </div>
        <img src="<%= imagePath6 %>" alt="" class="main-image" />
    </div>
    <cq:include path="content/zip-council" resourceType="gsusa/components/zip-council" />
</div>
