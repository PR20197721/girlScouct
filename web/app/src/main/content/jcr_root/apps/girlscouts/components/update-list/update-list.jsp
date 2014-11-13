<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%@ page import="com.day.cq.wcm.api.Page,
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


if(rPath != null ){
	if(rPath.isEmpty()){
    rPath=currentPage.getPath();
	}
	Resource res = slingRequest.getResourceResolver().getResource(rPath);
    Page udPage = res.adaptTo(Page.class);
    if(udPage != null){
        Iterator<Page> iter = udPage.listChildren(new PageFilter(request));
        while(iter.hasNext()){

            Page itemPage = iter.next();
            ValueMap props = itemPage.getProperties();
            updateList.add(props);
        }
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
}



%>
<div id=listhead>
   <h3><%= properties.get("jcr:title","New Section") %>  </h3>

<% if(seeAll.equals("true")) { 

%>
    <span> 
        <a href="<%=link %>.html" >See All</a> 
    </span>  
<%
		} 
%>

</div>

<div id=updateItems>

<%
int nItems = properties.get("nItems",updateList.size());

for(int i=0; i<nItems&&i<updateList.size(); i++){
    ValueMap updateItem = updateList.get(i);
%>
    <div class="updateItem">

        <div class="date"><%= updateItem.get("date", "00/00/00") %></div>
        <div class="title"><%= updateItem.get("jcr:title", "no title") %></div>
        <div class="text"><%= updateItem.get("text", "<p>no description</p>") %></div>
		<br>
    </div>
<%
}
%>
</div>

