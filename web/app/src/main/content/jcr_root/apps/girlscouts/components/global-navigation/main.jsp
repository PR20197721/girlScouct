<%@ page
    import="java.util.Arrays,
    java.util.List,java.util.Iterator"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<% 
String[] links = (String[])(request.getAttribute("globalNavigation"));
String currPath = currentPage.getPath(); 
String rootPath = currentPage.getAbsoluteParent(2).getPath();
String eventLeftNavRoot = currentSite.get("leftNavRoot", String.class);
String eventPath = currentSite.get("eventPath", String.class);
String contentResourceType="";
Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
String designPath = currentDesign == null ? "/" : currentDesign.getPath(); 
for (int i = 0; i < links.length; i++) {
	String[] values = links[i].split("\\|\\|\\|");
	String label = values[0];
	String path = values.length >= 2 ? values[1] : "";
	String menuPath = values.length >= 2 ? values[1] : "";
	path = genLink(resourceResolver, path);
	String clazz = values.length >= 3 ? " "+ values[2] : "";
	String mLabel = values.length >=4 ? " "+values[3] : "";
	String sLabel = values.length >=5 ? " "+values[4] : "";
	Iterator <Page> slingResourceIter;
    String slingResourceType = "girlscouts/components/placeholder-page";
    contentResourceType="";
    
    try
    {
      contentResourceType = resource.getResourceResolver().getResource(menuPath+"/jcr:content").getResourceType();
	  if(contentResourceType.equals(slingResourceType)){
         slingResourceIter = resource.getResourceResolver().getResource(menuPath).adaptTo(Page.class).listChildren();
         if(slingResourceIter.hasNext()){
             Page firstChild =  slingResourceIter.next();
             path = genLink(resourceResolver, firstChild.getPath());
             
         }
         
        }
	 }catch(Exception e){}
	
	if(!currPath.equals(rootPath)) {
		if(currPath.startsWith(eventPath) && eventLeftNavRoot.startsWith(menuPath)) {
%>
    	   <li class="active">
             <a class="hide-for-medium hide-for-small menu <%= clazz %>" href="<%= path %>"><%= label %></a>
             <a class="show-for-medium menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
          </li>
<%
		} else if((menuPath.indexOf(currPath) == 0) || (currPath.startsWith(menuPath))) {
%>
    	    <li class="active">
             <a class="hide-for-medium hide-for-small menu <%= clazz %>" href="<%= path %>"><%= label %></a>
             <a class="show-for-medium menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
          </li>
<%
		} else {
%>
           <li>
            <a class="hide-for-medium hide-for-small menu <%= clazz %>" href="<%= path %>"><%= label %></a>
            <a class="show-for-medium menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
          </li>
<%
		}
	} else {
%>
         <li>
            <a class="hide-for-medium hide-for-small menu <%= clazz %>" href="<%= path %>"><%= label %></a>
            <a class="show-for-medium menu <%= clazz %>" href="<%= path %>"><%= mLabel %></a>
         </li>
<%
	}
}
%>
<li>
<a class="show-for-medium right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="19" height="28"></a>
</li>