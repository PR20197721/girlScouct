package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.YearPlanComponent;

import java.io.Serializable;
import java.util.Date;

@Node
public class MilestoneNode extends JcrNode implements Serializable {
    @Field
    private Boolean show;
    @Field
    private String blurb;
    @Field
    private Date date;

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
