<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, org.apache.sling.api.resource.Resource, java.util.*" %>
<%!
    int MAX_TITLE = 58;

    public String trimTitle(String str) {
        String ret = str;
        if (ret.length() > MAX_TITLE) {
            ret = ret.substring(0, MAX_TITLE) + "...";
        }
        return ret;
    }
%>
<%
    String breadcrumbRoot = properties.get("breadcrumbRoot", "");
    Boolean isHidden = properties.get("isHidden", false);
    String delimStr = currentStyle.get("delim", "");

    if (!isHidden) { %>
        <ul class="breadcrumb inline-list"><%
            long level; //topic
            int currentLevel = currentPage.getDepth();
            if ("root".equals(breadcrumbRoot)) {
                level = 2;
            } else if ("subTopic".equals(breadcrumbRoot)) {
                level = 4;
            } else {  //default to topic;
                level = 3;
            }
            String prevTitle = "";
            String prevPath = "";
            if(level < currentLevel){
                while (level < currentLevel) {
                    String title = "";
                    Page crumb = currentPage.getAbsoluteParent((int) level);
                    if (crumb == null) {
                        break;
                    }
                    title = crumb.getNavigationTitle();
                    if (title == null || title.equals("")) {
                        title = crumb.getNavigationTitle();
                    }
                    if (title == null || title.equals("")) {
                        title = crumb.getTitle();
                    }
                    if (title == null || title.equals("")) {
                        title = crumb.getName();
                    }
                    if(title != null) {
                        if(prevTitle != null && prevTitle.length()>0 && !prevTitle.equals(title)){ %>
                            <li>
                                <%= xssAPI.filterHTML(delimStr) %>
                                <a href="<%= xssAPI.getValidHref(prevPath) %>"><%= xssAPI.encodeForHTML(trimTitle(prevTitle)) %></a>
                            </li><%
                        }
                        prevTitle = title;
                        prevPath = crumb.getPath()+".html";
                    }
                    level++;
                }
                %>
                <li><span><%= xssAPI.filterHTML(delimStr) %><%= xssAPI.encodeForHTML(trimTitle(prevTitle)) %></span></li>
                <%
            } else { //breadcrumb would be null if the root of the breadcrumb is set to a subtopic, but the page is actually a topic page
                //user mistakes
                //The user is not allowed to set the breadcrumb to start with subtopic when he/she is in the topic page
                %>Breadcrumb root is not set up properly. Please set the "breadcrumb root" one to two layers up to allow the current page to find its parent.<%
            }%>
        </ul>
    <%
    } else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        %>Breadcrumb is currently hidden for this page. Please click here to edit. <%
    }
%>

