<%@ page import="org.girlscouts.web.councilrollout.CouncilCreator" %>
<%@include file="/libs/foundation/global.jsp" %>

HANKE
<%
    CouncilCreator creator = sling.getService(CouncilCreator.class);
%><%= creator.create() %>
