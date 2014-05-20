package org.girlscouts.vtk.auth.models;

public class User {

    private String name, email;
    
    public void setName(String name){this.name=name;}
    public String getName(){return name;}
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
