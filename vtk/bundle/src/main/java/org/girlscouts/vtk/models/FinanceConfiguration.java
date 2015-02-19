package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;


public class FinanceConfiguration implements Serializable{

	public static final String FINANCE_CONFIG = "/finance_configuration";
	private static final long serialVersionUID = 6064391709671475214L;

	
	

	private List<String> incomeFields;
	
	private List<String> expenseFields;

	public List<String> getIncomeFields() {
		if(this.incomeFields == null){
			List<String> income = new ArrayList<String>();
			income.add("Beginning Balance");
			income.add("Troop Dues");
			income.add("Sponsorship/Donations");
			income.add("Product Sales Proceeds");
			income.add("Approved Money-Earnings Activities");
			income.add("Interest on Bank Accounts");
			return income;
		} else{
			return this.incomeFields;
		}
		
	}

	public void setIncomeFields(List<String> incomeFields) {
		this.incomeFields = incomeFields;
	}

	public List<String> getExpenseFields() {
		if(this.expenseFields == null){

			List<String> expenses = new ArrayList<String>();
			expenses.add("GSUSA Registrations");
			expenses.add("Service Activities/Events");
			expenses.add("Council Programs/Camp");
			expenses.add("Troop Activities");
			expenses.add("Troop Supplies");
			expenses.add("GS Store Purchase");
			return expenses;
		}else{
			return expenseFields;
		}
	}

	public void setExpenseFields(List<String> expenseFields) {
		this.expenseFields = expenseFields;
	}


	
}
