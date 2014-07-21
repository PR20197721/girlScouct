<%@page import="com.day.cq.wcm.foundation.List,com.day.cq.wcm.api.Page, 
                java.util.Iterator,com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
    
List list = new List(slingRequest, new PageFilter());


request.setAttribute("elist", list);
if (!list.isEmpty()){
	Iterator<Page> items = list.getPages();
	
	String listItemClass = null;
	while (items.hasNext()){
		Page item = (Page)items.next();
		System.out.println("Items...." +item.getPath());
		// TODO: Manu please fix!
		//Node node = item.getContentResource().adaptTo(Node.class);
		//node.setProperty("isFeature", true);
	    //node.save();
	   }
	    
}

if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
}

%>