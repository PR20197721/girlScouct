package org.girlscouts.vtk.auth.models;

import java.io.Serializable;

import org.girlscouts.vtk.salesforce.Troop;

public class ApiConfig  implements Serializable{

    private String accessToken, instanceUrl, tokenType, id,
    		refreshToken,
    		userId; /*userId should be moved out.User obj exists**/
    private org.girlscouts.vtk.auth.models.User user;
    private java.util.List<Troop> troops;
    
    
    
    public java.util.List<Troop> getTroops() {
		return troops;
	}

	public void setTroops(java.util.List<Troop> troops) {
		this.troops = troops;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public org.girlscouts.vtk.auth.models.User getUser() {
		return user;
	}

	public void setUser(org.girlscouts.vtk.auth.models.User user) {
		this.user = user;
	}

	public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}