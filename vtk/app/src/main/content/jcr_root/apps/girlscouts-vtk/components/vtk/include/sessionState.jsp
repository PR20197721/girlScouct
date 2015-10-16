<%!

void handleFailedApiConfigState(org.girlscouts.vtk.auth.models.ApiConfig apiConfig, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, boolean thisIsHome) {
	if (!thisIsHome) {
		if( apiConfig==null ){
		    String redirectTo = "/content/girlscouts-vtk/controllers/auth.sfauth.html?action=signin";
		    // GSWS-190 Add refererCouncil
		    String refererCouncil = request.getParameter("refererCouncil");
		    if (refererCouncil != null && !refererCouncil.isEmpty()) {
		        redirectTo = redirectTo + "&refererCouncil=" + refererCouncil;
		    }
		   try{
			   response.sendRedirect(redirectTo);
		   }catch(Exception e){e.printStackTrace();}
		    return;     
		}
	}
}

%>