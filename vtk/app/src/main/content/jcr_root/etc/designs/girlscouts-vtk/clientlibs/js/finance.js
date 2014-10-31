

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
	
	
	function saveFinances(){
		
		var starting_balance = document.getElementById("").value;
		var troop_dues = document.getElementById("").value;
		var sponsorship_donations = document.getElementById("").value;
		var product_sales_proceeds = document.getElementById("").value;
		var amea = document.getElementById("").value;
		var bank_interest = document.getElementById("").value;
		var gsusa_registrations = document.getElementById("").value;
		var service_ae = document.getElementById("").value;
		var council_pc = document.getElementById("").value;
		var troop_activities = document.getElementById("").value;
		var troop_supplies = document.getElementById("").value;
		var gs_store_purchase = document.getElementById("").value;
				
		 /*var assetDesc = document.getElementById("assetDesc").value;
		 var custasset = document.getElementById("custasset").value;
		 if( $.trim(custasset)=='' ){alert('Please select file to upload');return false;}
		 if( $.trim(assetDesc)=='' ){alert('Please enter name of asset');return false;}
		 */
		  $.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
				type: 'POST',
				data: { 
					act:'UpdateFinances',
					qtr:qtr,
					starting
					a:Date.now()
				},
				success: function(result) {
					
				}
			});
		
	}