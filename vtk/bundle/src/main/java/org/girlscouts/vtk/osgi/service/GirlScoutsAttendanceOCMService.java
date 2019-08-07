package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Attendance;

import java.util.List;
import java.util.Map;

public interface GirlScoutsAttendanceOCMService {
    Attendance create(Attendance object);

    Attendance update(Attendance object);

    Attendance read(String path);

    boolean delete(Attendance object);

    Attendance findObject(String path, Map<String, String> params);

    List<Attendance> findObjects(String path, Map<String, String> params);

}
