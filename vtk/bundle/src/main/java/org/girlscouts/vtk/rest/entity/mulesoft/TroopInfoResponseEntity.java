package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroopInfoResponseEntity {

    @SerializedName("troops")
    private List<TroopWrapperEntity> troops;

    public List<TroopWrapperEntity> getTroops() {
        return troops;
    }

    public void setTroops(List<TroopWrapperEntity> troops) {
        this.troops = troops;
    }

    @Override
    public String toString() {
        return "TroopInfoResponseEntity{" + "troops=" + troops + '}';
    }
}
