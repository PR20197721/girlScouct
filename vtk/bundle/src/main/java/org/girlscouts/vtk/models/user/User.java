package org.girlscouts.vtk.models.user;

import java.util.Calendar;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.salesforce.Troop;

@Node
public class User {

	public User(){}
	public User(String userId){
		this.id=userId;
		this.path= "/content/girlscouts-vtk/users/"+ userId;
		
	}
	
	//@Collection private java.util.List <YearPlan> yearPlans;
	@Field private String id, refId;
	@Field(path=true) String path;
	@Bean YearPlan yearPlan;
	
	private ApiConfig apiConfig;
	private Troop troop;
	
	@Field private String sfUserId, sfTroopId, sfTroopName;
	
	@Field(jcrName="jcr:lastModified") private Calendar lastModified;
	
	
	public Calendar getLastModified() {
		return lastModified;
	}
	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
		
	}
	public String getSfTroopName() {
		return sfTroopName;
	}
	public void setSfTroopName(String sfTroopName) {
		this.sfTroopName = sfTroopName;
	}
	public String getSfUserId() {
		return sfUserId;
	}
	public void setSfUserId(String sfUserId) {
		this.sfUserId = sfUserId;
	}
	public String getSfTroopId() {
		return sfTroopId;
	}
	public void setSfTroopId(String sfTroopId) {
		this.sfTroopId = sfTroopId;
	}
	public Troop getTroop() {
		return troop;
	}
	public void setTroop(Troop troop) {
		this.troop = troop;
	}
	public ApiConfig getApiConfig() {
		return apiConfig;
	}
	public void setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
	}
	public YearPlan getYearPlan() {
		return yearPlan;
	}
	public void setYearPlan(YearPlan yearPlan) {
		this.yearPlan = yearPlan;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	
	
}
