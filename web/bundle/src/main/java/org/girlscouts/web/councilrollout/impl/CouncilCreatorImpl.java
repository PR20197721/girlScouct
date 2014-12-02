package org.girlscouts.web.councilrollout.impl;

import java.util.ArrayList;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.CouncilCreator;
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
	private static Logger LOG = LoggerFactory
			.getLogger(CouncilCreatorImpl.class);

	public ArrayList<Page> generateSite(Session session, ResourceResolver rr,
			String contentPath, String councilName, String councilTitle)
			throws GirlScoutsException {
		ArrayList<Page> pages = new ArrayList<Page>();
		String councilPath = contentPath + "/" + councilName;
		String languagePath = null;
		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);
			// Create Council HomePage
			pages.add(buildPage(manager, session, contentPath, councilTitle,
					null, councilName, "", "foundation/components/page"));

			// languagePath = "content/<council>/en"
			Page englishPage = buildLanguagePage(manager, session, councilPath,
					councilTitle, "en", "", "girlscouts/components/homepage",
					councilName);
			languagePath = englishPage.getPath();
			pages.add(englishPage);
			pages.add(buildPage(manager, session, languagePath, "Ad Page",
					null, "ad-page", "", "girlscouts/components/ad-list-page"));
			pages.add(buildPage(manager, session, languagePath, "Search | "
					+ councilTitle, "Search | " + councilTitle, "site-search",
					"", "girlscouts/components/three-column-page"));
			pages.add(buildPage(manager, session, languagePath, "Map", null,
					"map", "", "girlscouts/components/map"));

			pages.add(buildRepository(manager, session, languagePath,
					"events-repository", "", "Events Repository"));
			pages.add(buildRepository(manager, session, languagePath,
					"contacts", "", "Contacts"));
			pages.add(buildRepository(manager, session, languagePath,
					"milestones", "", "Milestones"));

			session.save();
		} catch (Exception e) {
			LOG.error("Unable to create ..... with stack trace : "
					+ e.toString());
			e.printStackTrace();
		}
		return pages;
	}

	public ArrayList<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle) throws GirlScoutsException {
		ArrayList<Node> damNodes = new ArrayList<Node>();
		try {
			Node damNode = session.getNode(path + "/" + "dam");
			Node councilNode = buildFolder(damNode, path, councilName, null);
			damNodes.add(councilNode);
			damNodes.add(buildFolder(councilNode, path + "/" + "dam/" + councilName, "documents", "Forms and Documents"));
			session.save();
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error("Provided path is not correct" + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return damNodes;

	}

	private Node buildFolder(Node node, String path, String folderName,
			String folderTitle) {
		Node folderNode = null;
		try {
			folderNode = node.addNode(folderName, "sling:OrderedFolder");
			Node jcrNode = folderNode.addNode("jcr:content", "nt:unstructured");
			if (folderTitle == null || folderTitle == "") {
				jcrNode.setProperty("jcr:title", folderName);
			} else {
				jcrNode.setProperty("jcr:title", folderTitle);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folderNode;
	}

	private Page buildLanguagePage(PageManager manager, Session session,
			String path, String title, String pageName, String template,
			String resourceType, String councilName) {
		Page languagePage = null;
		try {

			languagePage = manager.create(path, pageName, "", title);

			Node jcrNode = session.getNode(languagePage.getPath()
					+ "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			jcrNode.setProperty("sling:resourceType", resourceType);
			jcrNode.setProperty("cq:designPath", "/etc/designs/girlscouts-test");
			// Adding pre-set properties for the homepage, which can be changed
			jcrNode.setProperty("adsPath", path + "/" + pageName + "/ad-page");
			jcrNode.setProperty("calendarPath", path + "/" + pageName
					+ "/event-calendar");
			jcrNode.setProperty("eventLanding", path + "/" + pageName
					+ "/events/event-list");
			jcrNode.setProperty("eventPath", path + "/" + pageName
					+ "/events-repository");
			jcrNode.setProperty(
					"footerTracking",
					"<script src=\"https://www.girlscouts.org/includes/join/council_ebiz_conversion_include.js\"></script>");
			jcrNode.setProperty("globalLanding", path + "/" + pageName
					+ "/site-search");
			jcrNode.setProperty("hideSignIn", "false");
			jcrNode.setProperty("hideVTKButton", "false");
			jcrNode.setProperty("leftNavRoot", path + "/" + pageName
					+ "/events/event-list");
			jcrNode.setProperty("locale", "America/New_York");
			jcrNode.setProperty("locationsPath", path + "/" + pageName
					+ "/location");
			jcrNode.setProperty("newsPath", path + "/" + pageName
					+ "/our-council/news");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return languagePage;
	}

	private Page buildRepository(PageManager manager, Session session,
			String languagePath, String folderName, String template,
			String folderTitle) {
		Page thisRepositoryPage = null;
		try {
			thisRepositoryPage = manager.create(languagePath, folderName,
					template, folderTitle);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisRepositoryPage;
	}

	private Page buildPage(PageManager manager, Session session, String path,
			String title, String seoTitle, String pageName, String template,
			String resourceType) {
		Page returnPage = null;
		try {
			returnPage = manager.create(path, pageName, template, title);
			Node jcrNode = session.getNode(returnPage.getPath()
					+ "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			jcrNode.setProperty("sling:resourceType", resourceType);
			if (seoTitle != null) {
				jcrNode.setProperty("seoTitle", title);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnPage;
	}

}
