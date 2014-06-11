package org.girlscouts.tools.meetingimporter;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class CollectionHoldString {

    public CollectionHoldString(String str) {
        this.str = str;
    }

    @Field
    private String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

}
