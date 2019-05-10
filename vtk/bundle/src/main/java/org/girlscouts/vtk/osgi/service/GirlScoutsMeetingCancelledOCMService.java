package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.MeetingCanceled;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingCancelledOCMService {

    public MeetingCanceled create(MeetingCanceled object);
    public MeetingCanceled update(MeetingCanceled object);

    public MeetingCanceled read(String path);
    public boolean delete(MeetingCanceled object);

    public MeetingCanceled findObject(String path, Map<String, String> params);
    public List<MeetingCanceled> findObjects(String path, Map<String, String> params);

}
