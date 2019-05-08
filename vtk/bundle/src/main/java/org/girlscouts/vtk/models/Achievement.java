package org.girlscouts.vtk.models;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;

public class Achievement extends JcrNode implements Serializable, MappableToNode {

    private String users; // sf id
    private int total;

    public Achievement(){

    }

    public Achievement(String path) {
        super(path);
    }

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
