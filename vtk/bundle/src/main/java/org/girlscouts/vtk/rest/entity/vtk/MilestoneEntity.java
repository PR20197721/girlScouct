package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.dao.YearPlanComponentType;

import java.util.Date;

public class MilestoneEntity extends BaseEntity{

    @SerializedName("path")
	private String path;
    @SerializedName("blurb")
	private String blurb;
    @SerializedName("show")
	private Boolean show;
    @SerializedName("date")
	private Date date;
    @SerializedName("uid")
	private String uid;
    @SerializedName("type")
    private YearPlanComponentType type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public YearPlanComponentType getType() {
        return type;
    }

    public void setType(YearPlanComponentType type) {
        this.type = type;
    }
}
