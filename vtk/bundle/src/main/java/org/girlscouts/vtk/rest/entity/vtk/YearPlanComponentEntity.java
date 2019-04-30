package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.dao.YearPlanComponentType;

import java.util.Date;

public class YearPlanComponentEntity extends BaseEntity {

    @SerializedName("date")
    private Date date;
    @SerializedName("type")
    private YearPlanComponentType type;
    @SerializedName("uid")
    private String uid;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
