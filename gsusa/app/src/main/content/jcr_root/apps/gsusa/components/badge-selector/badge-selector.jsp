<%@ page import="java.lang.Exception,
	java.util.Iterator,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:defineObjects />
<style>
	.hide {
        display: none !important;
    }
    /*
    *
    * https://codepen.io/Tont/pen/hdsev
    *
    */
	.dropdown {
        text-align: left;
        display: table;
        table-layout: fixed;
        margin: 0;
        max-width: 405px;
        /* 190+25+190 */
        width: 100%;
        padding: 0;
        color: #222;
        margin-bottom: 12px;
    }

    .dropdown * {
        font-size: 13px;
        -webkit-user-select: none;
        /* Chrome, Opera, Safari */
        -moz-user-select: none;
        /* Firefox 2+ */
        -ms-user-select: none;
        /* IE 10+ */
        user-select: none;
        /* Standard syntax */
    }

    .dropdown > li {
        display: table-cell;
        list-style-type: none;
        position: relative;
        background-color: whitesmoke;
        width: 100%;
        max-width: 190px;
        height: 28px;
        vertical-align: middle;
    }

    .dropdown > li.divider {
        width: 25px;
        background: none;
    }

    .dropdown label,
    .dropdown input[type="checkbox"] + label {
        position: relative;
        display: block;
        margin: 0;
        padding: 0 12px;
        line-height: 28px;
        transition: background 0.3s;
        cursor: pointer;
        color: #222;
        text-decoration: none;
        white-space: nowrap;
    }

    .dropdown > li > label {
        font-weight: bold;
        border: 1px solid darkgray;
        line-height: 26px;
    }
    
    .dropdown > li > label:hover,
    .dropdown > li > input:checked ~ label {
        //background-color: gainsboro;
    }

    .dropdown > li > label:after {
        content: "";
        position: absolute;
        display: block;
        top: 50%;
        right: 10px;
        margin-top: -2px;
        width: 0;
        height: 0;
        border-top: 4px solid black;
        border-bottom: 0 solid black;
        border-left: 4px solid transparent;
        border-right: 4px solid transparent;
        transition: border-bottom .1s, border-top .1s .1s;
    }
    
    .dropdown input:checked ~ label:after {
        border-top: 0 solid black;
        border-bottom: 4px solid black;
        transition: border-top .1s, border-bottom .1s .1s;
    }
    
    .dropdown input {
        display: none;
    }
    /* Submenu */

    .dropdown input:checked ~ .submenu {
        max-height: 300px;
        transition: max-height 0.5s ease-in;
    }

    .dropdown .submenu {
        z-index: 10;
        margin-top: -1px;
        overflow: hidden;
        position: absolute;
        min-width: 100%;
        max-height: 0;
        transition: max-height 0.5s ease-out;
    }

    .dropdown .submenu ul {
        padding: 0;
        list-style-type: none;
        background-color: whitesmoke;
        box-shadow: 0 0 1px gainsboro;
        border: 1px solid darkgray;
        margin: 0;
    }

    .dropdown .submenu li label:hover {
        background-color: gainsboro;
        outline: 1px solid darkgray;
    }

    .dropdown .submenu li input:checked ~ label {
        font-weight: bold;
    }
    
    .dropdown .submenu .divider {
        margin: 12px;
        border-top: 1px solid black;
    }
    /*
    *
    * Tags
    *
    */

    .tags {
        width: 100%;
        text-align: left;
        margin-bottom: 20px;
        min-height: 24px;
    }

    .tags label {
        display: inline-block;
        font-size: 11px;
        cursor: pointer;
        color: #60af79;
        margin-right: 12px;
    }

    .tags label:before {
        content: "\00d7"; /* "Times" CSS symbol */
        font-size: 16px;
        margin-right: 3px;
        color: darkgray;
    }
</style>
    
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