<%@include file="/libs/foundation/global.jsp"%>

    <%//Add the following js to clientlibs when possible%>
    <script>
    window.twttr = (function(d, s, id) {
  	var js, fjs = d.getElementsByTagName(s)[0],
    	t = window.twttr || {};
  	if (d.getElementById(id)) return t;
  	js = d.createElement(s);
  	js.id = id;
  	js.src = "https://platform.twitter.com/widgets.js";
  	fjs.parentNode.insertBefore(js, fjs);

  	t._e = [];
  	t.ready = function(f) {
    	t._e.push(f);
  	};
 
  	return t;
	}(document, "script", "twitter-wjs"));</script>

	<%
		String username = properties.get("username","");
		if(username.equals("")){ 
			%>PLEASE EDIT THE COMPONENT TO ENTER YOUR USERNAME <%
    	}
		else{
            int width = properties.get("width",520);
            int height = properties.get("height",600);
            int tweetLimit = properties.get("tweetLimit",-1);
			%>
				<div class="twitter-feed">
					<a class="twitter-timeline"
  					href="https://twitter.com/<%= username %>"
  					data-widget-id="600357408536436736"
					<%if(tweetLimit != -1){ %>
						data-tweet-limit="<%=tweetLimit%>"
                	<% } %>
                	height="<%= height %>"
                	width="<%= width %>"
                	data-list-owner-screen-name="<%= username %>">
					Tweets by @<%= username %>
					</a>
				</div>
			<%
		}
    %>
