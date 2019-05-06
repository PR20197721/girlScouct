package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Map;

@Node
public class Finance extends JcrNode implements Serializable {
    @Collection
    public Map<String, Double> expenses;
    @Collection
    public Map<String, Double> income;
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
