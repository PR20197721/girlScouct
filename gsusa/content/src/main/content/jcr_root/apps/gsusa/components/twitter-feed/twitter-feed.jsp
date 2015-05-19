<%@include file="/libs/foundation/global.jsp"%>

	<%
	String username = properties.get("username","");
	if(username.equals("")){ 
		%>PLEASE EDIT THE COMPONENT TO ENTER YOUR USERNAME <%
    }
	else{
    	int width = properties.get("width",520);
        int height = properties.get("height",600);
        int tweetLimit = properties.get("tweetLimit",-1);
        String widgetID = properties.get("widgetID", "600357408536436736");
		%>
		<div class="twitter-feed">
			<a class="twitter-timeline"
  				href="https://twitter.com/<%= username %>"
  				data-widget-id="<%= widgetID %>"
			<%if(tweetLimit != -1){ %>
				data-tweet-limit="<%=tweetLimit%>"
            <% } %>
            <% if(widgetID.equals("600357408536436736")){ 
            	//Override the default username, which in this case is dlubinNPD%>
                data-screen-name="<%= username %>"
            <% } %>
                height="<%= height %>"
                width="<%= width %>"> 
				Tweets by @<%= username %>
			</a>
		</div>
		<%
	}
    %>
