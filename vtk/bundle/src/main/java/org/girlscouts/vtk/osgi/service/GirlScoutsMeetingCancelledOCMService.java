package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.MeetingCanceled;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingCancelledOCMService {
    MeetingCanceled create(MeetingCanceled object);

    MeetingCanceled update(MeetingCanceled object);

    MeetingCanceled read(String path);

    boolean delete(MeetingCanceled object);

    MeetingCanceled findObject(String path, Map<String, String> params);

    List<MeetingCanceled> findObjects(String path, Map<String, String> params);

}
