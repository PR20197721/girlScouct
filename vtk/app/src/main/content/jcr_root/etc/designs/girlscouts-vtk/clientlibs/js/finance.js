

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
		alert("started save");
		alert("first value is: " + document.getElementById("income1").value);

		
		var qtr = document.getElementById("qtr").value;
		alert("Quarter is: " + document.getElementById("qtr").value);
		var exp = "[";
		var i = 1;
		var inc = "[";
		do{
			var tempElement = $('#income' + i);
			if(tempElement.length > 0){
				if(i != 1){
					inc = inc + ", ";
				}
				inc = inc + tempElement.attr('name') + ", " + tempElement.val();
			}
			i++;
		}while(tempElement.length > 0);
		
		i = 1;
		do{
			var tempElement = $('#expense' + i);
			
			if(tempElement.length > 0){
				if(i != 1){
					exp = exp + ", ";
				}
				exp = exp + tempElement.attr('name') + ", " + tempElement.val();
				
			}
			i++;
		}while(tempElement.length > 0);
		
		inc = inc + "]";
		exp = exp + "]";
		
		alert("Income: " + inc);
		alert("Expenses: " + exp);
		
		
		  $.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
				type: 'POST',
				data: { 
					act:'UpdateFinances',
					qtr:qtr,
					expenses: exp,
					income: inc,
					a:Date.now()
				},
				success: function(result) {
					
				}
			});
			
			return false;
		
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