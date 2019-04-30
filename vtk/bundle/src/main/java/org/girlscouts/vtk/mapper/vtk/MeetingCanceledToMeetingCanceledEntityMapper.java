package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.MeetingCanceled;
import org.girlscouts.vtk.rest.entity.vtk.MeetingCanceledEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingCanceledToMeetingCanceledEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(MeetingCanceledToMeetingCanceledEntityMapper.class);

    public static MeetingCanceledEntity map(MeetingCanceled meetingCanceled) {
        if (meetingCanceled != null) {
            try {
                MeetingCanceledEntity entity = new MeetingCanceledEntity();
                entity.setAssets(mapAssets(meetingCanceled.getAssets()));
                entity.setCancelled(meetingCanceled.getCancelled());
                entity.setDate(meetingCanceled.getDate());
                entity.setDbUpdate(meetingCanceled.isDbUpdate());
                entity.setEmlTemplate(meetingCanceled.getEmlTemplate());
                entity.setId(meetingCanceled.getId());
                entity.setLastAssetUpdate(meetingCanceled.getLastAssetUpdate());
                entity.setLocationRef(meetingCanceled.getLocationRef());
                entity.setMeetingInfo(MeetingToMeetingEntityMapper.map(meetingCanceled.getMeetingInfo()));
                entity.setPath(meetingCanceled.getPath());
                entity.setRefId(meetingCanceled.getRefId());
                entity.setSentEmails(mapSentEmails(meetingCanceled.getSentEmails(), meetingCanceled.getEmlTemplate()));
                entity.setType(meetingCanceled.getType());
                entity.setUid(meetingCanceled.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping MeetingCanceled to MeetingCanceledEntity ", e);
            }
        }
        return null;
    }
}
