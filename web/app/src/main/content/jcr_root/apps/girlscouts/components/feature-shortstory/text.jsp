<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/text.jsp -->
<%
	String shortDesc = properties.get("shortdesc","");
	String linkTitle = properties.get("pathfield","");
  String title = properties.get("title","");
%>
  <%= shortDesc %>
  <a href="<%= linkTitle %>">Continue &gt;</a>