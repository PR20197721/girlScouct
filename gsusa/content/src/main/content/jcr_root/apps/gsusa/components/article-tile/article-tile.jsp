<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
    String tileTitle = properties.get("tileTitle", "");
	String tileSummary = properties.get("tileSummary", "");
	String articleLink = properties.get("articleLink", "");
	String imageSrc = properties.get("imageSrc", "");
	String divId = properties.get("divId", "");
	boolean clickPlay = properties.get("clickPlay", false);
	String videoLink = properties.get("videoLink", "");
	String divId = properties.get("divId", "");
	String modId =  properties.get("modId", "");

%>

<section>
    <%
    if(clickPlay){
        %>
    <a href="<%=articleLink%>">
<% 
    } else{
    %>
	<a href="" onlclick="populateVideoIntoModal(<%=divId%>,<%=videoLink%>)" data-reveal-id="<%=modId%>">
<% 
    }
    %>
		<img src="<%=imageSrc%>"/>
		<div class="text-content" style="background: rgba(36, 184, 238, 0.8)">
			<h3><%=tileTitle%></h3>
			<p><%=articleSummary%></p>
		</div>
	</a>
</section>

<div id="videoModal_0" class="reveal-modal large" data-reveal aria-labelledby="videoModalTitle" aria-hidden="true" role="dialog">
<!--   <h2 id="videoModalTitle">This modal has video</h2>
  <div class="flex-video widescreen vimeo">
    <iframe width="100%" height="720" src="//www.youtube-nocookie.com/embed/wnXCopXXblE?rel=0" frameborder="0" allowfullscreen></iframe>
  </div> -->
  <a class="close-reveal-modal" aria-label="Close">&#215;</a>
</div>
