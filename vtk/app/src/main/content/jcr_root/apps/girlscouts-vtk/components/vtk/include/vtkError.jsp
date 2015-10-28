
<%
//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
java.util.List<org.girlscouts.vtk.ejb.VtkError> errors = org.girlscouts.vtk.utils.VtkUtil.getVtkErrors(request);
if( errors!=null ) {
%>
    <div class="error">
          <ul>
<%
boolean isHomePage= false;
for (String selectFragment: slingRequest.getRequestPathInfo().getSelectors()) {
    if ("home".equals(selectFragment)) {
        isHomePage=true;
    }
}


 java.util.List<org.girlscouts.vtk.ejb.VtkError> errorsToRmAfterShow = new java.util.ArrayList<org.girlscouts.vtk.ejb.VtkError>();
 for(int i=0;i<errors.size();i++){ 
	 org.girlscouts.vtk.ejb.VtkError err = errors.get(i);
	 
	 if( !isHomePage && err.getTargets()!=null && err.getTargets().contains("home") )
		   	 continue;
	 
	 
	 
	 if(!isHomePage && err.isSingleView() ){
		 errorsToRmAfterShow.add(err);
	 }
	 
%>
             <li>
                        <b><%= err.getName()%> : </b>
                        <%= err.getUserFormattedMsg()%>
                        <!--  
                        ---- description ----
                        <%= err.getDescription()%>
                        ---- error code ----
                        <%=err.getErrorCode() %>
                        ____ error time ----
                        <%=err.getErrorTime()%>
                        -->
             </li>
 <% }
 
 
 //rm singleViews
 if( errorsToRmAfterShow.size() > 0 ){
	 errors.removeAll(errorsToRmAfterShow);
 }
 org.girlscouts.vtk.utils.VtkUtil.setVtkErrors(request, errors);
 
 %>
          </ul>
     </div>
 <%} %> 