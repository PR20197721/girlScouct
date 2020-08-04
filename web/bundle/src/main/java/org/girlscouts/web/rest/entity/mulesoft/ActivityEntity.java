package org.girlscouts.web.rest.entity.mulesoft;

public class ActivityEntity {

    private String action;

    private PayloadEntity payload;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PayloadEntity getPayload() {
        return payload;
    }

    public void setPayload(PayloadEntity payload) {
        this.payload = payload;
    }
}
