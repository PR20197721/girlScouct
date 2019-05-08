package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.List;

@Node
public class YearPlanNode extends JcrNode implements Serializable, MappableToModel {

    @Field
    private String name;
    @Field
    private String desc;
    @Field
    private String refId;
    @Field
    private String altered;
    @Field
    private String resources;
    @Collection(autoUpdate = false)
    private List<MeetingENode> meetingEvents;
    @Collection(autoUpdate = false)
    private List<ActivityNode> activities;
    @Bean(autoUpdate = false)
    private CalNode schedule;
    @Collection(autoUpdate = false)
    private List<LocationNode> locations;
    @Field
    private Long calStartDate;
    @Field
    private String calFreq;
    @Field
    private String calExclWeeksOf;
    @Collection(autoUpdate = false)
    private List<MeetingCanceledNode> meetingCanceled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getAltered() {
        return altered;
    }

    public void setAltered(String altered) {
        this.altered = altered;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public List<MeetingENode> getMeetingEvents() {
        return meetingEvents;
    }

    public void setMeetingEvents(List<MeetingENode> meetingEvents) {
        this.meetingEvents = meetingEvents;
    }

    public List<ActivityNode> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityNode> activities) {
        this.activities = activities;
    }

    public CalNode getSchedule() {
        return schedule;
    }

    public void setSchedule(CalNode schedule) {
        this.schedule = schedule;
    }

    public List<LocationNode> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationNode> locations) {
        this.locations = locations;
    }

    public Long getCalStartDate() {
        return calStartDate;
    }

    public void setCalStartDate(Long calStartDate) {
        this.calStartDate = calStartDate;
    }

    public String getCalFreq() {
        return calFreq;
    }

    public void setCalFreq(String calFreq) {
        this.calFreq = calFreq;
    }

    public String getCalExclWeeksOf() {
        return calExclWeeksOf;
    }

    public void setCalExclWeeksOf(String calExclWeeksOf) {
        this.calExclWeeksOf = calExclWeeksOf;
    }

    public List<MeetingCanceledNode> getMeetingCanceled() {
        return meetingCanceled;
    }

    public void setMeetingCanceled(List<MeetingCanceledNode> meetingCanceled) {
        this.meetingCanceled = meetingCanceled;
    }

    @Override
    public Object toModel() {
        return NodeToModelMapper.INSTANCE.toModel(this);
    }
}
