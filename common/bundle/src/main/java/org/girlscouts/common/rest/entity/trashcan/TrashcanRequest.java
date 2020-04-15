package org.girlscouts.common.rest.entity.trashcan;

import java.util.List;

public class TrashcanRequest {

    private String action;
    private List<TrashcanItem> items;

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
}

