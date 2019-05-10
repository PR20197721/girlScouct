package org.girlscouts.vtk.models;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.Calendar;

public class JcrNode implements Serializable {
    private String path;
    private String id;
    private String uid;
    private boolean isDbUpdate;
    private Calendar createdDate;
    private Calendar lastModifiedDate;

    public JcrNode() {
    }

    public JcrNode(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if ((this.getPath() == null && path != null) || (this.getPath() != null && path != null && !this.getPath().equals(path))) {
            this.isDbUpdate = true;
        }
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if ((id != null && this.getId() != null && !this.getId().equals(id)) || (id != null && this.getId() == null)) {
            this.isDbUpdate = true;
        }
        this.id = id;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean isDbUpdate) {
        this.isDbUpdate = isDbUpdate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        if ((this.getUid() == null && uid != null) || (this.getUid() != null && uid != null && !this.getUid().equals(uid))) {
            this.isDbUpdate = true;
        }
        this.uid = uid;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }

    public Calendar getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Calendar lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}


