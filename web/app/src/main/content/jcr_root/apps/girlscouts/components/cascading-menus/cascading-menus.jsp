<%@page import="java.util.Iterator,
                java.util.HashSet,java.util.Set,
                java.util.Arrays,
                org.slf4j.Logger,org.slf4j.LoggerFactory,
                javax.jcr.Node" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />

<%
  // GET THE STRUCTURE FROM THE CURRENTPATH
  
  String curPath = currentPage.getPath();
  String curTitle = currentPage.getTitle();
  int levelDepth = 0;
  StringBuilder menuBuilder = new StringBuilder();
  // from this path get to the parent
  String gs_us_path = currentPage.getAbsoluteParent(2).getPath();
  String rootPath = currentPage.getPath().substring(gs_us_path.length()+1, curPath.length());  
  String navigationRoot = currentPage.getAbsoluteParent(3).getPath();
  
 // What is the navigationRoot
  boolean levelFlag = true;
  Iterator<Page> iterPage = resourceResolver.getResource(navigationRoot).adaptTo(Page.class).listChildren();
 
 // Handling events
  String eventGrandParent = currentPage.getParent().getParent().getPath();
  String eventLeftNavRoot = currentSite.get("leftNavRoot", String.class);  
  String insertAfter="";
 if(eventGrandParent.equalsIgnoreCase(currentSite.get("eventPath", String.class))){
     String eventPath = eventLeftNavRoot.substring(0,eventLeftNavRoot.lastIndexOf("/"));
     //System.out.println("What is the eventPath" +eventPath);
     iterPage = resourceResolver.getResource(eventPath).adaptTo(Page.class).listChildren();
  
 }
 buildMenu(iterPage, rootPath, gs_us_path, menuBuilder, levelDepth,"",levelFlag,eventLeftNavRoot, curPath, curTitle);
 
 
 %>
 <%=menuBuilder %>
 <%!
 
 public StringBuilder buildMenu(Iterator<Page> iterPage, String rootPath, String gs_us_path,StringBuilder menuBuilder,int levelDepth,String ndePath, boolean levelFlag,String eventLeftNavRoot,String currPath, String currTitle) throws RepositoryException{
     
	/* try{
		 if(null==topLevel)
		 {
	         //System.out.println("This is the " +topLevel.getName());
	         
	     }else
	     {
	         //System.out.println("This is the " +topLevel.getName());
	     }
	 }catch(Exception e){}*/
	 
	 
	 levelDepth++;
	 if(iterPage.hasNext())
	    {
		   if (levelDepth == 1) 
		   {
	         menuBuilder.append("<ul class=\"side-nav\" style=\"padding:0px\">");
	        } else{
	            menuBuilder.append("<ul>");
	       }
	 while(iterPage.hasNext())
	     {
    	 	    Page page = iterPage.next();
    	 	    
    	 	   if(!page.isHideInNav())
    	       { 
	    	    int dept = page.getDepth();
	    	    String nodePath = page.getPath().substring(gs_us_path.length()+1, page.getPath().length());

	    	    if(rootPath.indexOf(nodePath) == 0)
                {
                	if(rootPath.equalsIgnoreCase(nodePath) )
                        {
                            menuBuilder.append("<li class=\"active\">");
                            menuBuilder.append("<a href=").append(page.getPath()+".html").append(">").append(page.getTitle()).append("</a>");
                            menuBuilder.append("</li>");
                         }
                     else
                        {
                           if(levelFlag && page.listChildren().hasNext()){
                        	   menuBuilder.append("<li class=\"active\">");
                               menuBuilder.append("<a href=").append(page.getPath()+".html").append(">").append(page.getTitle()).append("</a>");
                               menuBuilder.append("</li>");
                               levelFlag=false;
                            }else{  	
                               menuBuilder.append("<li>");
                               menuBuilder.append("<a href=").append(page.getPath()+".html").append(">").append(page.getTitle()).append("</a>");
                               menuBuilder.append("</li>");
                               
                           }    
                               
                               
                               
                         }
                   if(page.listChildren().hasNext())
                     {
                	      buildMenu(page.listChildren(), rootPath,gs_us_path, menuBuilder, levelDepth,nodePath, levelFlag,eventLeftNavRoot,currPath, currTitle);           
	                 }
                }
                else
                   {
                	 if(page.getPath().indexOf(eventLeftNavRoot)==0)
                	 {
                		 menuBuilder.append("<li class=\"active\">");
                         menuBuilder.append("<a href=").append(page.getPath()+".html").append(">").append(page.getTitle()).append("</a>");
                         menuBuilder.append("</li>");
                         
                         menuBuilder.append("<ul><li class=\"active\">");
                         menuBuilder.append("<a href=").append(currPath+".html").append(">").append(currTitle).append("</a>");
                         menuBuilder.append("</li></ul>");
                     }else
                	 {
                    	menuBuilder.append("<li>");
            	        menuBuilder.append("<a href=").append(page.getPath()+".html").append(">").append(page.getTitle()).append("</a>");
            	        menuBuilder.append("</li>");
                	 }
               }
    
	       }
	  }
	}
     menuBuilder.append("</ul>"); 
     return menuBuilder;
     
 }
 
 
 //
 
  


%>

