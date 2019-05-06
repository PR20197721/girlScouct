package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

public class Council extends JcrNode implements Serializable {

    public Council() {
    }

    public Council(String path) {
        this.setPath(path);
    }

}
