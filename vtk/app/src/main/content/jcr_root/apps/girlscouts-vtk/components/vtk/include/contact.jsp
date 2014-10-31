<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>



<div class="row">

<!-- contact -->
  <div class="small-24 large-12 columns">
  	<div class="row">
	  <div class="small-24 large-12 columns">Starting Balance:</div>
 	  <div class="small-24 large-12 columns"><input type="text" name="starting_balance" id="starting_balance" value="<%=FORMAT_COST_CENTS.format(finance.getStartingBalance())%>"/>
 	  </div>
	</div>
  </div>
 </div>