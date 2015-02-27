<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, org.apache.commons.lang.StringEscapeUtils" %>
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
        FinanceConfiguration financeConfig = financeUtil.getFinanceConfig(troop, user.getCurrentYear());
        try { 
		String period = financeConfig.getPeriod();
		if(period != null && period.equals("Yearly")){
			qtr = 0;
			isQuarterly = false;
		}
		if (request.getParameter("qtr") != null) {
			qtr = Integer.parseInt( request.getParameter("qtr") );
		}
        }catch(NumberFormatException nfe){
		nfe.printStackTrace();
        }

        Finance finance = financeUtil.getFinances(troop, qtr, user.getCurrentYear());
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

			financeFieldTag = "<p id=\"%s\" name=\"%s\">&#36;%s</p>";
		} else{
			financeFieldTag = "<input type=\"text\" id=\"%s\" name=\"%s\" onkeyDown=\"enableSaveButton()\" oninput=\"enableSaveButton()\" onpaste=\"enableSaveButton()\" onblur=\"updateTotals()\" maxlength=\"11\" value=\"&#36;%s\"/>";
			save_btn = "<a id=\"saveFinanceFieldFormButton\" role=\"button\" onclick=\"saveFinances()\" class=\"button save disabled\">SEND</a>";
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
				<ul id="incomeFields" class="large-block-grid-2 small-block-grid-2 text-right">
					<%
							double incomeTotal = 0.0;
							for(int i = 0; i < incomeFields.size(); i++){
								String tempField = incomeFields.get(i);
					%>
						<%incomeTotal +=  finance.getIncomeByName(tempField);%>
						<li><p><%=tempField%>:</p></li> 
						<li><%=String.format(financeFieldTag, "income" + (i + 1), StringEscapeUtils.escapeHtml(tempField), FORMAT_COST_CENTS.format(finance.getIncomeByName(tempField))) %></li>
					<%
							}
					%>
				</ul>
			</section>
			<section class="column large-12 medium-12">
				<h6>current expenses</h6>
				<ul id="expenseFields" class="large-block-grid-2 small-block-grid-2 text-right">
					<%
							double expenseTotal = 0.0;
							for(int i = 0; i < expenseFields.size(); i++){
								String tempField = expenseFields.get(i);
					%>
							<%expenseTotal += finance.getExpenseByName(tempField); %>
							<li><p><%=tempField%>:</p></li>
							<li><%=String.format(financeFieldTag, "expense" + (i + 1), StringEscapeUtils.escapeHtml(tempField), FORMAT_COST_CENTS.format(finance.getExpenseByName(tempField))) %></li>
					<%
							}
					%>
				</ul>
			</section>
		</div>
		<!-- totals -->

		<% double balance = incomeTotal - expenseTotal; %>
		<div class="text-right row">
			<section>
				<h6 class="clearfix"><span class="column small-15 medium-20 large-20">Total Income:</span>  <span id="total_income" class="column small-9 large-4 medium-4"><%="&#36; " + FORMAT_COST_CENTS.format(incomeTotal) %></span></h6>
			</section>
			<section>
				<h6 class="clearfix"><span class="column small-15 medium-20 large-20">Total Expenses:</span> <span id="total_expenses" class="column small-9 large-4 medium-4"><%="&#36; " + FORMAT_COST_CENTS.format(expenseTotal) %></span></h6>
			</section>
			<section>
				<h6 class="clearfix"><span class="column small-15 medium-20 large-20">Current Balance:</span> <span id="current_balance" class="column small-9 large-4 medium-4"><%="&#36; " + FORMAT_COST_CENTS.format(balance) %></span></h6>
			</section>
			<%=save_btn%>
		</div>
	</form>
</div>
<script>
	$( document ).ready(function() {
		maskAllFields();
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
