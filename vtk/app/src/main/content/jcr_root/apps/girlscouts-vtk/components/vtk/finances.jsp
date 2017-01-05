<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, org.apache.commons.lang.StringEscapeUtils" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/finance.css" type="text/css">

<script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/react/15.4.1/react.js"></script>
<script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/react/15.4.1/react-dom.js"></script>


<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>

<div id="errInfo"></div>

<%
    String activeTab = "finances";
    boolean showVtkNav = true;
    int qtr = 1;
    boolean isQuarterly = true;
    String sectionClassDefinition="";
    FinanceConfiguration financeConfig = financeUtil.getFinanceConfig(user, troop, user.getCurrentYear());    
%>
<%@include file="include/bodyTop.jsp" %>
hello
<main id="content" class=""></main>
buy

<%@include file="include/bodyBottom.jsp" %>
<script>loadNav('finances');</script>
<script type="text/javascript"  src="/etc/designs/girlscouts-vtk/clientlibs/js/finance.js"></script>


