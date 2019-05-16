package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Map;

@Node
public class FinanceNode extends JcrNode implements Serializable {
    @Collection
    public Map<String, Double> expenses;
    @Collection
    public Map<String, Double> income;
    @Field(id = true)
    private int financialQuarter;

    public Map<String, Double> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<String, Double> expenses) {
        this.expenses = expenses;
    }

    public Map<String, Double> getIncome() {
        return income;
    }

    public void setIncome(Map<String, Double> income) {
        this.income = income;
    }

    public int getFinancialQuarter() {
        return financialQuarter;
    }

    public void setFinancialQuarter(int financialQuarter) {
        this.financialQuarter = financialQuarter;
    }
}
