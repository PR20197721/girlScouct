package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class TroopWrapperEntity {

    @SerializedName("Troop")
    private TroopEntity troop;

    public TroopEntity getTroop() {
        return troop;
    }

    public void setTroop(TroopEntity troop) {
        this.troop = troop;
    }

    @Override
    public String toString() {
        return "_TroopEntity{" + "troop=" + troop + '}';
    }
}
