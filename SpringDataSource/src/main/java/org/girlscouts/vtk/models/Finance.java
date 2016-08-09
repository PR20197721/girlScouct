package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="Finance")
public class Finance implements Serializable {
	
	public static final String INCOME = "income";
	public static final String EXPENSES = "expenses";
	public static final String PERIOD = "period";
	private static final long serialVersionUID = 8084860336298434137L;

	@Transient
	String path;

	//@OneToMany
	@Transient
	public Map<String, Double> expenses;
	
	//@OneToMany
	@Transient
	public Map<String, Double> income;
	
	public Finance(){
	}

	@Column @Id
	private int financialQuarter;

	public void setExpenses(Map<String, Double> expenses){
		this.expenses = expenses;
	}
	
	public void setIncome(Map<String, Double> income){
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
