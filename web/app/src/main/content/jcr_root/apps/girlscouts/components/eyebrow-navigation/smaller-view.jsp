<%@ page
    import="java.util.Arrays,java.util.Iterator,
    java.util.List"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
String[] link = properties.get("links", String[].class);
String gs_us_path = currentPage.getAbsoluteParent(2).getPath();
String currtPath = currentPage.getPath(); 
String currTitle = currentPage.getTitle();
String rootPath = currentPage.getPath().substring(gs_us_path.length()+1, currtPath.length()); 
int levelDepth = 0;
boolean levelFlag = true;
request.setAttribute("links", link);

%>
<%!
 
public void buildMenu(Iterator<Page> iterPage, String rootPath, String gs_us_path,StringBuilder menuBuilder,int levelDepth,String ndePath, boolean levelFlag,String currPath, String currTitle) throws RepositoryException{
	levelDepth++;
	menuBuilder.append("<ul>");
	if(iterPage.hasNext()){
		while(iterPage.hasNext()){
			Page page = iterPage.next();
			// CHECK FOR THE HIDEINNAV
			if(!page.isHideInNav()){ 
				int dept = page.getDepth();
				String nodePath = page.getPath().substring(gs_us_path.length()+1, page.getPath().length());
				if(rootPath.indexOf(nodePath) == 0){
					// This string buffer properly closes dangling li elements
					StringBuffer remainderStrings = new StringBuffer();

					// CHECK IF CURRENTLY WE ARE ON THE PAGE ELSE DONT HIGHLIGHT
					if(rootPath.equalsIgnoreCase(nodePath) ){
						menuBuilder.append("<li class=\"active\">");
						menuBuilder.append("<div>");
						menuBuilder.append(createHref(page));
						menuBuilder.append("</div>");
						remainderStrings.append("</li>");
					}else{
						menuBuilder.append("<li>");
						menuBuilder.append("<div>");
						menuBuilder.append(createHref(page));
						menuBuilder.append("</div>");
                        remainderStrings.append("</li>");
					}
					Iterator<Page> p = page.listChildren(); 
					if(p.hasNext()){
						buildMenu(p, rootPath,gs_us_path, menuBuilder, levelDepth,nodePath, levelFlag,currPath, currTitle);           
					}
					//menuBuilder.append(remainderStrings.toString());

				} else{
					
						menuBuilder.append("<li>");
						menuBuilder.append("<div style=\"background-color:red\">");
						menuBuilder.append("<a href=").append(createHref(page));
						menuBuilder.append("</div>");
						menuBuilder.append("</li>");
					}
			}// end of if
		}//while
		menuBuilder.append("");
	}
	menuBuilder.append("</ul>"); 
	// return menuBuilder;
}
%>

<div id="right-canvas-menu-bottom">
  <ul>
 <% 
	
	String[] links = (String[])(request.getAttribute("links"));
	
 	for (int i = 0; i < links.length; i++) {
		String[] values = links[i].split("\\|\\|\\|");
		String label = values[0];
		String menuPath = values.length >= 2 ? values[1] : "";
		String path = values.length >= 2 ? values[1] : "";
		Iterator<Page> iterPage=null;
		StringBuilder menuBuilder = new StringBuilder();
		path = genLink(resourceResolver, path);
		String startingPoint="";
		String clazz = values.length >= 3 ? "class=\""+ values[2] + "\"": "";
		String newWindow = values.length >= 4 && values[3].equalsIgnoreCase("true") ?" target=\"_blank\"" : "";
		if(!path.isEmpty() && !path.equalsIgnoreCase("#") && path.indexOf(currentPage.getAbsoluteParent(2).getPath()) == 0) {
			startingPoint = menuPath.substring(currentPage.getAbsoluteParent(2).getPath().length()+1,menuPath.length());
			if(startingPoint.indexOf("/")>0) {
				startingPoint = startingPoint.substring(0, startingPoint.indexOf("/"));
			}
			System.out.println("What is the startingPoint [" +startingPoint +"]");
			iterPage = resourceResolver.getResource(gs_us_path+"/"+startingPoint).adaptTo(Page.class).listChildren();
		
		}
		if((currtPath.indexOf(menuPath)==0) || (currtPath.startsWith(menuPath))){
			//Check if the current Path has childrens'
			if(currtPath.equals(menuPath)){
				System.out.println("I am if statement");
			%>
			   
				<div><li class="active"><a href="<%= path %>"<%= newWindow %>><%= label %></a>
			<%}else{%>
				<li><a href="<%= path %>"<%= newWindow %>><%= label %></a>
			<%}%>
			<% if(currtPath.indexOf(menuPath)==0){
				System.out.println("what is the currPath" +currtPath + "menuPath" +menuPath);
					buildMenu(iterPage, rootPath, gs_us_path, menuBuilder, levelDepth,"",levelFlag, currtPath, currTitle);
			} %>
			<%=menuBuilder%>
			</li></div>
		<%}else{
			System.out.println("Path" +path);
		%>
		
		<li><a href="<%= path %>"<%= newWindow %>><%= label %></a></li>
	<%}%>
	<%} %>

       
 </ul>
</div> 
   




