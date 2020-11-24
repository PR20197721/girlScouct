package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class RoleEntity {
    @SerializedName("Secondary")
    private String secondary;
    @SerializedName("Primary")
    private String primary;

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "RoleEntity{" + "secondary='" + secondary + '\'' + ", primary='" + primary + '\'' + '}';
    }
}
