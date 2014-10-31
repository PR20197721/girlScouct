<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%
Finance finance = financeUtil.getFinances(user, troop, 1);
if( finance ==null )
	finance= new Finance();

%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>

<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/finance.js"></script>




<h3>QXXX 2014</h3>
<form class="cmxform" id="financeForm">
<div class="errorMsg error"></div>
<div class="row">

<!-- current income -->
  <div class="small-24 large-12 columns">
  	<div class="row">
	  <div class="small-24 large-12 columns">Starting Balance:</div>
 	  <div class="small-24 large-12 columns"><input type="text" name="starting_balance" id="starting_balance" value="<%=finance.getStartingBalance()%>"/>
 	  </div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Dues:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_dues" name="troop_dues" value="<%=finance.getTroopDues()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Sponsorship/Donations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="sponsorship_donations" name="sponsorship_donations" value="<%=finance.getSponsorshipDonations()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Product Sales Proceeds:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="product_sales_proceeds" name="product_sales_proceeds" value="<%=finance.getProductSalesProceeds()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Approved Money-Earnings Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="amea" name="amea" value="<%=finance.getApprovedMoneyEarningActivity()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Interest on Bank Accounts:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="bank_interest" name="bank_interest" value="<%=finance.getInterestOnBankAccount()%>"/></div>
	</div>
  </div>
  
  <!-- current exp -->
  <div class="small-24 large-12 columns">
    <div class="row">
	  <div class="small-24 large-12 columns">GSUSA Registrations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="gsusa_registrations" name="gsusa_registrations" value="<%=finance.getGsusaRegistration()%>"/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Service Activities/Events:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="service_ae" name="service_ae" value="<%=finance.getServiceActivitiesEvents()%>"/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Council Programs/Camp:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="council_pc" name="council_pc" value="<%=finance.getCouncilProgramsCamp()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_activities" name="troop_activities" value="<%=finance.getTroopActivites()%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Supplies:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_supplies" name="troop_supplies" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">GS Store Purchase:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="gs_store_purchase" name="gs_store_purchase" value=""/></div>
	</div>
  </div>
 
</div>

<!-- totals -->
<div class="row">
 <div class="small-24 large-12 columns">
	<div class="row">
	  <div class="small-24 large-12 columns"></div>
 	  <div class="small-24 large-12 columns"></div>
	</div>
 </div>
 <div class="small-24 large-12 columns">
	<div class="row">
	  <div class="small-24 large-12 columns"></div>
 	  <div class="small-24 large-12 columns"></div>
	</div>
    <div class="row">
	  <div class="small-24 large-12 columns">Total Income:</div>
 	  <div class="small-24 large-12 columns">XXX</div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Total Expenses:</div>
 	  <div class="small-24 large-12 columns">XXX</div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Current Balance:</div>
 	  <div class="small-24 large-12 columns">XXX</div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns"></div>
 	  <div class="small-24 large-12 columns"><input type="button" name="" value="Save" id="updateFinances" onclick="checkFinances()"/>
 	 
 	  </div>
	</div>
  </div>
 </div>
 </form>