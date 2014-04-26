<%@include file="/libs/foundation/global.jsp"%>

<%
	String shortDesc = properties.get("shortdesc","");
	String linkTitle = properties.get("pathfield","");
%>

<div class="small-24 medium-12 large-12 columns">
	<p><%= shortDesc %></p>
	<a href="<%= linkTitle %>">Continue &gt;</a> <br /> <br />
</div>