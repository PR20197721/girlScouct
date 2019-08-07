package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;

@Node
public class AssetNode extends JcrNode implements Serializable {
    @Field(jcrDefaultValue = "false")
    private Boolean isCachable;
    @Field
    private String type;
    @Field
    private String description;
    @Field
    private String title;
    @Field
    private String docType;
    @Field
    private String refId;
    @Field(jcrDefaultValue = "false")
    private Boolean isOutdoorRelated;

    public Boolean getIsCachable() {
        return isCachable;
    }

    public void setIsCachable(Boolean cachable) {
        isCachable = cachable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Boolean getIsOutdoorRelated() {
        return isOutdoorRelated;
    }

    public void setIsOutdoorRelated(Boolean outdoorRelated) {
        isOutdoorRelated = outdoorRelated;
    }

}
