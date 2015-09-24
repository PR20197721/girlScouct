function checkFinances() {
	saveFinances();
}

function maskAllFields() {
	var i = 1;
	var incomeChildren = document.getElementById("incomeFields").children;
	var expenseChildren = document.getElementById("expenseFields").children;
	console.log("Started Masking Fields");
	for(var i = 0; i < incomeChildren.length || i < expenseChildren.length ; i++){
		if(i < incomeChildren.length){
			
			var incChild = incomeChildren[i].firstElementChild;
			if(incChild.tagName == "INPUT"){
			console.log("Masked income child" + i);
				$(incChild).maskMoney({allowZero: true, prefix: '$'});
			}
		}
		if(i < expenseChildren.length){
			var expChild = expenseChildren[i].firstElementChild;
			if(expChild.tagName == "INPUT"){
			console.log("Masked expense child" + i);
				$(expChild).maskMoney({allowZero: true, prefix: '$'});
				
			}
		}		
	}
}


function validateFinanceAdmin(){
	return $("#financeAdminForm").valid();
	
}

function saveFinanceAdmin(){
	var recipient = document.getElementById("recipient").value;
	
	if(!validateFinanceAdmin()){
		return false;
	}

	var incomeArray = "[";
	var expenseArray = "["
	
	var periodValue = document.getElementById("periodSelection").value;

	var incomeChildren = document.getElementById("income-list").children;
	var addComma = false;
	for(var i = 0; i < incomeChildren.length; i++){
		var tempChild = incomeChildren[i].firstElementChild;
		
		if(tempChild.tagName == "INPUT"){
			if(addComma){
				incomeArray = incomeArray + ",";
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
				expenseArray = expenseArray + ",";
			}
			expenseArray = expenseArray + tempChild.value;
			addComma = true;
		}
	}
	incomeArray = incomeArray + "]";
	expenseArray = expenseArray + "]";
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'UpdateFinanceAdmin',
			expenses: expenseArray,
			income: incomeArray,
			period: periodValue,
			recipient: recipient 
			
		},
		success: function(result) {
			$("#saveFinanceFieldFormButton").addClass("disabled");
			$(".error-message").html("<i class=\"icon-notice-info-announcement\"></i>Your changes saved.");
		}
	});
	vtkTrackerPushAction('ModifyFinanceForm');
	return false;
}

function saveFinances(){
	var qtr = document.getElementById("qtr").value;
	
	var incomeArray = "[";
	var expenseArray = "["

	var incomeChildren = document.getElementById("incomeFields").children;
	var addComma = false;
	for(var i = 0; i < incomeChildren.length; i++){
		var tempChild = incomeChildren[i].firstElementChild;
		
		if(tempChild.tagName == "INPUT"){
			if(addComma){
				incomeArray = incomeArray + ",";
			}
			incomeArray = incomeArray + tempChild.getAttribute("name") + "," + tempChild.value.replace(/\$/g, '').replace(/,/g, '');
			addComma = true;
		}
	}
	addComma = false;
	var expenseChildren = document.getElementById("expenseFields").children;
	for(var i = 0; i < expenseChildren.length; i++){
		var tempChild = expenseChildren[i].firstElementChild;
        
		if(tempChild.tagName == "INPUT"){
			if(addComma){
				expenseArray = expenseArray + ",";
			}
			expenseArray = expenseArray + tempChild.getAttribute("name") + "," + tempChild.value.replace(/\$/g, '').replace(/,/g, '');
			addComma = true;
		}
	}
	incomeArray = incomeArray + "]";
	expenseArray = expenseArray + "]";
	
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'UpdateFinances',
			qtr:qtr,
			expenses: expenseArray,
			income: incomeArray,
			a:Date.now()
		},
		success: function(result) {
			
			$("#saveFinanceFieldFormButton").addClass("disabled");
			$(".error-message").html("<i class=\"icon-notice-info-announcement\"></i>Your changes saved.");
		}
	});
	vtkTrackerPushAction('ModifyFinanceData');
	return false;
}
	
