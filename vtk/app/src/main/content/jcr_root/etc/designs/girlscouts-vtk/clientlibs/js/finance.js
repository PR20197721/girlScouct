

function checkFinances(){
	if (!$('#starting_balance').valid()) {	
			alert("The field starting_balance is invalid.");
	}else if(!$('#troop_dues').valid()){			
			alert("The field troop_dues is invalid.");
	}else if(!$('#sponsorship_donations').valid()){			
			alert("The field sponsorship_donations is invalid.");
	}else if(!$('#product_sales_proceeds').valid()){			
			alert("The field product_sales_proceeds is invalid.");
	}else if(!$('#amea').valid()){			
			alert("The field amea is invalid.");
	}else if(!$('#bank_interest').valid()){			
			alert("The field bank_interest is invalid.");
	}else if(!$('#gsusa_registrations').valid()){			
			alert("The field gsusa_registrations is invalid.");
	}else if(!$('#service_ae').valid()){			
			alert("The field service_ae is invalid.");
	}else if(!$('#council_pc').valid()){			
			alert("The field council_pc is invalid.");
	}else if(!$('#troop_activities').valid()){			
			alert("The field troop_activities is invalid.");
	}else if(!$('#troop_supplies').valid()){			
			alert("The field troop_supplies is invalid.");
	}else if(!$('#gs_store_purchase').valid()){			
			alert("The field gs_store_purchase is invalid.");
	}else {
		alert("about to save finances");
		saveFinances();
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



	$(document).ready(function() {
		$("#financeForm").validate({
			rules : {
				//Current Income
				starting_balance : {
					required : true,
					minlength : 4
				},
				troop_dues: {
					required : true,
					minlength : 4
				},
				sponsorship_donations: {
					required : true,
					minlength : 4
				},
				product_sales_proceeds: {
					required : true,
					minlength : 4
				},
				amea: {
					required : true,
					minlength : 4
				},
				
				bank_interest: {
					required : true,
					minlength : 4
				},
				//Current Expenses
				gsusa_registrations: {
					required : true,
					minlength : 4
				},
				service_ae: {
					required : true,
					minlength : 4
				},
				council_pc: {
					required : true,
					minlength : 4
				},
				troop_activities : {
						required : true,
						minlength : 4,
				},	
				troop_supplies	: {
					required : true,
					minlength : 4
				},
				gs_store_purchase: {
					required : true,
					minlength : 4
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
		
		
		var starting_balance = document.getElementById("starting_balance").value;
		starting_balance = starting_balance.replace(/,/g, '');
		
		var troop_dues = document.getElementById("troop_dues").value;
		troop_dues = troop_dues.replace(/,/g, '');
		
		var sponsorship_donations = document.getElementById("sponsorship_donations").value;
		sponsorship_donations = sponsorship_donations.replace(/,/g, '');
		
		var product_sales_proceeds = document.getElementById("product_sales_proceeds").value;
		product_sales_proceeds = product_sales_proceeds.replace(/,/g, '');
		
		var amea = document.getElementById("amea").value;
		amea = amea.replace(/,/g, '');
		
		var bank_interest = document.getElementById("bank_interest").value;
		bank_interest = bank_interest.replace(/,/g, '');
		
		var gsusa_registrations = document.getElementById("gsusa_registrations").value;
		gsusa_registrations = gsusa_registrations.replace(/,/g, '');
		
		var service_ae = document.getElementById("service_ae").value;
		service_ae = service_ae.replace(/,/g, '');
		
		var council_pc = document.getElementById("council_pc").value;
		council_pc = council_pc.replace(/,/g, '');
		
		var troop_activities = document.getElementById("troop_activities").value;
		troop_activities = troop_activities.replace(/,/g, '');
		
		var troop_supplies = document.getElementById("troop_supplies").value;
		troop_supplies = troop_supplies.replace(/,/g, '');

		var gs_store_purchase = document.getElementById("gs_store_purchase").value;
		gs_store_purchase = gs_store_purchase.replace(/,/g, '');
		
		var qtr = document.getElementById("qtr").value;
		
		  $.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
				type: 'POST',
				data: { 
					act:'UpdateFinances',
					qtr:qtr,
					starting_balance:starting_balance,
					troop_dues:troop_dues,
					sponsorship_donations:sponsorship_donations,
					product_sales_proceeds:product_sales_proceeds,
					amea:amea,
					bank_interest:bank_interest,
					gsusa_registrations:gsusa_registrations,
					service_ae:service_ae,
					council_pc:council_pc,
					troop_activities:troop_activities,
					troop_supplies:troop_supplies,
					gs_store_purchase:gs_store_purchase,
					a:Date.now()
				},
				success: function(result) {
					
				}
			});
		
	}
	
	function updateTotals(){
	
		var starting_balance = document.getElementById("starting_balance").value;
		starting_balance = Number(starting_balance.replace(/,/g, ''));
		starting_balance = starting_balance * 100;
		
		var troop_dues = document.getElementById("troop_dues").value;
		troop_dues = Number(troop_dues.replace(/,/g, ''));
		troop_dues = troop_dues * 100;
		
		var sponsorship_donations = document.getElementById("sponsorship_donations").value;
		sponsorship_donations = Number(sponsorship_donations.replace(/,/g, ''));
		sponsorship_donations = sponsorship_donations * 100;
		
		var product_sales_proceeds = document.getElementById("product_sales_proceeds").value;
		product_sales_proceeds = Number(product_sales_proceeds.replace(/,/g, ''));
		product_sales_proceeds = product_sales_proceeds * 100;
		
		var amea = document.getElementById("amea").value;
		amea = Number(amea.replace(/,/g, ''));
		amea = amea * 100;
		
		var bank_interest = document.getElementById("bank_interest").value;
		bank_interest = Number(bank_interest.replace(/,/g, ''));
		bank_interest = bank_interest * 100;
		
		var gsusa_registrations = document.getElementById("gsusa_registrations").value;
		gsusa_registrations = Number(gsusa_registrations.replace(/,/g, ''));
		gsusa_registrations = gsusa_registrations * 100;
		
		var service_ae = document.getElementById("service_ae").value;
		service_ae = Number(service_ae.replace(/,/g, ''));
		service_ae = service_ae * 100;
		
		var council_pc = document.getElementById("council_pc").value;
		council_pc = Number(council_pc.replace(/,/g, ''));
		council_pc = council_pc * 100;
		
		var troop_activities = document.getElementById("troop_activities").value;
		troop_activities = Number(troop_activities.replace(/,/g, ''));
		troop_activities = troop_activities * 100;
		
		var troop_supplies = document.getElementById("troop_supplies").value;
		troop_supplies = Number(troop_supplies.replace(/,/g, ''));
		troop_supplies = troop_supplies * 100;

		var gs_store_purchase = document.getElementById("gs_store_purchase").value;
		gs_store_purchase = Number(gs_store_purchase.replace(/,/g, ''));
		gs_store_purchase = gs_store_purchase * 100;
		
		var totalIncome = starting_balance + troop_dues + sponsorship_donations + product_sales_proceeds + amea + bank_interest;
		totalIncome = totalIncome / 100;
		var totalExpenses = gsusa_registrations + service_ae + council_pc + troop_activities + troop_supplies + gs_store_purchase;
		totalIncome = totalIncome / 100;
		var currentBalance = totalIncome - totalExpenses;
		totalIncome = totalIncome / 100;
		
		document.getElementById("total_income").innerHTML = "$" + totalIncome;
		document.getElementById("total_expenses").innerHTML = "$" + totalExpenses;
		document.getElementById("current_balance").innerHTML = "$" + currentBalance;
		
	
	
	}