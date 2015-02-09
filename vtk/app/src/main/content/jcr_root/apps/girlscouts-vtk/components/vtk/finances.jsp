<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/finance.js"></script>
<div id="errInfo"></div>
<%
        String activeTab = "finances";
        boolean showVtkNav = true;
		
        int qtr = 0;
        
        boolean isQuarterly = true;
        
        try { 
          qtr = Integer.parseInt( request.getParameter("qtr") );
        }catch(Exception e){out.println("Invalid qtr"); 
          return;
        }

        Finance finance = financeUtil.getFinances(user, troop, qtr);
        if( finance ==null ){
          finance= new Finance();
        }

        double acc_out = (finance.getGsusaRegistration() + finance.getServiceActivitiesEvents() + finance.getProductSalesProceeds() + finance.getTroopActivities() + finance.getTroopSupplies() + finance.getGsStorePurchases());
        double acc_rcv = (finance.getStartingBalance() + finance.getTroopDues() + finance.getSponsorshipDonations() + finance.getProductSalesProceeds()+ finance.getApprovedMoneyEarningActivity()+ finance.getInterestOnBankAccount() );
        double balance = acc_rcv - acc_out;
        
        
        boolean hasAdminPermissions = true;
        String financeFieldTag = "";
        String save_btn = "";
        if(hasAdminPermissions){
        	financeFieldTag = "<input type=\"text\" id=\"%s\" onblur=\"updateTotals()\" value=\"%s\"/>";
          save_btn = "<a role=\"button\" aria-label=\"submit form\" href=\"#\" class=\"button\">Save</a>";
        } else{
        	financeFieldTag = "<p name=\"%s\" id=\"%s\">&s</p>";
        }
%>

<%@include file="include/tab_navigation.jsp"%>

<div id="panelWrapper" class="row content meeting-detail">
  
  <%@include file="include/utility_nav.jsp"%>

  <%@include file="include/finances_navigator.jsp"%>

  <div class="column large-20 medium-20 large-centered medium-centered small-24">

    
    
    <form class="cmxform" id="financeForm">
      <input type="hidden" id="qtr" name="qtr" value="<%=qtr%>"/>
      <div class="errorMsg error"></div>
      
      <div class="row collapse">

        <section class="column large-12 medium-12">
          <h6>current income</h6>
          <ul class="large-block-grid-2 small-block-grid-2 text-right">
            <li>Beginning Balance:</li>
            <li><%=String.format(financeFieldTag, "income1", FORMAT_COST_CENTS.format(finance.getStartingBalance())) %></li>
            <li>Troop Dues:</li>
            <li><%=String.format(financeFieldTag, "income2", FORMAT_COST_CENTS.format(finance.getTroopDues())) %></li>
            <li>Sponsorship/Donations:</li>
            <li><%=String.format(financeFieldTag, "income3", FORMAT_COST_CENTS.format(finance.getSponsorshipDonations())) %></li>
            <li>Product Sales Proceeds:</li>
            <li><%=String.format(financeFieldTag, "income4", FORMAT_COST_CENTS.format(finance.getProductSalesProceeds())) %></li>
            <li>Approved Money-Earnings Activities:</li>
            <li><%=String.format(financeFieldTag, "income5", FORMAT_COST_CENTS.format(finance.getApprovedMoneyEarningActivity())) %></li>
            <li>Interest on Bank Accounts:</li>
            <li><%=String.format(financeFieldTag, "income6", FORMAT_COST_CENTS.format(finance.getInterestOnBankAccount())) %></li>
          </uL>
        </section>

        <section class="column large-12 medium-12">
           <h6>current expenses</h6>
           <ul class="large-block-grid-2 small-block-grid-2 text-right">
             <li>GSUSA Registrations:</li>
             <li><%=String.format(financeFieldTag, "expense1", FORMAT_COST_CENTS.format(finance.getGsusaRegistration())) %></li>
             <li>Service Activities/Events:</li>
             <li><%=String.format(financeFieldTag, "expense2", FORMAT_COST_CENTS.format(finance.getServiceActivitiesEvents())) %></li>
             <li>Council Programs/Camp:</li>
             <li><%=String.format(financeFieldTag, "expense3", FORMAT_COST_CENTS.format(finance.getCouncilProgramsCamp())) %></li>
             <li>Troop Activities:</li>
             <li><%=String.format(financeFieldTag, "expense4", FORMAT_COST_CENTS.format(finance.getTroopActivities())) %></li>
             <li>Troop Supplies:</li>
             <li><%=String.format(financeFieldTag, "expense5", FORMAT_COST_CENTS.format(finance.getTroopSupplies())) %></li>
             <li>GS Store Purchase:</li>
             <li><%=String.format(financeFieldTag, "expense6", FORMAT_COST_CENTS.format(finance.getSponsorshipDonations())) %></li>
           </ul>
        </section>
      </div><!--/row-->
      <!-- totals -->
      <div class="text-right row collapse">
        <section>
          <h6 class="clearfix"><span class="column small-20">Total Income:</span>  <span id="total_income" class="column small-4">$<%=FORMAT_COST_CENTS.format(acc_rcv)%></span></h6>
        </section>
        <section>
          <h6 class="clearfix"><span class="column small-20">Total Expenses:</span> <span id="total_expenses" class="column small-4">$<%=FORMAT_COST_CENTS.format(acc_out)%></span></h6>
        </section>
        <section>
          <h6 class="clearfix"><span class="column small-20">Current Balance:</span> <span id="current_balance" class="column small-4">$<%=FORMAT_COST_CENTS.format(balance)%></span></h6>
        </section>
        <%=save_btn%>
       </div>
      </div>
    </form>

  </div>
</div>
