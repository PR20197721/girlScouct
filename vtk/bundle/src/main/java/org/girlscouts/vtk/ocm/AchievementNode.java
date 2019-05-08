package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Achievement;

import java.io.Serializable;

@Node()
public class AchievementNode extends JcrNode implements Serializable, MappableToModel {
    @Field
    private String users;
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

    @Override
    public Object toModel() {
        return NodeToModelMapper.INSTANCE.toModel(this);
    }
}
