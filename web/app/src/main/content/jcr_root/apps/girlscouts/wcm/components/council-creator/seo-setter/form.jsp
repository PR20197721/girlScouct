<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.Iterator" %>


<form action="#" method="post">
<input type="hidden" name="action" value="set"/>
Welcome to SEO Setter! This tool will create SEO titles for any pages that don't already have that property set. Please use this only for council rollout puposes!
<br><br>
Please select a council:
<select name="councilPath">
<%
PageManager pm = resourceResolver.adaptTo(PageManager.class);
Resource content = resourceResolver.resolve("/content");
Iterator<Resource> contentChildren = content.listChildren();
while (contentChildren.hasNext()){
	Resource thisRes = contentChildren.next();
	Page thisPage = thisRes.adaptTo(Page.class);
	if(null != thisPage){
		if(thisPage.hasChild("en")){
			Page enPage = pm.getPage(thisPage.getPath() + "/en");
			if(enPage.getTitle() != null){
				%> 
					<option value="<%= enPage.getPath() %>"><%= enPage.getTitle() %></option>
				<%
			}
		}
	}
}
%>
</select>
Make sure you chose the right council. This action can not easily be undone.
<br><br>
<input type="submit" value="Submit">
</form>
