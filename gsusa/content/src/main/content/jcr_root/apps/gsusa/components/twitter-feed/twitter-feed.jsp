<%@include file="/libs/foundation/global.jsp"%>

	<%
	String username = properties.get("username","");
	String widgetID = properties.get("widgetID", "");
	if(username.equals("") || widgetID.equals("")){
		%>PLEASE EDIT THE COMPONENT TO ENTER YOUR USERNAME AND WIDGET ID. FOR INFORMATION REGARDING WIDGETS, PLEASE GO TO https://dev.twitter.com/web/embedded-timelines#creating <%
    }
	else{
    	int width = properties.get("width",520);
        int height = properties.get("height",600);
        int tweetLimit = properties.get("tweetLimit",-1);
		%>
		<div class="twitter-feed" style="overflow:scroll; max-height:<%= height + "px" %>; max-width: <%= width + "px" %>">
			<a class="twitter-timeline" data-link-color="#00ae58"
				href="https://twitter.com/<%= username %>"
  				data-chrome="noheader nofooter noborders transparent"
  				data-widget-id="<%= widgetID %>"
			<%if(tweetLimit != -1){ %>
				data-tweet-limit="<%=tweetLimit%>"
            <% } %>
                height="<%= height %>"
                width="<%= width %>">
				Tweets by @<%= username %>
			</a>
		</div>
		<%
	}
    %>
