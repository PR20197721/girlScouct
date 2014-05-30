package org.girlscouts.vtk.models.api;

/**
 * @author mike
 *
 */
public interface Location {
    String getName();
    void setName(String name);
    
    String getAddress1();
    void setAddress1(String address1);
    
    String getAddress2();
    void setAddress2(String address2);
    
    String getAddress3();
    void setAddress3(String address3);
    
    String getCity();
    void setCity(String city);
    
    // TODO: enum?
    String getState();
    void setState(String state);
    
    String getZip();
    void setZip(String zip);
}
