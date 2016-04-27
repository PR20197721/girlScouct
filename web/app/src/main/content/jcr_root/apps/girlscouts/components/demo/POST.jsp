<%
    if( request.getParameter("p")!=null && request.getParameter("p").equals("letmein")){

        session.setAttribute("demoSiteUser", true);
        %><script>self.location="/content/girlscouts-demo/en.html"</script><%

    }else{
        %><%@include file="content.jsp" %><%
    }
%>


