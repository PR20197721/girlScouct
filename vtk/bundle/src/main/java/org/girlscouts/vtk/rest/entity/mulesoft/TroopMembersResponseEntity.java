package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroopMembersResponseEntity {

    @SerializedName("Troop")
    private TroopEntity troop;

    @SerializedName("Members")
    private List<MembersEntity> members;

    public TroopEntity getTroop() {
        return troop;
    }

    public void setTroop(TroopEntity troop) {
        this.troop = troop;
    }

    public List<MembersEntity> getMembers() {
        return members;
    }

    public void setMembers(List<MembersEntity> members) {
        this.members = members;
    }
}
