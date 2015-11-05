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
           <a class="button arrow" href="http://www.girlscoutshop.com" title="shop now">Shop Now</a>
        </div>
        <div class="join-now text-version">
           <h5>Join the Cookie Team</h5>
           <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
           <button class="button arrow" type="submit">Join Now</button>
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