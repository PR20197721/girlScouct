package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.rest.entity.vtk.AttendanceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttendanceToAttendanceEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(AttendanceToAttendanceEntityMapper.class);

    public static AttendanceEntity map(Attendance attendance) {
        if (attendance != null) {
            try {
                AttendanceEntity entity = new AttendanceEntity();
                entity.setId(attendance.getId());
                entity.setPath(attendance.getPath());
                entity.setTotal(attendance.getTotal());
                entity.setUsers(attendance.getUsers());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Attendance to AttendanceEntity ", e);
            }
        }
        return null;
    }
}
