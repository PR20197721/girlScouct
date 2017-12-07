<%@page import="java.util.Iterator, java.util.HashSet,java.util.Set, java.util.Arrays, org.slf4j.Logger,org.slf4j.LoggerFactory, javax.jcr.Node"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%> 
<cq:includeClientLib categories="apps.girlscouts"/>
<cq:defineObjects/>

<%!
public void buildMenu(Iterator<Page> iterPage, String rootPath, int rootDepth,
        String gs_us_path, StringBuilder menuBuilder, int levelDepth,
        String ndePath, boolean levelFlag, String eventLeftNavRoot,
        String currPath, String currTitle, String eventDispUnder,
        String showCurrent) throws RepositoryException {
    levelDepth++;
    System.out.println("Root Path is " + rootPath);
    System.out.println("Level Depth is" + rootDepth);

    if (iterPage.hasNext()) {
        if (levelDepth == 1) {
            menuBuilder.append("<ul class=\"side-nav\" style=\"padding:0px\">");
        } else {
            menuBuilder.append("<ul>");
        }
        while (iterPage.hasNext()) {
            Page page = iterPage.next();
            int dept = page.getDepth();
            String nodePath = page.getPath().substring(gs_us_path.length() + 1, page.getPath().length());
            showCurrent = page.getParent().getProperties().get("showCurrent", "false");

            // Check to see if the current path startsWith the node which we are traversing
            if (rootPath.startsWith(nodePath) && (rootDepth != page.getDepth() || rootPath.equals(nodePath))) {
                /**** Check to see if the current folder hideInNav is not set to true, if it's set ********** 
                ***** set to true, we don't display is but look to the next node, this is necessary to highlighting the**** 
                ***** special form condition.******/

                // This string buffer properly closes dangling li elements
                StringBuffer remainderStrings = new StringBuffer();
                if (!page.isHideInNav()) {
                    System.out.println("Cascading Pace is: " + createHref(page));
                    System.out.println("Level Depth is" + rootDepth + " Node depth is " + page.getDepth());
                    if (rootPath.equalsIgnoreCase(nodePath) && showCurrent.equals("false")) {
                        menuBuilder.append("<li class=\"active current\">");
                        menuBuilder.append("<div>");
                        menuBuilder.append(createHref(page));
                        menuBuilder.append("</div>");
                        remainderStrings.append("</li>");
                    } else {
                        if (levelFlag && page.listChildren().hasNext()) {
                            menuBuilder.append("<li class=\"active\">");
                            menuBuilder.append("<div>");
                            menuBuilder.append(createHref(page));
                            menuBuilder.append("</div>");
                            remainderStrings.append("</li>");
                            levelFlag = false;
                        } else {
                            if (rootPath.equals(nodePath)) {
                                menuBuilder.append("<li class=\"active current\">");
                            } else {
                                menuBuilder.append("<li>");
                            }
                            menuBuilder.append("<div>");
                            menuBuilder.append(createHref(page));
                            menuBuilder.append("</div>");
                            remainderStrings.append("</li>");
                        }
                    }
                }
                Iterator<Page> p = page.listChildren();
                if (p.hasNext()) {
                    buildMenu(p, rootPath, rootDepth, gs_us_path, menuBuilder,
                            levelDepth, nodePath, levelFlag,
                            eventLeftNavRoot, currPath, currTitle,
                            eventDispUnder, showCurrent);
                }
                menuBuilder.append(remainderStrings.toString());
            } else {
                /*** Below if eventLeftNavRoot is to handle a special case for events. Events is create at separate location
                 ***  and when event is click event name need to be displayed in the left navigation
                */
                if (page.getPath().indexOf(eventLeftNavRoot) == 0 && currPath.indexOf(eventDispUnder) == 0) {
                    menuBuilder.append("<li class=\"active\">");
                    menuBuilder.append("<div>");
                    menuBuilder.append(createHref(page));
                    menuBuilder.append("</div>");

                    menuBuilder.append("<ul><li class=\"active current\">");
                    menuBuilder.append("<div><a href=")
                            .append(currPath + ".html").append(">")
                            .append(currTitle).append("</a></div>");
                    menuBuilder.append("</li></ul>");
                                            menuBuilder.append("</li>");

                } else {
                    /*****This showCurrent is for the highligting form *******
                     **** Top folder under which forms resides has a property "showCurrent = true" *****
                     ***** which we are using to display the form it form is in the URL path and parent of it is set ****
                     ***** to "true" else we are not displaying that content *******/
                    if (showCurrent.equals("false") && !page.isHideInNav()) {
                        menuBuilder.append("<li>");
                        menuBuilder.append("<div>");
                        menuBuilder.append(createHref(page));
                        menuBuilder.append("</div>");
                        menuBuilder.append("</li>");
                    }
                }
            }
        }
    }
    menuBuilder.append("</ul>");
}
%>

<%
// GET THE STRUCTURE FROM THE CURRENTPATH

String curPath = currentPage.getPath();
String curTitle = currentPage.getTitle();
int levelDepth = 0;
StringBuilder menuBuilder = new StringBuilder();
// from this path get to the parent
String gs_us_path = currentPage.getAbsoluteParent(2).getPath();
String rootPath = currentPage.getPath().substring(gs_us_path.length()+1, curPath.length());
int rootDepth = currentPage.getDepth();
String navigationRoot = currentPage.getAbsoluteParent(3).getPath();
String showCurrent = "false";

// What is the navigationRoot
boolean levelFlag = true;
Iterator<Page> iterPage = resourceResolver.getResource(navigationRoot).adaptTo(Page.class).listChildren();

// Handling events
String eventGrandParent = currentPage.getParent().getParent().getPath();
String eventLeftNavRoot = currentSite.get("leftNavRoot", String.class);
String eventDisplUnder = currentSite.get("eventPath", String.class);
boolean includeUL=false;
String insertAfter="";
    
if(eventGrandParent.equalsIgnoreCase(currentSite.get("eventPath", String.class))){
    String eventPath = eventLeftNavRoot.substring(0,eventLeftNavRoot.lastIndexOf("/"));
    iterPage = resourceResolver.getResource(eventPath).adaptTo(Page.class).listChildren();
}
    
buildMenu(iterPage, rootPath, rootDepth, gs_us_path, menuBuilder, levelDepth, "", levelFlag, eventLeftNavRoot, curPath, curTitle, eventDisplUnder, showCurrent);

%>
<%=menuBuilder %> 
<script type="text/javascript">
    $(document).ready(function () {
        $('#main .side-nav li.active.current').parent().parent().find(">div>a").css({
            "font-weight": "bold",
            "color": "#414141"
        });
    }); 
</script>