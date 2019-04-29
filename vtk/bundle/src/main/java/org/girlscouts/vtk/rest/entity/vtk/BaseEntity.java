package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.Gson;

public abstract class BaseEntity {

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
