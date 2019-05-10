package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.Date;

@Node
public class Milestone extends YearPlanComponent implements Serializable {

    private Boolean show;
    private String blurb;
    private Date date;

    public Milestone() {
        this.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        this.setType(YearPlanComponentType.MILESTONE);
    }

    public Milestone(String blurb, boolean show, java.util.Date date) {
        this.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        super.setType(YearPlanComponentType.MILESTONE);
        this.blurb = blurb;
        this.show = new Boolean(show);
        this.date = date;
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

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public void setUid(String uid) {
        if (uid == null) {
            this.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        }else{
            this.setUid(uid);
        }

    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean showInPlans) {
        this.show = showInPlans;
    }

}
