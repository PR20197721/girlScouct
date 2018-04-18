<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, org.apache.commons.lang3.StringEscapeUtils" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
  String sectionClassDefinition = "finances";
String activeTab = "finances";
%>
<%@include file="include/bodyTop.jsp" %>
<% if(!VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_FORM_ID) ){
	%>
	  <div class="columns large-20 large-centered">
                <p>
                Sorry! You currently don't have permission to view this tab. For questions, click Contact Us at the top of the page.
                </p>
      </div>
	 </div> <!-- end panelWrapper -->
	 <script>loadNav('financesadmin')</script>
	<%
	return;
}%>

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
	//String activeTab = "finances";
	pageContext.setAttribute("activeSubTab", "editFinances");
	boolean showVtkNav = true;

	FinanceConfiguration financeConfig = financeUtil.getFinanceConfig(user, troop, user.getCurrentYear());

	List<String> incomeFields = financeConfig.getIncomeFields();
	List<String> expenseFields = financeConfig.getExpenseFields();

	String period = financeConfig.getPeriod();

	String recipient = financeConfig.getRecipient();
	if(recipient == null){
		recipient = "";
	}

	int incomeCounter = 0;
	int expenseCounter = 0;

	boolean hasAdminPermissions = true;
	String financeFieldTag = "";
%>

	<div class="column large-20 medium-20 large-centered medium-centered small-24">
		<form class="cmxform" id="financeAdminForm" onchange="enableSaveButton()">
			<p id="error-message" class="error-message"></p>
			<div class="row options">
			 <section class="column large-12 medium-12">
				 <span>Reporting Frequency:</span>
				 <select id="periodSelection" name="periodSelection">
					 <%if("Yearly".equals(period)){%>
					 	<option value="Yearly">Yearly</option>
					 	<option value="Quarterly">Quarterly</option>
					 <%} else{%>
					 	<option value="Quarterly">Quarterly</option>
					 	<option value="Yearly">Yearly</option>
					 <%} %>
				 </select>
			 </section>
			 <section class="column large-12 medium-12">
				 <div id="incomeFields">
				 	<span>Send To:</span><input name="recipient" id="recipient" type="text" onkeyDown="enableSaveButton()" placeholder="Enter email of the recipient" value="<%=recipient%>"/>
				 </div>
			 </section>
			</div>

			<div class="row">
				<section class="column large-12 medium-12">
					<h6>income categories</h6>
					<ul id="income-list" class="large-block-grid-2 small-block-grid-2 text-left">
						<%
							for(String incomeField : incomeFields){
						%>
						<li id="incomeField<%=incomeCounter%>"><input name="incomeValue<%=incomeCounter%>" onkeyDown="enableSaveButton()" class="financeAdminField" oninput="enableSaveButton()" onpaste="enableSaveButton()"  maxlength="35" type="text" value="<%=StringEscapeUtils.escapeHtml(incomeField)%>"/></li>
						<li id="incomeButton<%=incomeCounter%>"><a href="" title="remove" onclick="return deleteIncomeRow(<%=incomeCounter%>)"><i class="icon-button-circle-cross"></i></a></li>
						<%
								incomeCounter++;
							}
						%>
					</ul>
					<a class="add-btn" title="add" onclick="return addIncomeField()"><i class="icon-button-circle-plus"></i>Add a  Finance Field</a>
				</section>
				<section class="column large-12 medium-12">
					<h6>expense categories</h6>
					<ul id="expense-list" class="large-block-grid-2 small-block-grid-2 text-left">
						<%
							for(String expenseField : expenseFields){
						%>
						<li id="expenseField<%=expenseCounter%>"><input onkeyDown="enableSaveButton()" name="expenseValue<%=expenseCounter%>" oninput="enableSaveButton()" class="financeAdminField" onpaste="enableSaveButton()" type="text" maxlength="35" value="<%=StringEscapeUtils.escapeHtml(expenseField)%>"/></li>
						<li id="expenseButton<%=expenseCounter%>"><a href="" title="remove" onclick="return deleteExpenseRow(<%=expenseCounter%>)"><i class="icon-button-circle-cross"></i></a></li>
						<%
								expenseCounter++;
							}
						%>
					</ul>
					<a class="add-btn" title="add" onclick="return addExpenseField()"><i class="icon-button-circle-plus"></i>Add a  Finance Field</a>
				</section>
			</div><!--/row-->
			<!-- totals -->
			<div class="text-right row collapse">
				<a id="saveFinanceFieldFormButton" role="button" aria-label="submit form" onclick="return saveFinanceAdmin()" class="button save disabled">Save</a>
			</div>
			<input type="hidden" id="incomeCount" value="<%=incomeCounter %>"/>
			<input type="hidden" id="expenseCount" value="<%=expenseCounter %>"/>
		</form>
	</div>
<%@include file="include/bodyBottom.jsp" %>
<script type="text/javascript">
	$(document).ready( function(){

		jQuery.validator.addClassRules("financeAdminField", {
			required: true
		});

		$("#financeAdminForm").validate({
			rules: {
				recipient: {
					required: true,
					email: true
				}
			},
      		showErrors: function(errorMap, errorList) {
      			var errors = this.numberOfInvalids();
				if (errors) {
					$(".error-message").html("<i class=\"icon-notice-info-announcement\"></i>There were errors while submitting the form");

					$('#error-message')[0].scrollIntoView( true );
				} else {
					$(".error-message").html("");
				}
				this.defaultShowErrors();
      		}

		});

	});
</script>
<script>loadNav('financesadmin')</script>