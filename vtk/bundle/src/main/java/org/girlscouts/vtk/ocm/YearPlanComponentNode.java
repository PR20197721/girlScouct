package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.io.Serializable;
import java.util.Date;

@Node
public class YearPlanComponentNode extends JcrNode implements Serializable {
    @Field
    private Date date;
    @Field(jcrType = "String", converter = YearPlanComponentTypeOCMPropertyConverter.class)
    private YearPlanComponentType type;
    @Field
    private Integer sortOrder;

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
