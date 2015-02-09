

function checkFinances(){
	
		alert("about to save finances");
		saveFinances();
	
	
}

$(function() {

		var i = 1;
		do{
			var tempElement = $('#income' + i);
			if(tempElement.length > 0){
				
				tempElement.maskMoney();
			}
			i++;
		}while(tempElement.length > 0);
		
		i = 1;
		do{
			var tempElement = $('#expense' + i);
			if(tempElement.length > 0){
				tempElement.maskMoney();
				
			}
			i++;
		}while(tempElement.length > 0);
		
});



	
	
function saveFinances(){
		
		
	/*	
		
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
			}); */
		
	}
	
function updateTotals(){


		var totalIncome = 0;
		var totalExpenses = 0;

		var i = 1;
		do{
			var tempElement = document.getElementById("income" + i);
			if(tempElement != null){
				var tempVal = tempElement.value;
				tempVal = Number(tempVal.replace(/,/g, ''));
				tempVal = tempVal * 100;
				
				totalIncome += tempVal;
			}
			i++;
		}while(tempElement != null);
		
		i = 1;
		
		do{
			var tempElement = document.getElementById("expense" + i);
			if(tempElement != null){
				var tempVal = tempElement.value;
				tempVal = Number(tempVal.replace(/,/g, ''));
				tempVal = tempVal * 100;
				
				totalExpenses += tempVal;
			}
			i++;
		}while(tempElement != null);
	
		
		
		
		var currentBalance = totalIncome - totalExpenses;
		
		totalIncome = totalIncome / 100;
		totalExpenses = totalExpenses / 100;
		currentBalance = currentBalance / 100;
		
		
		
		
		document.getElementById("total_income").innerHTML = "$" + totalIncome;
		document.getElementById("total_expenses").innerHTML = "$" + totalExpenses;
		document.getElementById("current_balance").innerHTML = "$" + currentBalance;
		
	
	
	}