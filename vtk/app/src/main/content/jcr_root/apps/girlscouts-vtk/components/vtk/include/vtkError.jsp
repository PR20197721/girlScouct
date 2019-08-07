<%@ page import="org.girlscouts.vtk.exception.VtkError" %><%
    //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    java.util.List<VtkError> errors = VtkUtil.getVtkErrors(request);
    if (errors != null) {
%>
<div class="error">
    <ul>
        <%
            boolean isHomePage = false;
            for (String selectFragment : slingRequest.getRequestPathInfo().getSelectors()) {
                if ("home".equals(selectFragment)) {
                    isHomePage = true;
                }
            }
            java.util.List<VtkError> errorsToRmAfterShow = new java.util.ArrayList<VtkError>();
            for (int i = 0; i < errors.size(); i++) {
                VtkError err = errors.get(i);
                if (!isHomePage && err.getTargets() != null && err.getTargets().contains("home"))
                    continue;
                if (isHomePage && (err.getTargets() == null || (err.getTargets() != null && !err.getTargets().contains("home"))))
                    continue;
                if (!isHomePage && err.isSingleView()) {
                    errorsToRmAfterShow.add(err);
                }
        %>
        <li id="_vtkErrMsgId_<%=err.getId()%>">
            <div style="display:none;">
                <p><strong><%= err.getName()%>
                </strong></p>
                <p><%= err.getUserFormattedMsg()%>
                </p>
                <a href="javascript:void(0)" onclick="rmVtkErrMsg('<%=err.getId()%>')">dismiss</a>
            </div>
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