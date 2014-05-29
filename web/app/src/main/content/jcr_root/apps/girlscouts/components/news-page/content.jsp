<%@page import="java.text.Format,com.day.cq.dam.commons.util.DateParser,java.text.SimpleDateFormat"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/news/content.jsp -->
<div id="main">
	<div class="row">
		<div class="large-24 medium-24 hide-for-small columns rightBodyTop">
			<nav class="breadcrumbs">
				<a href="#">/path/to/event</a>
			</nav>
		</div>
		<div class="large-24 medium-24 small-24 columns">
			<div id="mainContent">
<cq:include script="/libs/foundation/components/title/title.jsp"/>
<%
	String date = properties.get("date","");
	Format formatter = new SimpleDateFormat("dd MMM yyyy");
	if(date!=null && !date.isEmpty()){
%>
				<p><%=formatter.format(DateParser.parseDate(date)) %></p>
<%
	}
%>
				<br/>
<cq:include path="image" resourceType="foundation/components/image" />
<cq:include path="text" resourceType="foundation/components/text"/>
			</div>
		</div>
	</div>
</div>
