package org.girlscouts.vtk.models;

import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.Date;

public class YearPlanComponent extends JcrNode implements Serializable {
    private Date date;
    private YearPlanComponentType type;
    private Integer sortOrder;

    public YearPlanComponent(){

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public YearPlanComponentType getType() {
        return type;
    }

    public void setType(YearPlanComponentType type) {
        this.type = type;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}
