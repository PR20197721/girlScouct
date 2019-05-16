package org.girlscouts.vtk.models;

import java.io.Serializable;

public class Council extends JcrNode implements Serializable {
    public Council() {
    }

    public Council(String path) {
        this.setPath(path);
    }

}
