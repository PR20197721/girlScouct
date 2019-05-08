package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;

@Node
public class LocationNode extends JcrNode implements Serializable, MappableToModel {
    @Field
    private String address;
    @Field
    private String state;
    @Field
    private String city;
    @Field
    private String zip;
    @Field
    private String name;
    @Field
    private String locatinName;
    @Field
    private String locationAddress;

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

    @Override
    public Object toModel() {
        return NodeToModelMapper.INSTANCE.toModel(this);
    }

}
