<%@ page
        import="com.day.text.Text, org.girlscouts.vtk.auth.permission.Permission, com.google.common.collect.*, com.google.common.collect.BiMap, com.google.common.collect.HashBiMap,org.girlscouts.vtk.models.Contact,org.girlscouts.vtk.models.ContactExtras" %>
<%@ page import="org.girlscouts.vtk.models.YearPlanComponent" %>
<%@ page import="java.util.Calendar" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<div id="error-message"></div>
<%
    String activeTab = "myTroop";
    boolean showVtkNav = true;
    String sectionClassDefinition = "";
%>
<%@include file="include/bodyTop.jsp" %>
<%if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
    <%@include file="mytroop_react.jsp" %>
<%} else { %>
    <%@include file='myTroopImg.jsp' %>
    <%@include file="myTroopOff.jsp" %>
<% } %>
<script>loadNav('myTroop')</script>
<%@include file="include/bodyBottom.jsp" %>
<%!
    public boolean isRenewMembership(int membershipYear) {
        Calendar rightNow = Calendar.getInstance();

        if (membershipYear == rightNow.get(Calendar.YEAR) &&
                (rightNow.get(Calendar.MONTH) > 2 && rightNow.get(Calendar.MONTH) < 9)
        ) {
            return true;
        }
        return false;
    }
%>