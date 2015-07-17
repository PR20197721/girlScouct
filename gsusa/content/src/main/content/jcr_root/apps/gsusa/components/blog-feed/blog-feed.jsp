<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
String fbPage = properties.get("fbPage","GirlScoutsUSA");
	%>
        <div class="wrapper clearfix">
            <div class="social-block">
                <span class="icon-social-facebook"></span>
                <div class="blog-feed-area block-area">
                    <div class="fb-page" data-href="https://www.facebook.com/<%= fbPage %>" data-small-header="false" data-adapt-container-width="true" data-width="359px" data-hide-cover="false" data-show-facepile="true" data-show-posts="true"><div class="fb-xfbml-parse-ignore"><blockquote cite="https://www.facebook.com/<%= fbPage %>"><a href="https://www.facebook.com/<%= fbPage %>"><%= fbPage %></a></blockquote></div></div>
                </div>
                <p class="centered"><a href="https://www.facebook.com/GirlScoutsUSA" title="see more on facebook">See more</a></p>
            </div>
            <span class="scroll-more"></span>
        </div>

        <!-- Facebook logic is mostly gone since it doesn't have an image area anymore -->
