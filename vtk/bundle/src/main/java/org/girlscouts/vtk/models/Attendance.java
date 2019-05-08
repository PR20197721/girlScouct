package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.ocm.AttendanceNode;

import java.io.Serializable;

public class Attendance extends  JcrNode implements Serializable, MappableToNode {
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

    @Override
    public Object toNode() {
        return NodeToModelMapper.INSTANCE.toNode(this);
    }
}
