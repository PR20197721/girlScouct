package org.girlscouts.web.osgi.service;

import java.util.Date;

public interface MulesoftActivitiesRestClient {

    public String getEvents(Date asOfDate);

}
