<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.Page,
com.day.cq.wcm.api.PageFilter,
com.day.cq.dam.api.Asset,
java.util.ArrayList,
java.util.Iterator,
java.util.Collections,
java.util.Comparator,
java.text.SimpleDateFormat,
java.util.Date,
java.util.Calendar"
%>
<%
String rPath = properties.get("reference", null);
String seeAll = properties.get("showSeeAll","false");
String link = properties.get("link", rPath);//href of the "see all"button
ArrayList<ValueMap> updateList = new ArrayList<ValueMap>();
if(rPath == null || rPath.isEmpty()){
   	rPath = currentPage.getPath();
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
   	Collections.sort(updateList, new Comparator<ValueMap>() {
 	  	Calendar cal =  Calendar.getInstance();
 	  	DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
    	public int compare(ValueMap aProps, ValueMap bProps) {
	 		try{
	 			cal.setTime(format.parse(aProps.get("date", "1999-01-01T02:30:00.000-04:00")));
	 			Date aDate = cal.getTime();
	 			cal.setTime(format.parse(bProps.get("date", "1999-01-01T02:30:00.000-04:00")));
	   			Date bDate = cal.getTime();
	   			return bDate.compareTo(aDate);
			} catch(Exception e){
				e.printStackTrace();
			}
		return 0;
    	}
	});
}
%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<div class="row">
	<div class="columns large-20 large-centered">
		<section class="clearfix header">
			<h3><%= properties.get("jcr:title","New Section") %></h3>
			<% if(seeAll.equals("true")) { %>
				<a href="<%=link %>.html" title="see all" class="more-link">See all</a>
			<% } %>
		</section>
		<section class="clearfix">
			<ul>
				<%
		        int nItems = properties.get("nItems",updateList.size());
		        for(int i=0; i<nItems&&i<updateList.size(); i++) {
			        ValueMap updateItem = updateList.get(i);
			        String dateField = updateItem.get("date", "1999-01-01T02:30:00.000-04:00");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
					Date date = sdf.parse("1999-01-01T02:30:00.000-04:00");
					try{
						date = sdf.parse(dateField);
					} catch(Exception e){
						e.printStackTrace();
					}
			        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM.dd.yyyy h:mm a");
					String formattedDateStr = displayDateFormat.format(date);
	        		%>
					<li><span class="date"><%= formattedDateStr %></span>
						<span class="title"><strong><%= updateItem.get("jcr:title", "no title") %></strong></span>
						<div class="text"><%= updateItem.get("text", "no description") %></div>
					</li>
				<%} %>
			</ul>
		</section>
	</div>
</div>