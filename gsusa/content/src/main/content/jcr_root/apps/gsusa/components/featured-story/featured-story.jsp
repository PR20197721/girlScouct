<%@include file="/libs/foundation/global.jsp" %>
<%
	String story = properties.get("story", "FEATURED STORY");
%>
<!-- <div><%= story %></div> -->
<div class="thumb">
    <span class="icon-photo-camera"></span>
    <div class="contents">
        <h2><%= story %></h2>
        <p class="dek">Donec nec justo eget felis facilisis fermentum.</p>
    </div>
</div>
<section class="story" data-target="story_0">
    <span class="icon-cross"></span>
    <div class="header">

    </div>
</section>