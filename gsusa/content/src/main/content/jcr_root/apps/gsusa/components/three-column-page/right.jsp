<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<!-- right -->
<%
	if (isCookiePage(currentPage)) {
		// All cookie pages share the same right rail.
        String cookieRightPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/cookie-right";
        %>
        <div class="rotator">
           <h5>Get Supper-Fun Girl Scout Stuff</h5>
           <div class="shop-carousel">
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Cookie Charm Bracelet</p></div>
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Some Other product</p></div>
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Badges</p></div>
           </div>
           <a class="button arrow" href="http://www.girlscoutshop.com" title="shop now">SHOP NOW</a>
        </div>
        <cq:include path="cookieRightPath" resourceType="girlscouts/components/styled-parsys" /><%
	} else {
%>
        <div id="rightContent">
             <cq:include path="content/right/par" resourceType="girlscouts/components/styled-parsys" />
        </div>
<%
	}
%>
<!-- END of right -->