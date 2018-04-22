<%
	String path=(String)request.getAttribute("path");
	System.out.println("IN NEWS RENDER PATH IS: " + path);
	String title=(String)request.getAttribute("title");
	String date=(String)request.getAttribute("date");
	String date_yyyyMMdd=(String)request.getAttribute("date_yyyyMMdd");
	String text=(String)request.getAttribute("text");
	String external_url=(String)request.getAttribute("external_url");

	int MAX_MORE_LENGTH = 1500;
	boolean CLEAN = true;
	if (text.length() > MAX_MORE_LENGTH) {
		org.jsoup.select.Elements docBodyElements = null;
		try {
		    // clean up html of substring.
		    org.jsoup.nodes.Document fragment =  org.jsoup.Jsoup.parse(text.substring(0,MAX_MORE_LENGTH));
		    org.jsoup.nodes.Document.OutputSettings settings = fragment.outputSettings();
		    settings.prettyPrint(false);
		    settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended);
		    settings.charset("ASCII");
		    docBodyElements = fragment.select("body").get(0).children();
		    // inject ...
		    docBodyElements.get(docBodyElements.size()-1).text(docBodyElements.get(docBodyElements.size()-1).text() + "...");
		} catch (Exception e) {
		    // something went wrong with the parsing
		    docBodyElements = null;
		}
		if (CLEAN && docBodyElements != null) {
		    if (!external_url.isEmpty()){
			text = docBodyElements.outerHtml() + " <br/><br/><center>[&nbsp;<a href=\"" + external_url + "\" target=\"_blank\">Read Full News</a>&nbsp;]</center>";
		    } else {
			text = docBodyElements.outerHtml() + " <br/><br/><center>[&nbsp;<a href=\"" + path + ".html\">Read Full Article</a>&nbsp;]</center>";
		    }
		} else {
		    if (!external_url.isEmpty()){
			text = text.substring(0,MAX_MORE_LENGTH) + "... <br/><br/><center>[&nbsp;<a href=\"" + external_url + "\" target=\"_blank\">Read Full News</a>&nbsp;]</center>";
		    } else {
			text = text.substring(0,MAX_MORE_LENGTH) + "... <br/><br/><center>[&nbsp;<a href=\"" + path + ".html\">Read Full Article</a>&nbsp;]</center>";
		    }
		}
	}
%> 

<li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem"> 
	<h2>
<%if(!external_url.isEmpty()){ %>
	<a href="<%=external_url%>" target="_blank" itemprop="name"><%=title%></a>
<%}else{ %>
	<a href="<%=path%>.html" itemprop="name"><%=title%></a>
<%} %>
	</h2>
<%
	if (date != null && date.length() > 0) {
%>
<span itemprop="datePublished" content="<%=date_yyyyMMdd%>"><%=date%></span>
<br/>
<%
	}
%>
		<article itemscope itemprop="description"><%=text %></article>
	</li>

