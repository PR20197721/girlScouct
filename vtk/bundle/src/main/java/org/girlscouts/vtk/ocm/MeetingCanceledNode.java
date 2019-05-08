package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Node
public class MeetingCanceledNode extends YearPlanComponentNode implements Serializable, MappableToModel {
    @Collection
    private List<AssetNode> assets;
    @Collection
    private List<SentEmailNode> sentEmails;
    @Field
    private Date lastAssetUpdate;
    @Field
    private String refId;
    @Field
    private String locationRef;
    @Field
    private String cancelled;
    @Field
    private String emlTemplate;

    public List<AssetNode> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetNode> assets) {
        this.assets = assets;
    }

    public List<SentEmailNode> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<SentEmailNode> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public Date getLastAssetUpdate() {
        return lastAssetUpdate;
    }

    public void setLastAssetUpdate(Date lastAssetUpdate) {
        this.lastAssetUpdate = lastAssetUpdate;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getLocationRef() {
        return locationRef;
    }

    public void setLocationRef(String locationRef) {
        this.locationRef = locationRef;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getEmlTemplate() {
        return emlTemplate;
    }

    public void setEmlTemplate(String emlTemplate) {
        this.emlTemplate = emlTemplate;
    }

    @Override
    public Object toModel() {
        return NodeToModelMapper.INSTANCE.toModel(this);
    }

}
