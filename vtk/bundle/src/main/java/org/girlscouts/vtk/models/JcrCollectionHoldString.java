package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

@Node
public class JcrCollectionHoldString implements Comparable, Serializable {

    @Field
    private String str;

    public JcrCollectionHoldString() {
    }

    public JcrCollectionHoldString(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
