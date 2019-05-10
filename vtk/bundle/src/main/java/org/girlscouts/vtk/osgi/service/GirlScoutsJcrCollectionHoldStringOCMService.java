package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Attendance;

import java.util.List;
import java.util.Map;

public interface GirlScoutsJcrCollectionStringOCMService {

    public Attendance create(Attendance object);
    public Attendance update(Attendance object);

    public Attendance read(String path);
    public boolean delete(Attendance object);

    public Attendance findObject(String path, Map<String, String> params);
    public List<Attendance> findObjects(String path, Map<String, String> params);

}
