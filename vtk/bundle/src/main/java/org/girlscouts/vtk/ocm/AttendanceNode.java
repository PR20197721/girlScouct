package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

@Node
public class AttendanceNode extends JcrNode implements Serializable {
    @Field
    private String users; // sf id
    @Field
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
