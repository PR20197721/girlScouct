package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class JWTAuthEntity {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("instance_url")
    private String instanceUrl;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
}
