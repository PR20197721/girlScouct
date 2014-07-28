<%
	String path=(String)request.getAttribute("path");
	String title=(String)request.getAttribute("title");
	String date=(String)request.getAttribute("date");
//	String text=org.jsoup.Jsoup.parse((String)request.getAttribute("text")).text();
	String text=(String)request.getAttribute("text");
	String external_url=(String)request.getAttribute("external_url");

	int MAX_MORE_LENGTH = 1500;

	if (text.length() > MAX_MORE_LENGTH) {
		if (!external_url.isEmpty()){
                        text = text.substring(0,MAX_MORE_LENGTH) + "... <br/><br/><center>[&nbsp;<a href=\"" + external_url + "\" target=\"_blank\">Read Full News</a>&nbsp;]</center>";
		} else {
			text = text.substring(0,MAX_MORE_LENGTH) + "... <br/><br/><center>[&nbsp;<a href=\"" + path + ".html\">Read Full Article</a>&nbsp;]</center>";
		}
	}
%> 
<ul class="searchResultsList">
<li> 
	<h2>
<%if(!external_url.isEmpty()){ %>
	<a href="<%=external_url%>" target="_blank"><%=title%></a>
<%}else{ %>
	<a href="<%=path%>.html"><%=title%></a>
<%} %>
	</h2>
<%
	if (date != null && date.length() > 0) {
%>
<%=date%><br/>
<%
	}
%>
		<article><%=text %></article>
	</li>
</ul>
