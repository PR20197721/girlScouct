<%@ page import="com.day.cq.wcm.api.WCMMode"%>
<%@page import="java.util.Iterator,
                java.util.HashSet,java.util.Set,
                java.util.Arrays,org.apache.sling.api.resource.ResourceResolver,
                 org.apache.sling.api.resource.Resource,
                org.slf4j.Logger,org.slf4j.LoggerFactory"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->

<%

   String currPath = currentPage.getPath();  
   String[] links = properties.get("links", String[].class);
   request.setAttribute("globalNavigation", links);
   String eventGrandParent = currentPage.getParent().getParent().getPath();
   String leftNavRoot = currentSite.get("leftNavRoot", String.class); 
   String eventLeftNavRoot = currentSite.get("eventPath", String.class);
   String insertAfter="";
   String currTitle = currentPage.getTitle();
   String eventPath="";
   String eventGrdParent = "";
   String etPath="";
%>

<% 


for (int i = 0; i < links.length; i++) 
{
        String[] values = links[i].split("\\|\\|\\|");
        String label = values[0];
        String path = values.length >= 2 ? values[1] : "";
        path = genLink(resourceResolver, path);
        String clazz = values.length >= 3 ? " "+ values[2] : "";
        String mLabel = values.length >=4 ? " "+values[3] : "";
        String sLabel = values.length >=5 ? " "+values[4] : "";
        Set<String> navigationPath;
        Iterator<Page> menuLevel1;
        String navigation="";
        %>
       
          <li><a href="<%= path %>"><%= mLabel %></a></li>
      <% 
        if(!path.isEmpty() && !path.equalsIgnoreCase("#"))
        {
        	String rootPath = currentPage.getAbsoluteParent(2).getPath();
        	
        	navigation = currentPage.getPath().substring(currentPage.getAbsoluteParent(2).getPath().length()+1,currentPage.getPath().length());
            navigationPath = new HashSet<String>(Arrays.asList(navigation.split("/")));
            
            if(eventGrandParent.equalsIgnoreCase(currentSite.get("eventPath", String.class)))
            {
                eventGrdParent = currentPage.getParent().getParent().getPath().substring(rootPath.length()+1, currentPage.getParent().getParent().getPath().length());
                etPath =  eventLeftNavRoot.substring(rootPath.length()+1,eventLeftNavRoot.length());
                navigation =currentSite.get("leftNavRoot", String.class).substring(rootPath.length()+1,currentSite.get("leftNavRoot", String.class).length());
                navigationPath = new HashSet<String>(Arrays.asList(navigation.split("/"))); 
            }
            String startingPoint = path.substring(currentPage.getAbsoluteParent(2).getPath().length()+1,path.length());
            if(startingPoint.indexOf("/")>0){
            	startingPoint = startingPoint.substring(0, startingPoint.indexOf("/"));
            }
            
            //menuLevel1 = resourceResolver.getResource(rootPath+"/"+startingPoint).adaptTo(Page.class).listChildren();
            Resource res = resourceResolver.getResource(rootPath+"/"+startingPoint);
            menuLevel1 = res.adaptTo(Page.class).listChildren();
            StringBuilder menuBuilder = new StringBuilder();
            if(navigationPath.contains(startingPoint)){
            buildMenu(menuLevel1, navigationPath, menuBuilder, 0,currTitle,currPath,eventGrdParent,etPath);
            }
        %>    
        <%=menuBuilder%>
        <%} %>
       
        
<% }%>

<%!
  public StringBuilder buildMenu(Iterator<Page>menuLevel1, Set<String> navigationPath, StringBuilder menuBuilder, int levelDepth,String currTitle, String currPath,String eventGrandParent, String eventPath ){
    levelDepth++;
    menuBuilder.append("<ul>");
    if(menuLevel1.hasNext())
    {
      
     while(menuLevel1.hasNext())
     {
    	 
          Page level1 = menuLevel1.next();
          if(!level1.isHideInNav())
          { 
        	  if(navigationPath.contains(level1.getName()) )
              {
                
                  if(!eventGrandParent.isEmpty() && !eventPath.isEmpty() && eventGrandParent.equalsIgnoreCase(eventPath)){
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
          if(navigationPath.contains(level1.getName()) && !level1.isHideInNav() && level1.listChildren().hasNext()){
              
        	  buildMenu(level1.listChildren(),navigationPath,menuBuilder,levelDepth,currTitle,currPath,eventGrandParent,eventPath);
          }
          menuBuilder.append("</li>");
         
        }  
      }
    }  
      menuBuilder.append("</ul>"); 
      
    return menuBuilder;
 }

%>

