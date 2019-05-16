package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.MeetingE;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingEOCMService {
    MeetingE create(MeetingE object);

    MeetingE update(MeetingE object);

    MeetingE read(String path);

    boolean delete(MeetingE object);

    MeetingE findObject(String path, Map<String, String> params);

    List<MeetingE> findObjects(String path, Map<String, String> params);

}
