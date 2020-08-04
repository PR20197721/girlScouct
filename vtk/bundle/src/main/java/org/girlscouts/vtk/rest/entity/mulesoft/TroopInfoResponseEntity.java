package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroopInfoResponseEntity {

    @SerializedName("troops")
    private List<TroopEntity> troops;

    public List<TroopEntity> getTroops() {
        return troops;
    }

    public void setTroops(List<TroopEntity> troops) {
        this.troops = troops;
    }
}
