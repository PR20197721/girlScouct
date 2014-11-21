package org.girlscouts.web.councilrollout;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.exception.GirlScoutsException;

import com.day.cq.wcm.api.PageManager;

public interface CouncilCreator {

    ArrayList<String> generateHomePage(Session session, ResourceResolver rr,
			String councilPath, String councilName, String councilTitle) throws GirlScoutsException;

}