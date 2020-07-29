package org.girlscouts.web.osgi.service;

import org.girlscouts.web.rest.entity.mulesoft.ActivityEntity;

import java.util.Date;

public interface MulesoftActivitiesRestClient {

    public ActivityEntity[] getEvents(Date asOfDate);

}
