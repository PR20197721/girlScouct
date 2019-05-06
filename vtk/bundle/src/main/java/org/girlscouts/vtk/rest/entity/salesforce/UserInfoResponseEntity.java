package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponseEntity {
    @SerializedName("users")
    private UserEntity[] users;
    @SerializedName("camps")
    private ParentEntity[] camps;

    public UserEntity[] getUsers() {
        return users;
    }

    public void setUsers(UserEntity[] users) {
        this.users = users;
    }

    public ParentEntity[] getCamps() {
        return camps;
    }

    public void setCamps(ParentEntity[] camps) {
        this.camps = camps;
    }
}
