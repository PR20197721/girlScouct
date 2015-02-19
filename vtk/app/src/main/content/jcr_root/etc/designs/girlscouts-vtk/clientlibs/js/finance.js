function checkFinances() {
		// alert("about to save finances");
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


function saveFinanceAdmin(){

	console.log("Going through the lists");
    var incomeArray = "[";
    var expenseArray = "["
    
	var incomeChildren = document.getElementById("income-list").children;
    var addComma = false;
	for(var i = 0; i < incomeChildren.length; i++){
		var tempChild = incomeChildren[i].firstElementChild;
		
		if(tempChild.tagName == "INPUT"){
			if(addComma){
				incomeArray = incomeArray + ", ";
			}
			incomeArray = incomeArray + tempChild.value;
            addComma = true;
		}
	}
	addComma = false;
	var expenseChildren = document.getElementById("expense-list").children;
	for(var i = 0; i < expenseChildren.length; i++){
		var tempChild = expenseChildren[i].firstElementChild;
        
		if(tempChild.tagName == "INPUT"){
			if(addComma){
				expenseArray = expenseArray + ", ";
			}
			expenseArray = expenseArray + tempChild.value;
            addComma = true;
		}
	}
    incomeArray = incomeArray + "]";
    expenseArray = expenseArray + "]";
	console.log("Income array is: " + incomeArray);
	console.log("Expense array is: " + expenseArray);

    $.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
				type: 'POST',
				data: { 
					act:'UpdateFinanceAdmin',
					expenses: expenseArray,
					income: incomeArray,
				},
				success: function(result) {
					
				}
			});


	return false;
}
	
	
	
function saveFinances(){
		// alert("started save");
		// alert("first value is: " + document.getElementById("income1").value);

		
		var qtr = document.getElementById("qtr").value;
		// alert("Quarter is: " + document.getElementById("qtr").value);
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
		
		// alert("Income: " + inc);
		// alert("Expenses: " + exp);
		
		
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
	
function deleteIncomeRow(counter){ 
	var button = document.getElementById("incomeButton" + counter);
	var input = document.getElementById("incomeField" + counter);
	input.parentNode.removeChild(input);
	button.parentNode.removeChild(button);
    return false;
	
}

function deleteExpenseRow(counter){ 
	var button = document.getElementById("expenseButton" + counter);
	var input = document.getElementById("expenseField" + counter);
	input.parentNode.removeChild(input);
	button.parentNode.removeChild(button);
    return false;

}

function addExpenseField(){
	return addFinanceRow("expense-list", "expenseCount", "expenseButton", "expenseField", "deleteExpenseRow");
}

function addIncomeField(){
	return addFinanceRow("income-list", "incomeCount", "incomeButton", "incomeField", "deleteIncomeRow");
}

function addFinanceRow(listId, countId, buttonId, inputId, delMethod){
	var fieldsList = document.getElementById(listId);
	var countHolder = document.getElementById(countId);
	var count = countHolder.value;
	
	//Create button list element
	var newButtonHolder = document.createElement("LI");
	newButtonHolder.setAttribute("id", buttonId + count);
	
	var newButtonLink = document.createElement("A");
	newButtonLink.setAttribute("href", "");
	newButtonLink.setAttribute("onclick", "return " + delMethod + "(" + count + ")");
	
	var newButtonIcon = document.createElement("I");
	newButtonIcon.setAttribute("class", "icon-button-circle-cross");
	
	newButtonLink.appendChild(newButtonIcon);
	newButtonHolder.appendChild(newButtonLink);
	
	//Create input list element
	var newInputHolder = document.createElement("LI");
	newInputHolder.setAttribute("id", inputId + count);
	
	var newInput = document.createElement("INPUT");
	newInput.setAttribute("type", "text");
	newInput.setAttribute("value", "");
	
	newInputHolder.appendChild(newInput);
	
	fieldsList.appendChild(newInputHolder);
	fieldsList.appendChild(newButtonHolder);
	
	countHolder.value = (Number(count) + 1) + "";
	
	return false;
}