package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

@Node
public class Council implements Serializable {
    @Field(path = true)
    String path;

    public Council() {
    }

    public Council(String path) {
        this.path = path;
    }

    /*
        @Collection
        private java.util.List<Troop> troops;

        public java.util.List<Troop> getTroops() {
            return troops;
        }

        public void setTroops(java.util.List<Troop> troops) {
            this.troops = troops;
        }
        */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
