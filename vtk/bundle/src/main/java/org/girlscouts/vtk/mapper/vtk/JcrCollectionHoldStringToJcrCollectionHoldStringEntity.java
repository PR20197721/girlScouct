package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.rest.entity.vtk.JcrCollectionHoldStringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrCollectionHoldStringToJcrCollectionHoldStringEntity extends BaseModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(MeetingToMeetingEntityMapper.class);

    public static JcrCollectionHoldStringEntity map(JcrCollectionHoldString jcrStr) {
        if (jcrStr != null) {
            try {
                JcrCollectionHoldStringEntity entity = new JcrCollectionHoldStringEntity();
                entity.setStr(jcrStr.getStr());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping JcrCollectionHoldString to JcrCollectionHoldStringEntity ", e);
            }
        }
        return null;
    }
}
