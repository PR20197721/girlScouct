package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Map;

@Node
public class Finance implements Serializable {

    public static final String INCOME = "income";
    public static final String EXPENSES = "expenses";
    public static final String PERIOD = "period";
    private static final long serialVersionUID = 8084860336298434137L;
    @Collection
    public Map<String, Double> expenses;
    @Collection
    public Map<String, Double> income;
    @Field(path = true)
    String path;
    @Field(id = true)
    private int financialQuarter;

    public Finance() {
    }

    public void setExpenses(Map<String, Double> expenses) {
        this.expenses = expenses;
    }

    public void setIncome(Map<String, Double> income) {
        this.income = income;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getExpenseByName(String name) {
        if (this.expenses == null) {
            return 0.0;
        } else {
            Double result = this.expenses.get(name);
            if (result == null) {
                return 0.0;

            } else {
                return result.doubleValue();
            }
        }
    }

    public double getIncomeByName(String name) {
        if (this.income == null) {
            return 0.0;
        } else {
            Double result = this.income.get(name);
            if (result == null) {
                return 0.0;

            } else {
                return result.doubleValue();
            }
        }
    }

    public int getFinancialQuarter() {
        return financialQuarter;
    }

    public void setFinancialQuarter(int financialQuarter) {
        this.financialQuarter = financialQuarter;
    }

}
