package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Location {

	    @Field(path=true) String path;
		@Field private String address, state, city, zip;
		@Field(id=true) private String name;
		@Field private String locatinName, locationAddress;
		
		public Location(){}
		public Location( String name, String address, String city, String state, String zip ){
		
			this.name= name;
			this.address=address;
			this.state= state;
			this.zip=zip;
			this.city= city;
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
			System.err.println("Setting path: "+ path+" : "+address+" : "+name);
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
