package org.girlscouts.vtk.models;

import java.io.Serializable;
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
		return incomeFields;
	}

	public void setIncomeFields(List<String> incomeFields) {
		this.incomeFields = incomeFields;
	}

	public List<String> getExpenseFields() {
		return expenseFields;
	}

	public void setExpenseFields(List<String> expenseFields) {
		this.expenseFields = expenseFields;
	}


	
}
