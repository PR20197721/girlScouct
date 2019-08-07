package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class TroopLeadersInfoResponseEntity {
    @SerializedName("troopLeaders")
    private TroopLeaderEntity[] troopLeaders;

    public TroopLeaderEntity[] getTroopLeaders() {
        return troopLeaders;
    }

    public void setTroopLeaders(TroopLeaderEntity[] troopLeaders) {
        this.troopLeaders = troopLeaders;
    }
}