function updateTotals(){
	var totalIncome = 0;
	var totalExpenses = 0;
	var i = 1;
	do{
		var tempElement = $("#income" + i);
		if(tempElement != null){
			var tempVal = null;
			if (tempElement.val()) {
				tempVal = tempElement.val();
			} else {
				tempVal = tempElement.text();
			}
			tempVal = tempVal.replace(/\$/g, '').replace(/,/g, '');
			tempVal = Number(tempVal);
			tempVal = tempVal * 100;
			
			totalIncome += tempVal;
		}
		i++;
	}while(tempElement.length > 0);
	i = 1;
	do{
		var tempElement = $("#expense" + i);
		if(tempElement != null){
			var tempVal = null;
			if (tempElement.val()) {
				tempVal = tempElement.val();
			} else {
				tempVal = tempElement.text();
			}
			tempVal = tempVal.replace(/\$/g, '').replace(/,/g, '');
			tempVal = Number(tempVal);
			tempVal = tempVal * 100;
			
			totalExpenses += tempVal;
		}
		i++;
	}while(tempElement.length > 0);
	
	
	
	var currentBalance = totalIncome - totalExpenses;
	
	totalIncome = Math.round(totalIncome);
	totalExpenses = Math.round(totalExpenses);
	currentBalance = Math.round(currentBalance);
	
	totalIncome = totalIncome / 100;
	totalExpenses = totalExpenses / 100;
	currentBalance = currentBalance / 100;
	
	totalIncome =  totalIncome.toFixed(2);
	totalExpenses =  totalExpenses.toFixed(2);
	currentBalance =  currentBalance.toFixed(2);
	
	totalIncome = numberWithCommas(totalIncome);
	totalExpenses = numberWithCommas(totalExpenses);
	currentBalance = numberWithCommas(currentBalance);
	
	$("#total_income").text("\$ " + totalIncome);
	$("#total_expenses").text("\$ " + totalExpenses);
	$("#current_balance").text("\$ " + currentBalance);
}

function incomeAtMinimum(){
	if(document.getElementById("income-list").children.length <= 2){
		return true
	} else{
		return false;
	}

}

function expensesAtMinimum(){
	if(document.getElementById("expense-list").children.length <= 2){
		return true
	} else{
		return false;
	}

}
	
function deleteIncomeRow(counter){ 
	
	if(!validateFinanceAdmin()){
		return false;
	}
	
	 if (!confirm("Warning: Are you sure you want to remove this category? Doing so will remove all data association with this category")) {
		 return false;
	 }
	
	if(incomeAtMinimum()){
		return false;
	}
	var button = document.getElementById("incomeButton" + counter);
	var input = document.getElementById("incomeField" + counter);
	input.parentNode.removeChild(input);
	button.parentNode.removeChild(button);
	
	saveFinanceAdmin();
	
	return false;
}

function deleteExpenseRow(counter){ 
	
	if(!validateFinanceAdmin()){
		return false;
	}
	
	if(expensesAtMinimum()){
		return false;
	}
	var button = document.getElementById("expenseButton" + counter);
	var input = document.getElementById("expenseField" + counter);
	input.parentNode.removeChild(input);
	button.parentNode.removeChild(button);
	saveFinanceAdmin();
	return false;
}

function addExpenseField(){
	
	if(!validateFinanceAdmin()){
		return false;
	}
	
	if(isBlankField()){alert("Income and Expense Category Fields must contain text, they cannot be blank."); return false; }
	
	
	vtkTrackerPushAction('EditFinancialForm');
	return addFinanceRow("expense-list", "expenseCount", "expenseButton", "expenseField", "deleteExpenseRow");
}

function addIncomeField(){
	if(!validateFinanceAdmin()){
		return false;
	}
	
	if(isBlankField()){alert("Income and Expense Category Fields must contain text, they cannot be blank."); return false; }
	
	vtkTrackerPushAction('EditFinancialForm');
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
	newInput.setAttribute("maxlength", "30");
	newInput.setAttribute("onkeyDown", "enableSaveButton()");
	
	newInputHolder.appendChild(newInput);
	
	fieldsList.appendChild(newInputHolder);
	fieldsList.appendChild(newButtonHolder);
	
	countHolder.value = (Number(count) + 1) + "";
	
	return false;
}

function enableSaveButton() {
	$("#saveFinanceFieldFormButton").removeClass("disabled");
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function isBlankField(){
	var incomeChildren = document.getElementById("income-list").children;
	for(var i = 0; i < incomeChildren.length; i++){
		var tempChild = incomeChildren[i].firstElementChild;
		if(   tempChild.value =='') {return true;}
	}
	
	var expenseChildren = document.getElementById("expense-list").children;
	for(var i = 0; i < expenseChildren.length; i++){
		var tempChild = expenseChildren[i].firstElementChild;
		if(  tempChild.value =='') {return true;}
	}
	
	return false;
}