package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseEntity {
    public String getJson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").enableComplexMapKeySerialization().serializeNulls().create();
        return gson.toJson(this);
    }
}
