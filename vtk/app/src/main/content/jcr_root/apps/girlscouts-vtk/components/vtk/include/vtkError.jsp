
<%
//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
java.util.List<org.girlscouts.vtk.ejb.VtkError> errors = org.girlscouts.vtk.utils.VtkUtil.getVtkErrors(request);
if( errors!=null ) {
%>
    <div class="error">
          <ul>
<%
 java.util.List<org.girlscouts.vtk.ejb.VtkError> errorsToRmAfterShow = new java.util.ArrayList<org.girlscouts.vtk.ejb.VtkError>();
 for(int i=0;i<errors.size();i++){ 
	 org.girlscouts.vtk.ejb.VtkError err = errors.get(i);
	 if( err.isSingleView() ){
		 errorsToRmAfterShow.add(err);
	 }
	 
%>
             <li><%=errorsToRmAfterShow.size() %>
                        <b><%= err.getName()%> : </b>
                        <%= err.getUserFormattedMsg()%>
                        <!--  
                        ---- description ----
                        <%= err.getDescription()%>
                        ---- error code ----
                        <%=err.getErrorCode() %>
                        -->
             </li>
 <% }
 
 
 //rm singleViews
 if( errorsToRmAfterShow.size() > 0 ){
	 errors.removeAll(errorsToRmAfterShow);
 }
 for(int i=0;i<errors.size();i++){ 
     org.girlscouts.vtk.ejb.VtkError err = errors.get(i);
     errors.remove(i);
 }
 %>
          </ul>
     </div>
 <%} %> 