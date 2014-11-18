<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%@ page
	import="com.day.cq.wcm.api.Page,
com.day.cq.wcm.api.PageFilter,
com.day.cq.dam.api.Asset,
java.util.ArrayList,
java.util.Iterator,
java.util.Collections,
java.util.Comparator"%>
<%

  String rPath = properties.get("reference", null);
  String seeAll = properties.get("showSeeAll","false");


  String link = properties.get("link", rPath);//href of the "see all"button
  ArrayList<ValueMap> updateList = new ArrayList();


  if(rPath == null || rPath.isEmpty()){
    
    rPath=currentPage.getPath();
  }
    
  Resource res = slingRequest.getResourceResolver().getResource(rPath);
  if(res != null){
	  Page udPage = res.adaptTo(Page.class);

      Iterator<Page> iter = udPage.listChildren(new PageFilter(request));
      while(iter.hasNext()){

        Page itemPage = iter.next();
        ValueMap props = itemPage.getProperties();
        updateList.add(props);
      }
      Collections.reverse(updateList);
      Collections.sort(updateList, new Comparator() {
      public int compare(Object a, Object b) {
      ValueMap aProps = (ValueMap) a;
      ValueMap bProps = (ValueMap) b;

      String[] dateArr1 = aProps.get("date", "00/00/00").split("/");
      String d1 = dateArr1[2] + dateArr1[0] + dateArr1[1];

      String[] dateArr2 = bProps.get("date", "00/00/00").split("/");
      String d2 = dateArr2[2] + dateArr2[0] + dateArr2[1];

      return d2.compareTo(d1);
      }
    });
  }

%>
<div class="row">
	<div class="columns large-20 large-centered">
		<section class="clearfix header">
			<h3><%= properties.get("jcr:title","New Section") %></h3>

			<% if(seeAll.equals("true")) { %>
			<a href="<%=link %>.html" title="see all" class="more-link">See
				all</a>
			<% } %>
		</section>

		<section class="clearfix">
			<ul>
				<%
        int nItems = properties.get("nItems",updateList.size());
        for(int i=0; i<nItems&&i<updateList.size(); i++) {
        ValueMap updateItem = updateList.get(i);
        %>
				<li><span class="date"><%= updateItem.get("date", "00/00/00") %></span>
					<span class="title"><strong><%= updateItem.get("jcr:title", "no title") %></strong></span>
					<div class="text"><%= updateItem.get("text", "no description") %></div>
				</li>
				<% } %>
			</ul>
		</section>
	</div>
</div>

