
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
	 if( isHomePage && (err.getTargets()==null || (err.getTargets()!=null && !err.getTargets().contains("home") ) ) )
         continue;
	 if(!isHomePage && err.isSingleView() ){
		 errorsToRmAfterShow.add(err);
	 }
%>
             <li id="_vtkErrMsgId_<%=err.getId()%>">
                <p><strong><%= err.getName()%></strong></p>
                <p><%= err.getUserFormattedMsg()%></p>
                <a href="javascript:void(0)" onclick="rmVtkErrMsg('<%=err.getId()%>')">dismiss</a>
                <!--
                ---- description ----
                <p><%= err.getDescription()%></p>
                ---- error code ----
                <p><%=err.getErrorCode() %></p>
                ---- error time ----
                <p><%=err.getErrorTime()%></p>
                -->
             </li>
   <% } %>
          </ul>
     </div>
 <%} %>