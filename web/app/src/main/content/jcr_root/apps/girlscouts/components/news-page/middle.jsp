<%@page import="java.util.Calendar,java.text.DateFormat,com.day.cq.dam.commons.util.DateParser,java.text.SimpleDateFormat,java.util.Date"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/news/content.jsp -->
<div id="main">
	<div class="row">
		<div class="large-24 medium-24 small-24 columns">
			<div id="mainContent">
		
<cq:include script="/libs/foundation/components/title/title.jsp"/>
<%
	

	DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    Date date = properties.get("date",Date.class);
	if(date!=null){
%>
				<p><%=dateFormat.format(date) %></p>
<%
	}
%>
				<br/>
<cq:include path="middle/par/text/image" resourceType="girlscouts/components/image" />
<cq:include path="middle/par/text" resourceType="girlscouts/components/text"/>
			</div>
		</div>
	</div>
</div>

