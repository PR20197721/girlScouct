<%@ page import="org.girlscouts.vtk.exception.VtkError, java.util.ArrayList, java.util.List,org.girlscouts.vtk.osgi.component.util.VtkUtil" %>
<div id="panelWrapper" class="row meeting-detail content">
    <div class="columns large-20 large-centered">
        <%
            List<VtkError> errors = VtkUtil.getVtkErrors(request);
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
                    List<VtkError> errorsToRmAfterShow = new ArrayList<VtkError>();
                    for (int i = 0; i < errors.size(); i++) {
                        VtkError err = errors.get(i);
                        if (!isHomePage && err.getTargets() != null && err.getTargets().contains("home")) {
                            continue;
                        }
                        if (isHomePage && (err.getTargets() == null || (err.getTargets() != null && !err.getTargets().contains("home")))) {
                            continue;
                        }
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
        <%
            }
        %>
        <p>We're sorry you're having trouble accessing the Volunteer Toolkit. We're here to help!</p>
        <p>The Volunteer Toolkit is a digital planning tool currently available to troop leaders, co-leaders, council-selected administrative volunteers, and primary caregivers of Girl Scouts with active <%= VtkUtil.getCurrentGSYear() + 1 %> memberships.</p>
        <p>If you feel you've reached this screen in error, click Contact Us at the top of the page and we'll help you get it sorted out.</p>
        <p>Thank you!</p>
    </div>
</div>