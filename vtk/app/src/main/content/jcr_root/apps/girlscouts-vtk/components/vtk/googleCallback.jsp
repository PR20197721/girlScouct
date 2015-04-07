<%
System.err.println("google");
//https://accounts.google.com/o/oauth2/auth?client_id=415198072678-176k4l71spaqjfeis5gjugbommv3bgla.apps.googleusercontent.com&redirect_uri=http://localhost:4503/content/girlscouts-vtk/controllers/vtk.googleCallback.html&response_type=code&scope=https://www.googleapis.com/auth/analytics.readonly
%>

<%=request.getParameter("code")%>