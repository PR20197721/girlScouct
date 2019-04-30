package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.rest.entity.vtk.MeetingEEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingEToMeetingEEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(MeetingEToMeetingEEntityMapper.class);

    public static MeetingEEntity map(MeetingE meetingE) {
        if (meetingE != null) {
            try {
                MeetingEEntity entity = new MeetingEEntity();
                entity.setAchievement(AchievementToAchievementEntityMapper.map(meetingE.getAchievement()));
                entity.setAllMultiActivitiesSelected(meetingE.isAllMultiActivitiesSelected());
                entity.setAnyGlobalActivityInMeeting(meetingE.isAnyGlobalActivityInMeeting());
                entity.setAnyGlobalActivityInMeetingAvailable(meetingE.isAnyGlobalActivityInMeetingAvailable());
                entity.setAnyOutdoorActivityInMeeting(meetingE.isAnyOutdoorActivityInMeeting());
                entity.setAnyOutdoorActivityInMeetingAvailable(meetingE.isAnyOutdoorActivityInMeetingAvailable());
                entity.setAssets(mapAssets(meetingE.getAssets()));
                entity.setAttendance(AttendanceToAttendanceEntityMapper.map(meetingE.getAttendance()));
                entity.setCancelled(meetingE.getCancelled());
                entity.setDbUpdate(meetingE.isDbUpdate());
                entity.setEmlTemplate(meetingE.getEmlTemplate());
                entity.setId(meetingE.getId());
                entity.setLastAssetUpdate(meetingE.getLastAssetUpdate());
                entity.setLocationRef(meetingE.getLocationRef());
                entity.setMeetingInfo(MeetingToMeetingEntityMapper.map(meetingE.getMeetingInfo()));
                entity.setNotes(mapNotes(meetingE.getNotes()));
                entity.setPath(meetingE.getPath());
                entity.setRefId(meetingE.getRefId());
                entity.setSentEmails(mapSentEmails(meetingE.getSentEmails(), meetingE.getEmlTemplate()));
                entity.setType(meetingE.getType());
                entity.setUid(meetingE.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping MeetingE to MeetingEEntity ", e);
            }
        }
        return null;
    }
}
