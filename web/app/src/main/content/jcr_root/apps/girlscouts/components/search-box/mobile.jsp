<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
Resource search = resourceResolver.resolve(headerPath+"/search-box");
if(search != null){
	ValueMap searchProps = search.getValueMap();
	String placeholderText = searchProps.get("placeholder-text","");
	String searchAction = searchProps.get("searchAction", null);
	String action = (String)request.getAttribute("altSearchPath");
	if ((null==searchAction) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>
		Please edit Search Box Component
	<%
	} else if(searchAction==null){
		%> Search Not Configured <%
	} else {
	
		if( !(action!=null && !action.trim().equals("")) )
		    	action = currentSite.get(searchAction,String.class);	
	%>
	    <form action="<%=action%>.html" method="get"> 
			<input type="text" name="q" placeholder="<%=placeholderText %>" class="searchField"/>
		</form>
	<%}
}%>
	
