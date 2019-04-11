package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class TroopInfoResponseEntity {

    @SerializedName("troops")
    private TroopEntity[] troops;

    public TroopEntity[] getTroops() {
        return troops;
    }

    public void setTroops(TroopEntity[] troops) {
        this.troops = troops;
    }
}
