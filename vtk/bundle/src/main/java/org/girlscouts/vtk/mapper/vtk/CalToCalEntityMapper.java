package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.rest.entity.vtk.CalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalToCalEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(CalToCalEntityMapper.class);

    public static CalEntity map(Cal cal){
        if(cal != null) {
            try {
                CalEntity entity = new CalEntity();
                entity.setDates(cal.getDates());
                entity.setDbUpdate(cal.isDbUpdate());
                entity.setPath(cal.getPath());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Cal to CalEntity ", e);
            }
        }
        return null;
    }
}
