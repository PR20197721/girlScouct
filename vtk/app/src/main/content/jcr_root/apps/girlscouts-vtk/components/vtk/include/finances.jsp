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


<script>



function checkFinances(){
	if ($('#financeForm').valid()) {	
				;
		}else {			
			alert("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
		}
}

$(function() {		
		$("#starting_balance").maskMoney();
		$("#troop_dues").maskMoney();
		$("#sponsorship_donations").maskMoney();
		$("#product_sales_proceeds").maskMoney();
		$("#amea").maskMoney();
		$("#bank_interest").maskMoney();
		$("#gsusa_registrations").maskMoney();
		$("#service_ae").maskMoney();
		$("#council_pc").maskMoney();
		$("#troop_activities").maskMoney();
		$("#troop_supplies").maskMoney();
		$("#gs_store_purchase").maskMoney();	
});


$.validator.addMethod('currency', function(value, element, regexp) {
		var re = /^\d{1,9}(\.\d{1,2})?$/;
		return this.optional(element) || re.test(value);
		}, '');



	$().ready(function() {
		$("#financeForm").validate({
			rules : {
				troop_supplies	: {
					required : true,
					minlength : 4,
					currency : true
				},
				gs_store_purchase: {
					required : true,
					minlength : 4,
					currency : true
				},
				
				troop_dues: {
					required : true,
					minlength : 4,
					currency : true
				},
				sponsorship_donations: {
					required : true,
					minlength : 4,
					currency : true
				},
				product_sales_proceeds: {
					required : true,
					minlength : 4,
					currency : true
				},
				amea: {
					required : true,
					minlength : 4,
					currency : true
				},
				bank_interest: {
					required : true,
					minlength : 4,
					currency : true
				},
				gsusa_registrations: {
					required : true,
					minlength : 4,
					currency : true
				},
				service_ae: {
					required : true,
					minlength : 4,
					currency : true
				},
				council_pc: {
					required : true,
					minlength : 4,
					currency : true
				},
				troop_activities : {
						required : true,
						minlength : 4,
						currency : true
					},
				starting_balance : {
					required : true,
					minlength : 4,
					currency : true
				}
			},

			messages : {
				troop_dues : {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				sponsorship_donations: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				product_sales_proceeds: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				amea: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				bank_interest: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				gsusa_registrations: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				gsusa_registrations: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				service_ae: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				council_pc: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				troop_activities: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				troop_supplies: {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				},
				starting_balance : {
					required : "Please enter a valid amount. Default 0.00",
					minlength : "Valid format 0.00"
				}

			}
		});

	});
</script> 




<h3>QXXX 2014</h3>
<form class="cmxform" id="financeForm">
<div class="errorMsg error"></div>
<div class="row">

<!-- current income -->
  <div class="small-24 large-12 columns">
  	<div class="row">
	  <div class="small-24 large-12 columns">Starting Balance:</div>
 	  <div class="small-24 large-12 columns"><input type="text" name="starting_balance" id="starting_balance" value="asdf"/>
 	  </div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Dues:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_dues" name="troop_dues" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Sponsorship/Donations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="sponsorship_donations" name="sponsorship_donations" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Product Sales Proceeds:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="product_sales_proceeds" name="product_sales_proceeds" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Approved Money-Earnings Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="amea" name="amea" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Interest on Bank Accounts:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="bank_interest" name="bank_interest" value=""/></div>
	</div>
  </div>
  
  <!-- current exp -->
  <div class="small-24 large-12 columns">
    <div class="row">
	  <div class="small-24 large-12 columns">GSUSA Registrations:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="gsusa_registrations" name="gsusa_registrations" value=""/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Service Activities/Events:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="service_ae" name="service_ae" value=""/></div>
	</div>
	 <div class="row">
	  <div class="small-24 large-12 columns">Council Programs/Camp:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="council_pc" name="council_pc" value=""/></div>
	</div>
	<div class="row">
	  <div class="small-24 large-12 columns">Troop Activities:</div>
 	  <div class="small-24 large-12 columns"><input type="text" id="troop_activities" name="troop_activities" value=""/></div>
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