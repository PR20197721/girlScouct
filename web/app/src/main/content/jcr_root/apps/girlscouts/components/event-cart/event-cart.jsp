<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
org.girlscouts.vtk.ejb.*,
org.girlscouts.vtk.helpers.*,
org.girlscouts.vtk.dao.*,
org.girlscouts.vtk.models.User" %>

<div class="cart">
	<dl class="accordion" data-accordion>
		<dt data-target="drop-down-cart"><h6 class = "on">My Events</h6></dt>
		<dd class="event-cart-navigation">
			<ul id="event-cart-nav-list">
			</ul>
		</dd>
	</dl>
</div>
<%
HttpSession session = request.getSession();
if(session.getAttribute("event-cart") != null){
	%><script> retrieveEvents("/content/girlscouts-shared/event-cart.html"); </script><%
} else{
	%><script> 
	createCart("/content/girlscouts-shared/event-cart.html"); 
	</script><%
}
%>