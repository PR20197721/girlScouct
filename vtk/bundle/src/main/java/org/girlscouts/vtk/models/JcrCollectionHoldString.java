package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;

public class JcrCollectionHoldString implements Comparable, Serializable {

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
