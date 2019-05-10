package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.MeetingE;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingEOCMService {

    public MeetingE create(MeetingE object);
    public MeetingE update(MeetingE object);

    public MeetingE read(String path);
    public boolean delete(MeetingE object);

    public MeetingE findObject(String path, Map<String, String> params);
    public List<MeetingE> findObjects(String path, Map<String, String> params);

}
