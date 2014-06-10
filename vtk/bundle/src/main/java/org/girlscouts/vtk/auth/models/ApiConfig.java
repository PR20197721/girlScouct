package org.girlscouts.vtk.auth.models;

public class ApiConfig {

    private String accessToken, instanceUrl, tokenType, id,
    		refreshToken,
    		userId; /*userId should be moved out.User obj exists**/
    private org.girlscouts.vtk.auth.models.User user;
    
    
    
    
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