package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Calendar;

@Node(jcrMixinTypes = "mix:referenceable")
public class JcrNode implements Serializable {
    @Field(path = true, jcrMandatory = true)
    private String path;
    @Field
    private String uuid;
    @Field(id = true)
    private String id;
    @Field(id = true)
    private String uid;
    @Field(jcrName = "createdDate", jcrType = "Date", converter = CalendarOCMPropertyConverter.class)
    private Calendar createdDate;
    @Field(jcrName = "lastModifiedDate", jcrType = "Date", converter = CalendarOCMPropertyConverter.class)
    private Calendar lastModifiedDate;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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
