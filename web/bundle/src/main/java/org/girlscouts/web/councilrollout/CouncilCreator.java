package org.girlscouts.web.councilrollout;

import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.exception.GirlScoutsException;

public interface CouncilCreator {

    String generateHomePage(Session session, ResourceResolver rr,
			String councilPath, String councilName, String councilTitle) throws GirlScoutsException;

}