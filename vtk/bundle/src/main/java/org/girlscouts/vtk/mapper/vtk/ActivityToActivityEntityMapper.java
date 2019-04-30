package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.rest.entity.vtk.ActivityEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityToActivityEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(ActivityToActivityEntityMapper.class);

    public static ActivityEntity map(Activity activity){
        if(activity != null) {
            try {
                ActivityEntity entity = new ActivityEntity();
                entity.setActivityDescription(activity.getActivityDescription());
                entity.setActivityNumber(activity.getActivityNumber());
                entity.setAssets(mapAssets(activity.getAssets()));
                entity.setAttendance(AttendanceToAttendanceEntityMapper.map(activity.getAttendance()));
                entity.setCancelled(activity.getCancelled());
                entity.setContent(activity.getContent());
                entity.setCost(activity.getCost());
                entity.setDate(activity.getDate());
                entity.setDbUpdate(activity.isDbUpdate());
                entity.setDuration(activity.getDuration());
                entity.setEditable(activity.getIsEditable());
                entity.setEmlTemplate(activity.getEmlTemplate());
                entity.setEndDate(activity.getEndDate());
                entity.setGlobal(activity.getGlobal());
                entity.setId(activity.getId());
                entity.setImg(activity.getImg());
                entity.setLocationAddress(activity.getLocationAddress());
                entity.setLocationName(activity.getLocationName());
                entity.setLocationRef(activity.getLocationRef());
                entity.setMaterials(activity.getMaterials());
                entity.setMultiactivities(mapActivities(activity.getMultiactivities()));
                entity.setName(activity.getName());
                entity.setOutdoor(activity.getOutdoor());
                entity.setPath(activity.getPath());
                entity.setRefUid(activity.getRefUid());
                entity.setRegisterUrl(activity.getRegisterUrl());
                entity.setSelected(activity.getIsSelected());
                entity.setSentEmails(mapSentEmails(activity.getSentEmails(), activity.getEmlTemplate()));
                entity.setSteps(activity.getSteps());
                entity.setSubtitle(activity.getSubtitle());
                entity.setUid(activity.getUid());
                entity.setType(activity.getType());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Activity to ActivityEntity ", e);
            }
        }
        return null;
    }
}
