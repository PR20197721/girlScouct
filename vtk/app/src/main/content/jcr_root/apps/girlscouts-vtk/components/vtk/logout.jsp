<%
	HttpSession httpSession = request.getSession();
httpSession.invalidate();
%>

<div id="error">
	Your session expired.
	<br/>Please log in. <a href="/content/girlscouts-vtk/en/vtk.html">Log In</a>
</div>