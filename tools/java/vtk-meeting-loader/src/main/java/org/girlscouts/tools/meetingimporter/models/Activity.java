package org.girlscouts.tools.meetingimporter.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Activity {

    @Field(path = true)
    String path;
    @Field
    private String name, activityDescription;
    @Field
    private int duration, activityNumber;

    public String getPath() {

        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(int activityNumber) {
        this.activityNumber = activityNumber;
        /*
         * if( path==null ) path="/myActivity/"+activityNumber;
         * 
         * //System.err.println("Activity path: "+ path);
         */
    }

}
