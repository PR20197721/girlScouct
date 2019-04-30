package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.rest.entity.vtk.MeetingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingToMeetingEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(MeetingToMeetingEntityMapper.class);

    public static MeetingEntity map(Meeting meeting) {
        if(meeting != null) {
            try {
                MeetingEntity entity = new MeetingEntity();
                entity.setAchievement(meeting.getIsAchievement());
                entity.setActivities(mapActivities(meeting.getActivities()));
                entity.setAgenda(meeting.getAgenda());
                entity.setAidTags(meeting.getAidTags());
                entity.setBlurb(meeting.getBlurb());
                entity.setCat(meeting.getCat());
                entity.setCatTags(meeting.getCatTags());
                entity.setCatTagsAlt(meeting.getCatTagsAlt());
                entity.setId(meeting.getId());
                entity.setLevel(meeting.getLevel());
                entity.setMeetingInfo(mapMeetingInfo(meeting.getMeetingInfo()));
                entity.setMeetingPlanType(meeting.getMeetingPlanType());
                entity.setMeetingPlanTypeAlt(meeting.getMeetingPlanTypeAlt());
                entity.setName(meeting.getName());
                entity.setPath(meeting.getPath());
                entity.setPosition(meeting.getPosition());
                entity.setReq(meeting.getReq());
                entity.setReqTitle(meeting.getReqTitle());
                entity.setResources(meeting.getResources());
                entity.setType(meeting.getType());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Meeting to MeetingEntity ", e);
            }
        }
        return null;
    }
}
