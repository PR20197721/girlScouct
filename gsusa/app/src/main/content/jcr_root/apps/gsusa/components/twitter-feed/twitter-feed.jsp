<%@include file="/libs/foundation/global.jsp"%>

	<%
	String username = properties.get("username","");
	String widgetID = properties.get("widgetID", "");
	if(username.equals("") || widgetID.equals("")){
		%>PLEASE EDIT THE COMPONENT TO ENTER YOUR USERNAME AND WIDGET ID. FOR INFORMATION REGARDING WIDGETS, PLEASE GO TO https://dev.twitter.com/web/embedded-timelines#creating <%
    }
	else{
    	int width = properties.get("width",520);
        int height = properties.get("height",460);
        int tweetLimit = properties.get("tweetLimit",20);
		%>
        <div  id="tag_social_feed_twitter" class="wrapper clearfix">
            <span class="icon-social-twitter-tweet-bird"></span>
    		<div class="social-block">
                <div class="block-area">
        			<a class="twitter-timeline" data-link-color="#00ae58"
        				href="https://twitter.com/<%= username %>"
          				data-chrome="noheader nofooter noborders transparent"
          				data-widget-id="<%= widgetID %>"
                        data-aria-polite="assertive"
        			<%if(tweetLimit != -1) {//Previously used to give option of unlimited tweets %>
        				data-tweet-limit="<%=tweetLimit%>"
                    <% } %>
                        width="100%" height="460px">
        				Tweets by @<%= username %>
        			</a>
                    <p class="centered"><a href="https://twitter.com/girlscouts" title="see more on twitter">See more</a></p>
                </div>
    		</div>
        </div>
		<%
	}
    %>
