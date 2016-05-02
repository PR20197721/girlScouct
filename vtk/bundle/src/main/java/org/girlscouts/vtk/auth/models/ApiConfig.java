package org.girlscouts.vtk.auth.models;

import java.io.Serializable;

import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.salesforce.Troop;

public class ApiConfig implements Serializable {

	
	/**
	 * 
	*/
	private static final long serialVersionUID = 7310414085726791761L;
	private String accessToken, instanceUrl, tokenType, id, refreshToken,
			userId, webServicesUrl; /* userId should be moved out.User obj exists* */
	private org.girlscouts.vtk.auth.models.User user;
	private java.util.List<Troop> troops;
	
	//used in refreshToken
	private String callbackUrl, clientId, clientSecret, OAuthUrl;
	private long lastTimeTokenRefreshed;
	
	//from jcr config -APIs
	private String vtkApiTroopUri, vtkApiUserUri, vtkApiContactUri, vtkApiTroopLeadersUri;
	
	//error msg
	java.util.List<VtkError> errors;
	private boolean isFail, accessTokenValid;
	
	
	private boolean demoUser, useAsDemo;
	private String demoUserName;
	
	
	
	
	public String getDemoUserName() {
		return demoUserName;
	}

	public void setDemoUserName(String demoUserName) {
		this.demoUserName = demoUserName;
	}

	public boolean isUseAsDemo() {
		return useAsDemo;
	}

	public void setUseAsDemo(boolean useAsDemo) {
		this.useAsDemo = useAsDemo;
	}

	public boolean isDemoUser() {
		return demoUser;
	}

	public void setDemoUser(boolean demoUser) {
		this.demoUser = demoUser;

	}

	public String getVtkApiTroopLeadersUri() {
		return vtkApiTroopLeadersUri;
	}

	public void setVtkApiTroopLeadersUri(String vtkApiTroopLeadersUri) {
		this.vtkApiTroopLeadersUri = vtkApiTroopLeadersUri;
	}

	public String getVtkApiContactUri() {
		return vtkApiContactUri;
	}

	public void setVtkApiContactUri(String vtkApiContactUri) {
		this.vtkApiContactUri = vtkApiContactUri;
	}

	public String getVtkApiUserUri() {
		return vtkApiUserUri;
	}

	public void setVtkApiUserUri(String vtkApiUserUri) {
		this.vtkApiUserUri = vtkApiUserUri;
	}

	public String getVtkApiTroopUri() {
		return vtkApiTroopUri;
	}

	public void setVtkApiTroopUri(String vtkApiTroopUri) {
		this.vtkApiTroopUri = vtkApiTroopUri;
	}

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
		this.lastTimeTokenRefreshed = new java.util.Date().getTime();
		
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

	public String getWebServicesUrl() {
		return webServicesUrl;
	}

	public void setWebServicesUrl(String webServicesUrl) {
		this.webServicesUrl = webServicesUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getOAuthUrl() {
		return OAuthUrl;
	}

	public void setOAuthUrl(String oAuthUrl) {
		OAuthUrl = oAuthUrl;
	}

	public long getLastTimeTokenRefreshed() {
		return lastTimeTokenRefreshed;
	}

	public void setLastTimeTokenRefreshed(long lastTimeTokenRefreshed) {
		this.lastTimeTokenRefreshed = lastTimeTokenRefreshed;
	}

	public java.util.List<VtkError> getErrors() {
		return errors;
	}

	public void setErrors(java.util.List<VtkError> errors) {
		this.errors = errors;
	}

	public boolean isAccessTokenValid() {
		return accessTokenValid;
	}

	public void setAccessTokenValid(boolean accessTokenValid) {
		this.accessTokenValid = accessTokenValid;
	}
	

}