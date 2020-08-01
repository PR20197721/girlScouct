package org.girlscouts.vtk.osgi.service;

import java.io.InputStream;

public interface GirlScoutsMeetingIconService {

    InputStream getIconByMeetingId(String id);

}
