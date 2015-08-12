<% 

org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;
try{
    apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
}catch(Exception e){e.printStackTrace();}

if( apiConfig!=null && apiConfig.getUser().isAdmin() ){
    response.sendRedirect("/content/girlscouts-vtk/en/vtk.resource.html");
    return;
}else{            
    response.sendRedirect("/content/girlscouts-vtk/en/vtk.plan.html");
    return;
}
%>