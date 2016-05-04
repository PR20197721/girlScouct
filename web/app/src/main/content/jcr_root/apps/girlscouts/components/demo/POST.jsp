<%
    java.util.List<String> letmein= new java.util.ArrayList<String>();
	letmein.add("VTKDemo321");    
	letmein.add("VTKDemo590");
	letmein.add("VTKDemo346");
	letmein.add("VTKDemo258");
	letmein.add("VTKDemo376");
	letmein.add("VTKDemo642");
	letmein.add("VTKDemo497");
	letmein.add("VTKDemo387");
	letmein.add("VTKDemo192");
	letmein.add("VTKDemo438");
	letmein.add("VTKDemo687");
	letmein.add("VTKDemo634");
	letmein.add("VTKDemo110");
	letmein.add("VTKDemo441");
	letmein.add("VTKDemo1000");
       System.err.println("tata demo: "+request.getParameter("p") +" : "+ letmein.contains( request.getParameter("p") ) );
    if( request.getParameter("p")!=null && letmein.contains( request.getParameter("p") )){

        session.setAttribute("demoSiteUser", true);

    }
%>
 <script>self.location="/content/girlscouts-demo/en.html?"</script>

