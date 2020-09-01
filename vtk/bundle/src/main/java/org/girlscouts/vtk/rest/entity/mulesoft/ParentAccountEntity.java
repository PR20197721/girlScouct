package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class ParentAccountEntity {

    @SerializedName("Name")
    private String name;
    @SerializedName("Id")
    private String id;
    @SerializedName("CouncilCode")
    private String councilCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    @Override
    public String toString() {
        return "ParentAccountEntity{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", councilCode='" + councilCode + '\'' + '}';
    }
}
