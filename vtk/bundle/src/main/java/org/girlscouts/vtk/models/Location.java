package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes="mix:lockable" )
public class Location {

	
		
	
	    @Field(path=true) String path;
		@Field private String address, state, city, zip;
		@Field private String name;
		@Field private String locatinName, locationAddress;
		@Field(id=true) private String uid;
		
		public Location(){this.uid= "L"+new java.util.Date().getTime();}
		public Location( String name, String address, String city, String state, String zip ){
		
			this.uid= "L"+new java.util.Date().getTime();
			this.name= name;
			this.address=address;
			this.state= state;
			this.zip=zip;
			this.city= city;
		}
		
		
		
		
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
			if(uid==null)
				this.uid= "L"+new java.util.Date().getTime()+"_"+ Math.random();
		}
		public String getLocatinName() {
			return locatinName;
		}
		public void setLocatinName(String locatinName) {
			this.locatinName = locatinName;
		}
		public String getLocationAddress() {
			return locationAddress;
		}
		public void setLocationAddress(String locationAddress) {
			this.locationAddress = locationAddress;
		}
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
}
