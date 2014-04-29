<%@page import="java.util.Iterator,java.util.HashSet,java.util.Set, java.util.Arrays" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />

<%
  


  Iterator<Page> menuLevel1;
  String navigation="";
  Set<String> navigationPath;
  String currPath = currentPage.getPath();
  if(currPath.equalsIgnoreCase(currentPage.getAbsoluteParent(2).getPath()))
  {
	 // So I am the root, Check for the children
	 menuLevel1 = currentPage.listChildren();
	 navigationPath = new HashSet<String>(Arrays.asList(navigation.split("/")));
	 
  }else{
	  navigation = currentPage.getPath().substring(currentPage.getAbsoluteParent(2).getPath().length()+1,currentPage.getPath().length());
	  navigationPath = new HashSet<String>(Arrays.asList(navigation.split("/")));
	  menuLevel1 = currentPage.getAbsoluteParent(3).listChildren();
  }
  StringBuilder menuBuilder = new StringBuilder();
  int levelDepth = 0;
  
  buildMenu(menuLevel1,navigationPath,menuBuilder,levelDepth);
 
 %>
<%=menuBuilder %>
<%!
  public StringBuilder buildMenu(Iterator<Page>menuLevel1, Set<String> navigationPath, StringBuilder menuBuilder, int levelDepth){
    levelDepth++; 
    if(menuLevel1.hasNext())
    {
	 if (levelDepth == 1) {
      menuBuilder.append("<ul class=\"side-nav\" style=\"padding:0px\">");
	 } else {
      menuBuilder.append("<ul>");
	 }
     while(menuLevel1.hasNext())
     {
          
          Page level1 = menuLevel1.next();
          if(navigationPath.contains(level1.getName()))
          {
              menuBuilder.append("<li class=\"active\">");
          } else {
        	  
              menuBuilder.append("<li>");
          }
              
          menuBuilder.append("<a href=")
	          .append(level1.getPath()+".html")
	          .append(">")
	          .append(level1.getTitle())
	          .append("</a>");
          if(navigationPath.contains(level1.getName()) && level1.listChildren().hasNext() && levelDepth < 3){
              buildMenu(level1.listChildren(),navigationPath,menuBuilder,levelDepth);
          }
          menuBuilder.append("</li>");

          if (levelDepth == 1) {
        	menuBuilder.append("<li class=\"divider\"></li>");
          }
      }
      menuBuilder.append("</ul>"); 
    }
    return menuBuilder;
 }

%>
