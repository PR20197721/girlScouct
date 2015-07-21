<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/text.jsp -->
<%
	String shortDesc = properties.get("shortdesc","");
	String linkTitle = properties.get("pathfield","");
  String title = properties.get("title","");
  if (!linkTitle.isEmpty()){
      //fix: add ".html"suffix to cq:page
      Resource res = resourceResolver.getResource(linkTitle);
      if(res!=null && res.adaptTo(Page.class)!=null){
    	   linkTitle += ".html";
      }
	}
%>
  <%= shortDesc %>
  <a href="<%= linkTitle %>">Continue &gt;</a>