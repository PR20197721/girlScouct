<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%
int qtr= 0;
try{ qtr = Integer.parseInt( request.getParameter("qtr") ); }catch(Exception e){out.println("Invalid qtr"); return;}
Finance finance = financeUtil.getFinances(user, user.getCurrentYear(), troop, qtr);
if( finance ==null ){
	finance= new Finance();
}

double acc_out = (finance.getGsusaRegistration() + finance.getServiceActivitiesEvents() + finance.getProductSalesProceeds() + finance.getTroopActivities() + finance.getTroopSupplies() + finance.getGsStorePurchases());
double acc_rcv = (finance.getStartingBalance() + finance.getTroopDues() + finance.getSponsorshipDonations() + finance.getProductSalesProceeds()+ finance.getApprovedMoneyEarningActivity()+ finance.getInterestOnBankAccount() );
double balance = acc_rcv - acc_out;

//To be set using permissions
boolean hasAdminPermissions = true;

String financeFieldTag = "";
if(hasAdminPermissions){
	financeFieldTag = "<input type=\"text\" name=\"%s\" id=\"%s\" onblur=\"updateTotals()\" value=\"%s\"/>";
} else{
	financeFieldTag = "<p name=\"%s\" id=\"%s\">&s</p>";
}


%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/finance.js"></script>

<div id="panelWrapper" class="row content">
	 <div class="column large-20 large-centered">
		
		<%@include file="finances_navigator.jsp"%>
		<form class="cmxform" id="financeForm">
		<input type="hidden" id="qtr" name="qtr" value="<%=qtr%>"/>
		<div class="errorMsg error"></div>
		<div class="row">


<!-- current income -->
  <div class="small-24 large-12 columns">
  	<div class="row">
	  <div class="small-24 large-12 columns">Starting Balance:</div>
 	  <div class="small-24 large-12 columns"><%=String.format(financeFieldTag, "starting_balance", "starting_balance", FORMAT_COST_CENTS.format(finance.getStartingBalance())) %>
 	  
 	  </div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Dues:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_dues" name="troop_dues" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getTroopDues())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Sponsorship/Donations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="sponsorship_donations" onblur="updateTotals()" name="sponsorship_donations" value="<%=FORMAT_COST_CENTS.format(finance.getSponsorshipDonations())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Product Sales Proceeds:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="product_sales_proceeds" onblur="updateTotals()" name="product_sales_proceeds" value="<%=FORMAT_COST_CENTS.format(finance.getProductSalesProceeds())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Approved Money-Earnings Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="amea" name="amea" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getApprovedMoneyEarningActivity())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Interest on Bank Accounts:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="bank_interest" name="bank_interest" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getInterestOnBankAccount())%>"/></div>
	</div>
  </div>
  
  <!-- current exp -->
  <div class="small-24 large-12 columns">
    <div class="row">
	  <div class="small-24 large-12 columns">GSUSA Registrations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="gsusa_registrations" name="gsusa_registrations" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getGsusaRegistration())%>"/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Service Activities/Events:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="service_ae" name="service_ae" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format( finance.getServiceActivitiesEvents())%>"/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Council Programs/Camp:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="council_pc" name="council_pc" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getCouncilProgramsCamp())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_activities" name="troop_activities" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getTroopActivities())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Supplies:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_supplies" name="troop_supplies" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getTroopSupplies())%>"/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">GS Store Purchase:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="gs_store_purchase" name="gs_store_purchase" onblur="updateTotals()" value="<%=FORMAT_COST_CENTS.format(finance.getGsStorePurchases())%>"/></div>
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
		 	  <div class="small-24 large-12 columns" id="total_income">$<%=FORMAT_COST_CENTS.format(acc_rcv)%></div>
			</div>
			<div class="row">
			  <div class="small-24 large-12 columns">Total Expenses:</div>
		 	  <div class="small-24 large-12 columns" id="total_expenses">$<%= FORMAT_COST_CENTS.format(acc_out)%></div>
			</div>
			<div class="row">
			  <div class="small-24 large-12 columns">Current Balance:</div>
		 	  <div class="small-24 large-12 columns" id="current_balance">$<%=FORMAT_COST_CENTS.format(balance) %></div>
			</div>
			<div class="row">
			  <div class="small-24 large-12 columns"></div>
		 	  
		 	  <div class="small-24 large-12 columns"><input type="button" name="" value="Save" id="updateFinances" onclick="checkFinances()"/>
		 	 
		 	  </div>
			</div>
		  </div>
		 </div>
		 </form>
	</div>
</div><!--/panelWrapper-->

