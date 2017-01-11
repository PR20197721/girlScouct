    <div id="fullNav" class="hide-for-print"></div>
    <div id="panelWrapper" class="row <%=sectionClassDefinition %> content meeting-detail">
    <div id="vtkErrMsg"></div>
    
    <% if(!org.girlscouts.vtk.utils.VtkUtil.getUser(request.getSession()).getCurrentYear().equals( org.girlscouts.vtk.utils.VtkUtil.getCurrentGSYear()+"") &&
        ( "myTroop".equals(activeTab) || "resource".equals(activeTab)  || "finances".equals(activeTab) ||
            "reports".equals(activeTab)  || "milestones".equals(activeTab) )
        ){%>
       
        No access.
        <script>loadNav('myTroop')</script>
        <%@include file="bodyBottom.jsp" %>
        <% return;
    }%>