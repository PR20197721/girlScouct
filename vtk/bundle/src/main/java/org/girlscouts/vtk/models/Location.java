package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Location implements Serializable {

	@Field(path = true) String path;
	@Field private String address, state, city, zip;
	@Field private String name;
	@Field private String locatinName, locationAddress;
	@Field(id = true) private String uid;
	private boolean isDbUpdate=false;
	
	public Location() {
		this.uid = "L" + new java.util.Date().getTime();
	}

	public Location(String name, String address, String city, String state,
			String zip) {

		this.uid = "L" + new java.util.Date().getTime();
		this.name = name;
		this.address = address;
		this.state = state;
		this.zip = zip;
		this.city = city;
		isDbUpdate=true;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		if( this.uid!=null && uid!=null && this.uid.equals(uid)){
			isDbUpdate=true;	
		}
		
		this.uid = uid;
		if (uid == null)
			this.uid = "L" + new java.util.Date().getTime() + "_"
					+ Math.random();
	}

	public String getLocatinName() {
		return locatinName;
	}

	public void setLocatinName(String locatinName) {
		if(this.locatinName!=null && locatinName!=null && !this.locatinName.equals(locatinName))
			isDbUpdate=true;
		this.locatinName = locatinName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		if(locationAddress!=null && this.locationAddress!=null && !locationAddress.equals(this.locationAddress))
			isDbUpdate=true;
		this.locationAddress = locationAddress;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if( this.path!=null && path!=null && this.path.equals(path) )
			isDbUpdate=true;
		this.path = path;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if( address!=null && this.address!=null && !this.address.equals(address))
			isDbUpdate=true;
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		if( state!=null && this.state!=null && !this.state.equals(state))
			isDbUpdate=true;
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		if( this.city!=null && city!=null && this.city.equals(city))
			isDbUpdate=true;
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		if( this.zip!=null && zip!=null && !this.zip.equals(zip))
			isDbUpdate=true;
		this.zip = zip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if( name!=null && this.name!=null && !this.name.equals(name) )
			isDbUpdate=true;
		this.name = name;
	}

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}

}
