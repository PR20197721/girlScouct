package org.girlscouts.web.councilrollout.impl;

import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.CouncilCreator;
import org.girlscouts.web.dataimport.impl.DataImporterFactoryImpl;
import org.girlscouts.web.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component
@Service(value = CouncilCreator.class)
@Properties({
		@Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.councilcreator", propertyPrivate = false),
		@Property(name = "service.description", value = "Girl Scouts council rollout service", propertyPrivate = false),
		@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class CouncilCreatorImpl implements CouncilCreator {
	private static Logger log = LoggerFactory
			.getLogger(CouncilCreatorImpl.class);
	
	ArrayList<String> paths = new ArrayList<String>();

	public ArrayList<String> generateHomePage(Session session,
			ResourceResolver rr, String contentPath, String councilName,
			String councilTitle) throws GirlScoutsException {
		String councilPath = null;
		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);
			// Create Council HomePage
			Page councilPage = manager.create(contentPath, councilName, "",
					councilTitle, true);
			councilPath = councilPage.getPath();
			paths.add(councilPath);
			Node jcrNode = session.getNode(councilPath + "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			jcrNode.setProperty("sling:resourceType",
					"foundation/components/page");
			String enPath = generateEnglishPage(manager, session, rr,
					councilPath, "en", councilTitle);
			paths.add(enPath);
			session.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paths;
	}

	String generateEnglishPage(PageManager manager, Session session,
			ResourceResolver rr, String contentPath, String pageName,
			String councilTitle) {
		String pagePath = null;
		try {

			Page englishPage = manager.create(contentPath, pageName, "",
					councilTitle, true);
			pagePath = englishPage.getPath();
			Node jcrNode = session.getNode(pagePath + "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			jcrNode.setProperty("sling:resourceType",
					"girlscouts/components/homepage");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pagePath;
	}

}
