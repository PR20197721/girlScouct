<%
	String name = user.getName();
	String logoutUrl = "/content/girlscouts-vtk/controllers/auth.html";
%>
var html = '<span>Hello <%= name %> .</span> <a href="<%= logoutUrl %>" class="signout">SIGN OUT</a>';
$('.login').html(html);