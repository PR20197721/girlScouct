<%@include file="/libs/foundation/global.jsp" %>
<%-- DO NOT REMOVE THIS LINE. For eagle checking --%>
<!-- 404 GirlScouts -->
<!--PAGE STRUCTURE: MAIN-->
<% String headerPath = (String) request.getAttribute("headerPath"); %>
  <div id="main" class="row collapse page-404">
	<div class="small-13 columns small-centered">
		<p></p>
        <img src="/content/dam/girlscouts-shared/images/banners/medium/404.jpg">
        <p><h3>We're very sorry.</h3></p>

        <p><strong>We can't seem to find the page you're looking for.</strong></p>
        
        <p>Use the global navigation above, or try a search below.</p>
        
        <div class="searchBar columns medium-6 search-box large-10 large-centered">
            <cq:include path="<%= headerPath + "/search-box" %>" resourceType="gsusa/components/search-box" />
            <p></p>
        </div>
	      <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
	</div>
</div>