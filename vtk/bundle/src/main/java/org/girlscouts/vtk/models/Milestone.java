package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;
import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.Date;

@Node
public class Milestone extends YearPlanComponent implements Serializable {
    private Boolean show;
    private String blurb;
    private Date date;

    public Milestone() {
        super.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        this.setType(YearPlanComponentType.MILESTONE);
    }

    public Milestone(String blurb, boolean show, java.util.Date date) {
        super.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        super.setType(YearPlanComponentType.MILESTONE);
        setBlurb(blurb);
        this.show = new Boolean(show);
        this.date = date;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String parBlurb) {
        this.blurb = Jsoup.parse(parBlurb).text();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public void setUid(String uid) {
        if (uid == null) {
            super.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        } else {
            super.setUid(uid);
        }

    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean showInPlans) {
        this.show = showInPlans;
    }

}
