
<%
//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
java.util.List<org.girlscouts.vtk.ejb.VtkError> errors = org.girlscouts.vtk.utils.VtkUtil.getVtkErrors(request);
if( errors!=null ) {
%>
    <div class="error">
          <ul>
<%
 for(int i=0;i<errors.size();i++){ 
	 org.girlscouts.vtk.ejb.VtkError err = errors.get(i);
%>
             <li>
                        <b><%= err.getName()%> : </b>
                        <%= err.getUserFormattedMsg()%>
                        <!--  
                        ---- description ----
                        <%= err.getDescription()%>
                        ---- error code ----
                        <%=err.getErrorCode() %>
                        -->
             </li>
 <%} %>
          </ul>
     </div>
 <%} %> 