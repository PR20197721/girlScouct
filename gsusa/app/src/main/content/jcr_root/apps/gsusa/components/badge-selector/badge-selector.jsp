<%@ page import="java.lang.Exception,
	java.util.Iterator,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<cq:defineObjects />

<%
/************************** Badge Selector Component ************************
** This component creates a tag dropdown for badges in order to filter through collection
**
*************************************************************************/
String[] tagPaths = properties.get("badgeTagPaths", String[].class);
if (tagPaths != null && tagPaths.length > 0) { 
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"tags\">");
    %><ul class="dropdown"><%
        int dropDwnCnt = 0;
        for (String tagPath : tagPaths) {
            Tag dropdownTag = tagManager.resolve(tagPath);
            if (dropdownTag != null) {
                dropDwnCnt++;
                if (dropDwnCnt > 1) {
                    %><li class="divider"></li><%
                }
                %><li>
                    <input id="dropdown-<%=dropDwnCnt%>" type="checkbox" name="menu" autocomplete="off" /> 
                    <label for="dropdown-<%=dropDwnCnt%>"><%=dropdownTag.getTitle()%></label><%
                    int subTagGroupsCnt = 0;
                    int subTagCnt = 0;
                    Iterator<Tag> subTagGroups = dropdownTag.listChildren();
                    if (subTagGroups.hasNext()) {
                    	%><div class="submenu">
	                        <ul><%
	                        while (subTagGroups.hasNext()){
		                    	if(subTagGroupsCnt > 0){%><li><div class="divider"></div></li><%}
		                    	subTagGroupsCnt++;
		                    	Iterator<Tag> subTags = subTagGroups.next().listChildren();
			                    if (subTags.hasNext()) {                       
			                        while (subTags.hasNext()) {
			                            Tag subTag = subTags.next();
			                            subTagCnt++;
			                            %><li>
		                                   <input id="select-<%=dropDwnCnt%>-<%=subTagCnt%>-<%=subTag.getName()%>" type="checkbox" name="menu" autocomplete="off" /> 
		                                   <label for="select-<%=dropDwnCnt%>-<%=subTagCnt%>-<%=subTag.getName()%>"><%=subTag.getTitle()%></label>
		                               </li><%
		                               sb.append("<label class=\"hide\" for=\"select-" + dropDwnCnt + "-" + subTagCnt + "-" + subTag.getName() + "\">" + subTag.getTitle() + "</label>");
			                        }
			                    }
	                        }
		                    %></ul>
                        </div><%
                    }
                %></li><%
            }
        }
    %></ul><%
    sb.append("</div>");
    out.print(String.valueOf(sb));
} else {
    out.print("Please set a valid path to badges tags.");
}
%>