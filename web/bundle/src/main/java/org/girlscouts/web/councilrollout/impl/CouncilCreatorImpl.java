package org.girlscouts.web.councilrollout.impl;

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

	public String generateHomePage(Session session, ResourceResolver rr,
			String contentPath, String councilName, String councilTitle)
			throws GirlScoutsException {
		String councilPath;
		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);
			Page councilPage = manager.create(contentPath, councilName, "",
					councilTitle, true);
			councilPath = councilPage.getPath();
			Node jcrNode = session.getNode(councilPath + "/jcr:content");
			jcrNode.setProperty("sling:resourceType", "foundation/components/page");
			session.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return councilTitle;
	}
}