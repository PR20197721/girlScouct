<%@include file="/libs/foundation/global.jsp" %>
<%
	String story = properties.get("story", "FEATURED STORY");
%>
<!-- <div><%= story %></div> -->
<div class="thumb" style="background-color: rgba(249, 164, 44, .8)">
    <span class="icon-photo-camera"></span>
    <div class="contents">
        <h3><%= story %></h3>
        <p class="dek">Donec nec justo eget felis facilisis fermentum.</p>
    </div>
</div>
<section class="story" data-target="story_0"  style="background: url('/etc/designs/gsusa/clientlibs/images/getty_163433067.jpg') no-repeat transparent 0 50% / cover">
    <div class="bg-wrapper" style="background-color: rgba(249, 164, 44, .8)">
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