<%@page import="java.util.Iterator,
                java.util.HashSet,java.util.Set,
                java.util.Arrays,
                org.slf4j.Logger,org.slf4j.LoggerFactory" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<cq:includeClientLib categories="apps.girlscouts" />





<cq:defineObjects />

<%
  Iterator<Page> menuLevel1;
  String navigation="";
  Set<String> navigationPath;
  String currPath = currentPage.getPath();
  String currTitle = currentPage.getTitle();
  boolean flag=false;
  String dispEvtBelow = "";
  String eventGrandParent = currentPage.getParent().getParent().getPath();
  String eventLeftNavRoot = currentSite.get("leftNavRoot", String.class);  
  String insertAfter="";
  if(eventGrandParent.equalsIgnoreCase(currentSite.get("eventPath", String.class))){
	  
	  String eventPath = eventLeftNavRoot.substring(0,eventLeftNavRoot.lastIndexOf("/"));
	  insertAfter = eventLeftNavRoot.substring(eventLeftNavRoot.lastIndexOf("/")+1, eventLeftNavRoot.length());
	  menuLevel1 = resourceResolver.getResource(eventPath).adaptTo(Page.class).listChildren();
	  navigationPath = new HashSet<String>(Arrays.asList(eventLeftNavRoot.split("/")));
      flag = true;
      
  
  }
  
  else if(currPath.equalsIgnoreCase(currentPage.getAbsoluteParent(2).getPath()))
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
  buildMenu(menuLevel1,navigationPath,menuBuilder,levelDepth,flag, insertAfter, currPath, currTitle);

  
 %>

<%=menuBuilder %>
<%!
  public StringBuilder buildMenu(Iterator<Page>menuLevel1, Set<String> navigationPath, StringBuilder menuBuilder, int levelDepth, boolean flag, String dispEvtBelow, String currPath, String currTitle){
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
          
          if(!level1.isHideInNav())
          { 
        	 
        	 if(navigationPath.contains(level1.getName()) )
           	   {
        		 
        		   if(level1.getName().equalsIgnoreCase(dispEvtBelow)){
        			   menuBuilder.append("<li class=\"active\" id=").append("\"").append(level1.getName()).append("\">").append("<a href=").append(level1.getPath()+".html").append(">").append(level1.getTitle()).append("</a>").append("</li>");
        			   menuBuilder.append("<ul> <li class=\"active\" id=").append("\"").append(currTitle).append("\">").append("<a href=").append(currPath+".html").append(">").append(currTitle).append("</a>").append("</li></ul>");
        		   }else{
        			   menuBuilder.append("<li class=\"active\">");
        			   menuBuilder.append("<a href=")
                       .append(level1.getPath()+".html")
                       .append(">")
                       .append(level1.getTitle())
                       .append("</a>");
                       menuBuilder.append("</li>");
        			   
        		   }
        		    
                } 
        	 else
        	 {
        		 menuBuilder.append("<li id=");
                 menuBuilder.append("\"").append(level1.getName()).append("\">");
                 menuBuilder.append("<a href=")
                 .append(level1.getPath()+".html")
                 .append(">")
                 .append(level1.getTitle())
                 .append("</a>");
                 menuBuilder.append("</li>");
        	 }
              
         
          
          }
          if(navigationPath.contains(level1.getName()) && !level1.isHideInNav() && level1.listChildren().hasNext() && levelDepth < 3){
              buildMenu(level1.listChildren(),navigationPath,menuBuilder,levelDepth,flag,dispEvtBelow,currPath,currTitle);
          }
          menuBuilder.append("</li>");
          if (levelDepth == 1) {
        	menuBuilder.append("<li class=\"divider\"></li>");
          }
        }  
      }
      
      menuBuilder.append("</ul>"); 
      
    return menuBuilder;
 }

%>



