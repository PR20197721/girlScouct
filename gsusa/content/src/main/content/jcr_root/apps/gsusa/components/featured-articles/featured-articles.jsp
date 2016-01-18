<%--
  blockquote component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>

<% 
	String articlePath1 = properties.get("articlePath1", "");
	request.setAttribute("articlePath", articlePath1);
    %>
<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    <div id="videoModal_0" class="reveal-modal large" data-reveal aria-labelledby="videoModalTitle" aria-hidden="true" role="dialog">
<!--   <h2 id="videoModalTitle">This modal has video</h2>
  <div class="flex-video widescreen vimeo">
    <iframe width="100%" height="720" src="//www.youtube-nocookie.com/embed/wnXCopXXblE?rel=0" frameborder="0" allowfullscreen></iframe>
  </div> -->
  <a class="close-reveal-modal" aria-label="Close">&#215;</a>
</div>