<%


    String err="";
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
	
letmein.add("VTKDemo635");
letmein.add("VTKDemo402");
letmein.add("VTKDemo434");
letmein.add("VTKDemo218");
letmein.add("VTKDemo258");
letmein.add("VTKDemo360");
letmein.add("VTKDemo345");
letmein.add("VTKDemo140");
letmein.add("VTKDemo647");
letmein.add("VTKDemo282");

letmein.add("VTKDemo289");
letmein.add("VTKDemo312");
letmein.add("VTKDemo155");
letmein.add("VTKDemo208");
letmein.add("VTKDemo204");
letmein.add("VTKDemo169");
letmein.add("VTKDemo608");
letmein.add("VTKDemo674");
letmein.add("VTKDemo688");

letmein.add("VTKDemo131");
letmein.add("VTKDemo116");
letmein.add("VTKDemo512");
letmein.add("VTKDemo514");
letmein.add("VTKDemo313");
letmein.add("VTKDemo430");
letmein.add("VTKDemo524");
letmein.add("VTKDemo212");
letmein.add("VTKDemo636");
letmein.add("VTKDemo622");
letmein.add("VTKDemo623");
letmein.add("VTKDemo591");
letmein.add("VTKDemo660");
letmein.add("VTKDemo263");
letmein.add("VTKDemo467");

letmein.add("VTKDemo607");
letmein.add("VTKDemo564");
letmein.add("VTKDemo536");
letmein.add("VTKDemo234");
letmein.add("VTKDemo664");
letmein.add("VTKDemo661");
letmein.add("VTKDemo465");
letmein.add("VTKDemo240");
letmein.add("VTKDemo563");

letmein.add("VTKDemo320");
letmein.add("VTKDemo597");
letmein.add("VTKDemo388");
letmein.add("VTKDemo367");

letmein.add("VTKDemo578");
letmein.add("VTKDemo117");
letmein.add("VTKDemo126");
letmein.add("VTKDemo134");
letmein.add("VTKDemo654");
letmein.add("VTKDemo557");
letmein.add("VTKDemo415");
letmein.add("VTKDemo238");
letmein.add("VTKDemo583");
letmein.add("VTKDemo333");

letmein.add("VTKDemo612");
letmein.add("VTKDemo278");
letmein.add("VTKDemo506");
letmein.add("VTKDemo548");
letmein.add("VTKDemo314");
letmein.add("VTKDemo468");
letmein.add("VTKDemo168");
letmein.add("VTKDemo499");
letmein.add("VTKDemo194");
letmein.add("VTKDemo135");
letmein.add("VTKDemo306");
letmein.add("VTKDemo368");
letmein.add("VTKDemo700");


    if( request.getParameter("p")!=null && letmein.contains( request.getParameter("p") )){

        session.setAttribute("demoSiteUser", true);
        
        %>
        <script>
	        if(typeof(Storage) !== "undefined") {
	        	localStorage.setItem("isShowDemoWelcomePop", "true");
	        } 
        </script>
        <%
        
    }else{
    	err+="Your password was incorrect. Please try again.";
    }
%>
 <script>self.location="/content/girlscouts-demo/en.html?err=<%=err%>"</script>

