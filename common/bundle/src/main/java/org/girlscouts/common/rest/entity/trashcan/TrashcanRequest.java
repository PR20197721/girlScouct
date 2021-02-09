package org.girlscouts.common.rest.entity.trashcan;

import java.util.List;

public class TrashcanRequest {

    private String action;
    private List<TrashcanItem> items;
    //GSAWDO-61 -Force delete references
    private List<String> refErrorAssertLocation;
    private Boolean forceDeleteRef = false;
    private Boolean forceRepublishUpdatedPages = false;;

    public List<TrashcanItem> getItems() {
        return items;
    }

    public void setItems(List<TrashcanItem> items) {
        this.items = items;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setForceDeleteRef(Boolean forceDeleteRef) {
        this.forceDeleteRef = forceDeleteRef;
    }

    public void setForceRepublishUpdatedPages(Boolean forceRepublishUpdatedPages) {
        this.forceRepublishUpdatedPages = forceRepublishUpdatedPages;
    }

    public void setRefErrorAssertLocation(List<String> refErrorAssertLocation) {
        this.refErrorAssertLocation = refErrorAssertLocation;
    }

    public Boolean getForceDeleteRef() {
        return forceDeleteRef;
    }

    public Boolean getForceRepublishUpdatedPages() {
        return forceRepublishUpdatedPages;
    }

    public List<String> getRefErrorAssertLocation() {
        return refErrorAssertLocation;
    }
}

