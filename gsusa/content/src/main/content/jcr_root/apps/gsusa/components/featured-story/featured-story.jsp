<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	String title = properties.get("title", "FEATURED STORY");

	String description = properties.get("description", "");
	if (WCMMode.fromRequest(request) == WCMMode.EDIT && description.isEmpty()) {
	    description = "No Description";
	}
	String bgcolor = properties.get("bgcolor", "E70C82");
	bgcolor = "rgba(" + hexToDec(bgcolor.substring(0, 2)) + ',' 
	        + hexToDec(bgcolor.substring(2, 4)) + ','
	        + hexToDec(bgcolor.substring(4, 6)) + ", .8)";
%>
<!-- <div><%= title %></div> -->
<div class="thumb" style="background-color: <%= bgcolor %>">
    <span class="icon-photo-camera"></span>
    <div class="contents">
        <h3><%= title %></h3>
        <p class="dek"><%=description%></p>
    </div>
</div>
<section class="story" data-target="story_0"  style="background: url('/etc/designs/gsusa/clientlibs/images/getty_163433067.jpg') no-repeat transparent 0 50% / cover">
    <div class="bg-wrapper" style="background-color: <%= bgcolor %>">
        <div class="header">
            <div class="left-wrapper">
                <span class="icon-photo-camera"></span>
                <h3><%= title %></h3>
                <p class="dek"><%=description%></p>
            </div>
            <span class="icon-cross"></span>
        </div>
        <div class="contents">
            <img src="/etc/designs/gsusa/clientlibs/images/temp.png" alt="" />
        </div>
    </div>
</section>

<%!
    public String hexToDec(String hex) {
    	return Integer.toString(Integer.parseInt(hex.trim(), 16));
    }
%>