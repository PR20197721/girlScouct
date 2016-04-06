<%
    if( request.getParameter("p")!=null && request.getParameter("p").equals("letmein") && 
      request.getParameter("u")!=null && request.getParameter("u").equals("gsdemouser")){

        session.setAttribute("demoSiteUser", true);
        %><script>self.location="/content/girlscouts-demo/en.html"</script><%

    }else{
        %><%@include file="content.jsp" %><%
    }
%>


