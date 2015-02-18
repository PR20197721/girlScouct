<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

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
          financeFieldTag = "<input type=\"text\" name=\"%s\" id=\"%s\" onblur=\"updateTotals()\" value=\"%s\"/>";
          save_btn = "<a role=\"button\" aria-label=\"submit form\" href=\"#\" class=\"button\">Save</a>";
        } else{
          financeFieldTag = "<p name=\"%s\" id=\"%s\">&s</p>";
        }
%>

<%@include file="../include/tab_navigation.jsp"%>

<div id="panelWrapper" class="row content meeting-detail finances">
  
  <%@include file="../include/utility_nav.jsp"%>

  <div class="column large-20 medium-20 large-centered medium-centered small-24">
    
    <form class="cmxform" id="financeForm">
      <input type="hidden" id="qtr" name="qtr" value="<%=qtr%>"/>
      <div class="errorMsg error"></div>
      
      <div class="row collapse opts">
        <span class="column small-10 large-5 medium-7">Reporting Frequency:</span>
        <select class="columns small-6 large-3 medium-5 left">
          <option value="Quarterly">Quarterly</option>
          <option value="Yearly">Yearly</option>
        </select>
      </div>

      <div class="row collapse">

        <section class="column large-12 medium-12">
          <h6>income categories</h6>
          <p class="text-right">show in finances</p>
          <ul class="large-block-grid-2 small-block-grid-2 text-left">
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_1" id="ch_1"><label for="ch_1"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_2" id="ch_2"><label for="ch_2"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_3" id="ch_3"><label for="ch_3"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_4" id="ch_4"><label for="ch_4"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_5" id="ch_5"><label for="ch_5"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_6" id="ch_6"><label for="ch_6"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_7" id="ch_7"><label for="ch_7"></label></li>
            <li><input type="text" placeholder="Beginning Balance:"/></li>
            <li><input type="checkbox" name="ch_8" id="ch_8"><label for="ch_8"></label></li>
          </uL>
        </section>

        <section class="column large-12 medium-12">
           <h6>expense categories</h6>
           <p class="text-right">show in finances</p>
           <ul class="large-block-grid-2 small-block-grid-2 text-left">
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_9" id="ch_9"><label for="ch_9"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_10" id="ch_10"><label for="ch_10"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_11" id="ch_11"><label for="ch_11"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_12" id="ch_12"><label for="ch_12"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_13" id="ch_13"><label for="ch_13"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_14" id="ch_14"><label for="ch_14"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_15" id="ch_15"><label for="ch_15"></label></li>
             <li><input type="text" placeholder="Beginning Balance:"/></li>
             <li><input type="checkbox" name="ch_16" id="ch_16"><label for="ch_16"></label></li>
           </ul>
        </section>
      </div><!--/row-->
      <!-- totals -->
      <div class="text-right row collapse">
        <%=save_btn%>
      </div>

    </form>

  </div>
</div>
