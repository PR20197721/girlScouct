<%@page import="java.util.Iterator,java.util.HashSet,java.util.Set, java.util.Arrays" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%

  String navigation = currentPage.getPath().substring(currentPage.getAbsoluteParent(2).getPath().length()+1,currentPage.getPath().length());
  Set<String> navigationPath = new HashSet<String>(Arrays.asList(navigation.split("/")));
  
  Iterator<Page> menuLevel1 = currentPage.getAbsoluteParent(3).listChildren();
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
	 menuBuilder.append("<ul>");
	 while(menuLevel1.hasNext())
	 {
		 
    	  Page level1 = menuLevel1.next();
    	  if(navigationPath.contains(level1.getName()) )
          {
    		  menuBuilder.append("<li>").append(level1.getTitle()).append("</li>");
    		  if(level1.listChildren().hasNext() && levelDepth < 3){
    			  buildMenu(level1.listChildren(),navigationPath,menuBuilder,levelDepth);
    			  
    		  }
    	  }else
    	  {
    		  
   		    menuBuilder.append("<li><a href=");
    	    menuBuilder.append(level1.getPath()+".html");
    	    menuBuilder.append(">"+level1.getTitle()+"</a></li>");
    		  
          }
    	  
      }
	  menuBuilder.append("</ul>"); 
	}
    return menuBuilder;
 }

%>  
  