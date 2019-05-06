package org.girlscouts.vtk.models;

import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

public class Location extends JcrNode implements Serializable {
    private String address, state, city, zip;
    private String name;
    private String locatinName, locationAddress;

    public Location() {
        this.setUid("L" + new java.util.Date().getTime() + "_" + Math.random());
    }

    public Location(String name, String address, String city, String state, String zip) {
        this.setUid("L" + new java.util.Date().getTime() + "_" + Math.random());
        this.name = name;
        this.address = address;
        this.state = state;
        this.zip = zip;
        this.city = city;
    }

    public String getLocatinName() {
        return locatinName;
    }

    public void setLocatinName(String locatinName) {
        if (this.locatinName != null && locatinName != null && !this.locatinName.equals(locatinName)) {
            setDbUpdate(true);
        }
        this.locatinName = locatinName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        if (locationAddress != null && this.locationAddress != null && !locationAddress.equals(this.locationAddress)) {
            setDbUpdate(true);
        }
        this.locationAddress = locationAddress;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address != null && this.address != null && !this.address.equals(address)) {
            setDbUpdate(true);
        }
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state != null && this.state != null && !this.state.equals(state)) {
            setDbUpdate(true);
        }
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (this.city != null && city != null && this.city.equals(city)) {
            setDbUpdate(true);
        }
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        if (this.zip != null && zip != null && !this.zip.equals(zip)) {
            setDbUpdate(true);
        }
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && this.name != null && !this.name.equals(name)) {
            setDbUpdate(true);
        }
        this.name = name;
    }
}
