package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class LocationEntity extends BaseEntity {
    @SerializedName("path")
    private String path;
    @SerializedName("address")
    private String address;
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("zip")
    private String zip;
    @SerializedName("name")
    private String name;
    @SerializedName("locatinName")
    private String locatinName;
    @SerializedName("locationAddress")
    private String locationAddress;
    @SerializedName("uid")
    private String uid;
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }
}
