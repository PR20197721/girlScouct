<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<!-- apps/girlscouts/components/feature-shortstory/text.jsp -->
<%
	String shortDesc = properties.get("shortdesc","");
  //use genlink to get href of cq:page
  String linkTitle = genLink(resourceResolver, properties.get("pathfield",""));
  String title = properties.get("title","");

%>
  <%= shortDesc %>
  <a href="<%= linkTitle %>">Continue &gt;</a>