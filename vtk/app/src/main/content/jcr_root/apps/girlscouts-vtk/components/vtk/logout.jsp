<%
	HttpSession httpSession = request.getSession();
httpSession.invalidate();
%>