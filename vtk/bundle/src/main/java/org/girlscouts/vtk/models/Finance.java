package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:lockable")
public class Finance implements Serializable {

	private static final long serialVersionUID = 8084860336298434137L;

	@Field(path = true)
	String path;

	@Collection
	public HashMap<String, Double> expenses;
	
	@Collection
	public HashMap<String, Double> income;
	
	public Finance(){
		
	}

	@Field(id = true)
	private int financialQuarter;

	public void setExpenses(HashMap<String, Double> expenses){
		this.expenses = expenses;
	}
	
	public void setIncome(HashMap<String, Double> income){
		this.income = income;
	}
	

	public String getPath() {
		return path;
	}
	
	public double getExpenseByName(String name){
		if(this.expenses == null){
			return 0.0;
		} else{
			Double result = this.expenses.get(name);
			if(result == null){
				return 0.0;
				
			} else{
				return result.doubleValue();
			}
		}
	}
	
	public double getIncomeByName(String name){
		if(this.income == null){
			return 0.0;
		} else{
			Double result = this.income.get(name);
			if(result == null){
				return 0.0;
				
			} else{
				return result.doubleValue();
			}
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFinancialQuarter() {
		return financialQuarter;
	}

	public void setFinancialQuarter(int financialQuarter) {
		this.financialQuarter = financialQuarter;
	}

	

}
