<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!--PAGE STRUCTURE: MAIN-->
<% String headerPath = (String) request.getAttribute("headerPath"); %>
  <div id="main" class="row collapse">
<div class="large-push-5 medium-24 small-24 columns mainRight">
    <div class="row mainRightBottom">
				<div class="large-18 medium-18 small-24 columns rightBodyLeft">

<h3>We're Very Sorry</h3>
                    <br>
                    <br>
<b>We can't seem to find the page you're looking for</b>
                    <div class="large-18 medium-18 small-24 columns">
                    <cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
                    </div>
	      <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
			</div>
    </div>
      </div>
</div>

