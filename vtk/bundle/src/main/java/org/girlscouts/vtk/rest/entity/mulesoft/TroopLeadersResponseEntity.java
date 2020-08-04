package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroopLeadersResponseEntity {

    @SerializedName("troopLeaders")
    private List<DPInfoEntity> troopLeaders;

    public List<DPInfoEntity> getTroopLeaders() {
        return troopLeaders;
    }

    public void setTroopLeaders(List<DPInfoEntity> troopLeaders) {
        this.troopLeaders = troopLeaders;
    }
}
