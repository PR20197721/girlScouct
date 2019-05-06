package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.AssetComponentType;

import java.io.Serializable;

public class Asset extends JcrNode implements Serializable {
    private Boolean isCachable;
    private String type, description, title, docType, refId;
    private Boolean isOutdoorRelated;

    public Asset() {
        this.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
        this.isCachable = false;
        this.type = "AID";
    }

    public Asset(String path) {
        this.setPath(path);
        this.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
        this.isCachable = false;
        this.type = "AID";
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        if ((docType != null && this.docType != null && !this.docType.equals(docType)) || (docType != null && this.docType == null)) {
            setDbUpdate(true);
        }
        this.docType = docType;
    }

    public Boolean getIsOutdoorRelated() {
        return isOutdoorRelated == null ? false : isOutdoorRelated;
    }

    public void setIsOutdoorRelated(Boolean isOutdoorRelated) {
        this.isOutdoorRelated = isOutdoorRelated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if ((title != null && this.title != null && !this.title.equals(title)) || (title != null && this.title == null)) {
            setDbUpdate(true);
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if ((description != null && this.description != null && !this.description.equals(description)) || (description != null && this.description == null)) {
            setDbUpdate(true);
        }
        this.description = description;
    }

    public Boolean getIsCachable() {
        return isCachable;
    }

    public void setIsCachable(Boolean isCachable) {
        if ((isCachable != null && this.isCachable != null && this.isCachable.booleanValue() != isCachable.booleanValue()) || (isCachable != null && this.isCachable == null)) {
            setDbUpdate(true);
        }
        this.isCachable = isCachable;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        if ((refId != null && this.refId != null && !this.refId.equals(refId)) || (refId != null && this.refId == null)) {
            setDbUpdate(true);
        }
        this.refId = refId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if ((type != null && this.type != null && !this.type.equals(type)) || (type != null && this.type == null)) {
            setDbUpdate(true);
        }
        this.type = type;
    }

    public void setType(AssetComponentType act) {
        setType(act.toString());
    }

    public AssetComponentType getType(boolean nothing) {
        return AssetComponentType.valueOf(this.getType());
    }

}
