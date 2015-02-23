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
		
        int qtr = 1;
        
        boolean isQuarterly = true;
        
        try { 
		if (request.getParameter("qtr") != null) {
			qtr = Integer.parseInt( request.getParameter("qtr") );
		}
        }catch(NumberFormatException nfe){
		nfe.printStackTrace();
        }

        Finance finance = financeUtil.getFinances(troop, qtr);
        FinanceConfiguration financeConfig = financeUtil.getFinanceConfig(troop);
        
        List<String> expenseFields = financeConfig.getExpenseFields();
        List<String> incomeFields = financeConfig.getIncomeFields();
        
        if( finance ==null ){
          finance= new Finance();
        }
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content meeting-detail">
<%@include file="include/utility_nav.jsp"%>
<%
	if ((SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) && sessionFeatures.contains(SHOW_FINANCE_FEATURE)) {
		String financeFieldTag = "";
		String save_btn = "";
		if(sessionFeatures.contains(SHOW_PARENT_FEATURE)){
			financeFieldTag = "<p name=\"%s\" id=\"%s\">%s</p>";
		} else{
			financeFieldTag = "<input type=\"text\" id=\"%s\" name=\"%s\" onblur=\"updateTotals()\" value=\"%s\"/>";
			save_btn = "<a id=\"saveFinanceFieldFormButton\" role=\"button\" onclick=\"saveFinances()\" class=\"button save disabled\">Save</a>";
		}
%>
<%@include file="include/finances_navigator.jsp"%>
<div class="column large-20 medium-20 large-centered medium-centered small-24">
	<form class="cmxform" id="financeForm"  onchange="enableSaveButton()">
		<input type="hidden" id="qtr" name="qtr" value="<%=qtr%>"/>
		<div class="errorMsg error"></div>
		<div class="row">
			<section class="column large-12 medium-12">
				<h6>current income</h6>
				<ul class="large-block-grid-2 small-block-grid-2 text-right">
<%
		for(int i = 0; i < incomeFields.size(); i++){
			String tempField = incomeFields.get(i);
%>
					<li><p><%=tempField%>:</p></li> 
					<li><%=String.format(financeFieldTag, "income" + (i + 1), tempField, FORMAT_COST_CENTS.format(finance.getIncomeByName(tempField))) %></li>
<%
		}
%>
				</ul>
			</section>
			<section class="column large-12 medium-12">
				<h6>current expenses</h6>
				<ul class="large-block-grid-2 small-block-grid-2 text-right">
<%
		for(int i = 0; i < expenseFields.size(); i++){
			String tempField = expenseFields.get(i);
%>
					<li><p><%=tempField%>:</p></li> 
					<li><%=String.format(financeFieldTag, "expense" + (i + 1), tempField, FORMAT_COST_CENTS.format(finance.getExpenseByName(tempField))) %></li>
<%
		}
%>
				</ul>
			</section>
		</div>
		<!-- totals -->
		<div class="text-right row collapse">
			<section>
				<h6 class="clearfix"><span class="column small-20">Total Income:</span>  <span id="total_income" class="column small-4">0.00</span></h6>
			</section>
			<section>
				<h6 class="clearfix"><span class="column small-20">Total Expenses:</span> <span id="total_expenses" class="column small-4">0.00</span></h6>
			</section>
			<section>
				<h6 class="clearfix"><span class="column small-20">Current Balance:</span> <span id="current_balance" class="column small-4">0.00</span></h6>
			</section>
			<%=save_btn%>
		</div>
	</form>
</div>
<script>
	$( document ).ready(function() {
		updateTotals();
	});
</script>
<%
	} else {
%>
<div class="columns large-20 large-centered">
	<h3>Coming in future releases:</h3> 
	<ul>
		<li>- Create and manage your troop's financial report</li>
		<li>- Share with council personnel and with troop parents</li>
	</ul>
</div>
<%
	}
%>
</div>
