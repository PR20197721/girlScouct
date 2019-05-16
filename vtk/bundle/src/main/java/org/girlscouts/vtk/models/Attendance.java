package org.girlscouts.vtk.models;

import java.io.Serializable;

public class Attendance extends JcrNode implements Serializable {
    private String users;
    private int total;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
