<%@include file="/libs/foundation/global.jsp" %>
<%
	String story = properties.get("story", "FEATURED STORY");
	String bgcolor = properties.get("bgcolor", "E70C82");
	bgcolor = "rgba(" + hexToDec(bgcolor.substring(0, 2)) + ',' 
	        + hexToDec(bgcolor.substring(2, 4)) + ','
	        + hexToDec(bgcolor.substring(4, 6)) + ", .8)";
%>
<!-- <div><%= story %></div> -->
<div class="thumb" style="background-color: <%= bgcolor %>">
    <span class="icon-photo-camera"></span>
    <div class="contents">
        <h3><%= story %></h3>
        <p class="dek">Donec nec justo eget felis facilisis fermentum.</p>
    </div>
</div>
<section class="story" data-target="story_0"  style="background: url('/etc/designs/gsusa/clientlibs/images/getty_163433067.jpg') no-repeat transparent 0 50% / cover">
    <div class="bg-wrapper" style="background-color: rgba(231,12,130, .8)">
        <div class="header">
            <div class="left-wrapper">
                <span class="icon-photo-camera"></span>
                <h3>Some title</h3>
                <p class="dek">Donec nec justo eget felis facilisis fermentum.</p>
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