<%@include file="/libs/foundation/global.jsp" %> 
<%@include file="/apps/gsusa/components/global.jsp" %> 
<%@page import="com.day.cq.wcm.api.WCMMode" %> 
<div class="search search-box">
	<%
	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header"; 
	Resource search = resourceResolver.resolve(headerPath+"/search"); 
	if(search != null){ 
	  	ValueMap searchProps = search.getValueMap(); 
		String placeholderText = searchProps.get("placeholder-text","");
		String searchAction = searchProps.get("searchAction", null);
		String action="";
		if ((null==searchAction) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>
			Please edit Search Box Component
		<%
		} else if(null != searchAction) {
			action = currentSite.get(searchAction,String.class);
		%>
			<form action="<%=action%>.html" method="get" class="search-form">
				<input type="text" name="q" placeholder="<%=placeholderText %>" tabindex="35" pattern=".{3,}" required title="3 characters minimum" /> 
				<span class="icon-search-magnifying-glass"></span>
			</form>
		<%
		}
	}%>
</div>