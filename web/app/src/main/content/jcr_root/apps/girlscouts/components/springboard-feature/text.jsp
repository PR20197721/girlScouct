<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/text.jsp -->
<%
	String shortDesc = properties.get("shortdesc","");
	String linkTitle = properties.get("pathfield","");
%>
<div class="small-24 medium-12 large-12 columns">
	<%= shortDesc %>
	<a href="<%= linkTitle %>">Continue &gt;</a> <br /> <br />
</div>
