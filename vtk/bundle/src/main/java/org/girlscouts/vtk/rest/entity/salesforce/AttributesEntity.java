package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class AttributesEntity {
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{" + "type='" + type + '\'' + ", url='" + url + '\'' + '}';
    }
}
