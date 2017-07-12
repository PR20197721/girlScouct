<%--
  Related Articles component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page session="false" %>
<%@page import="com.day.cq.wcm.api.WCMMode,
    			java.util.List,
				java.util.ArrayList,
				com.day.cq.search.QueryBuilder,
				com.day.cq.search.result.Hit"%>
<%
	String title = properties.get("title", "Related Articles");
	String pullFromFeed = properties.get("pullFromFeed", "false");
	String hasBorderLine = properties.get("borderLine", String.class);

	String article1 = "";
	String article2 = "";
	String article3 = "";
	if(pullFromFeed.equals("true")){
		int feedLimit = Integer.parseInt(properties.get("feedLimit", "3"));
        if(feedLimit > 3 || feedLimit < 1)
            feedLimit = 3;
        String sortByPriority = properties.get("sortByPriority", "false");
        QueryBuilder builder = sling.getService(QueryBuilder.class);
        String[] tags = (String[])properties.get("tag",String[].class);
        List<String> tagIds = new ArrayList<String>();
		for(String tag : tags){
			tagIds.add(tag);
		}

        List<Hit> hits = getTaggedArticles(tagIds, feedLimit, resourceResolver, builder, sortByPriority);

        if(hits.size() > 0){
			article1 = hits.get(0).getPath();
        }

        if(hits.size() > 1){
			article2 = hits.get(1).getPath();
        }

        if(hits.size() > 2){
			article3 = hits.get(2).getPath();
        }


    } else{
		article1 = properties.get("article1", "");
		article2 = properties.get("article2", "");
		article3 = properties.get("article3", "");
    }

	if ("on".equals(hasBorderLine)) {
		%> <hr style="border-top: solid 1px #dddddd"><%
	}


	if(article1.isEmpty() && article2.isEmpty() && article3.isEmpty()){
		if(WCMMode.fromRequest(request) == WCMMode.EDIT){

		%>
			<h4>##Configure Related Articles Component##</h4>
		<%
        }

    }else{%>
		<h4><%=title%></h4>


	<div class="block-grid">
    	<ul>
    		<li>
                <%
        		if(!article1.isEmpty()) {
        			request.setAttribute("articlePath", article1);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 1</p>
                <% } else { %>
					<p></p>
                <%}%>
            </li>
            <li>
                <%
        		if(!article2.isEmpty()) {
        			request.setAttribute("articlePath", article2);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 2</p>
                <% } else { %>
					<p></p>
                <%}%>
            </li>
            <li>
                <%
        		if(!article3.isEmpty()) {
        			request.setAttribute("articlePath", article3);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 3</p>
                <% } else { %>
                	<p></p>
                <%}%>
            </li>
        </ul>
	</div>
	<% } %>
