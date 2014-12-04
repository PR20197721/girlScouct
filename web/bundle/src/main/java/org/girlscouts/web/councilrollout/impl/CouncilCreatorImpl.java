package org.girlscouts.web.councilrollout.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.CouncilCreator;
import org.girlscouts.web.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.security.Authorizable;
import com.day.cq.security.Group;
import com.day.cq.security.UserManager;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

@Component
@Service(value = CouncilCreator.class)
@Properties({
		@Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.councilcreator", propertyPrivate = false),
		@Property(name = "service.description", value = "Girl Scouts council rollout service", propertyPrivate = false),
		@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class CouncilCreatorImpl implements CouncilCreator {
	private static Logger LOG = LoggerFactory.getLogger(CouncilCreatorImpl.class);

	public ArrayList<Page> generateSite(Session session, ResourceResolver rr, String contentPath, String councilName, String councilTitle) throws GirlScoutsException {
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, String> propertyMap = new HashMap<String,String>();
		String councilPath = contentPath + "/" + councilName;
		String languagePath = null;
		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);
			// Create Council HomePage
			pages.add(buildPage(manager, session, contentPath, councilTitle, null, councilName, "", "foundation/components/page"));
			
			propertyMap = setLangPropertyMap(councilPath, "en");
			Page englishPage = buildPage(manager, session, councilPath, councilTitle, "en", "", "girlscouts/components/homepage", propertyMap);
			languagePath = englishPage.getPath();
			pages.add(englishPage);
			pages.add(buildPage(manager, session, languagePath, "Ad Page", null, "ad-page", "", "girlscouts/components/ad-list-page"));
			pages.add(buildPage(manager, session, languagePath, "Search | " + councilTitle, "Search | " + councilTitle, "site-search", "", "girlscouts/components/three-column-page"));
			pages.add(buildPage(manager, session, languagePath, "Map", null, "map", "", "girlscouts/components/map"));

			pages.add(buildRepository(manager, session, languagePath, "events-repository", "", "Events Repository"));
			pages.add(buildRepository(manager, session, languagePath, "contacts", "", "Contacts"));
			pages.add(buildRepository(manager, session, languagePath, "milestones", "", "Milestones"));
			session.save();
		} catch (Exception e) {
			LOG.error("Unable to create ..... with stack trace : " + e.toString());
			e.printStackTrace();
		}
		return pages;
	}
	
	public ArrayList<Page> generateScaffolding(Session session, ResourceResolver rr, String councilName) throws GirlScoutsException {
		ArrayList<Page> scaffoldings = new ArrayList<Page>();
		final String scaffoldingPath = "/etc/scaffolding";
		final String scaffoldingPrototype = "girlscouts-prototype";
		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);

			Node scaffoldingFolder = session.getNode(scaffoldingPath);
			Node councilFolder = buildFolder(scaffoldingFolder, councilName, null, "nt:folder", false);
			
			if(scaffoldingFolder.hasNode(scaffoldingPrototype)){
			Resource prototype = rr.resolve(scaffoldingPath + "/" + scaffoldingPrototype);
			if(scaffoldingFolder.getNode(scaffoldingPrototype).hasNodes()){
				Iterator<Resource> i = prototype.listChildren();
				while(i.hasNext()){
					Page scaffoldingType = (Page) i.next().adaptTo(Page.class);
					String type = scaffoldingType.getName();
					manager.copy(scaffoldingType, scaffoldingPath +  "/" + councilName, type, false, false);
				}
			}
			}
			LOG.error("SEKJFHWKEF");
			session.save();
		} catch (Exception e) {
			LOG.error("Unable to create DAM Folders with stack trace : " + e.toString());
		}
		return scaffoldings;

	}
	
	public ArrayList<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle) throws GirlScoutsException {
		ArrayList<Node> damNodes = new ArrayList<Node>();
		try {
			Node damNode = session.getNode(path + "/" + "dam");
			Node councilNode = buildFolder(damNode, councilName, null, "sling:OrderedFolder", true);
			damNodes.add(councilNode);
			damNodes.add(buildFolder(councilNode, "documents", "Forms and Documents", "sling:OrderedFolder", true));
			session.save();
		} catch (PathNotFoundException e) {
			LOG.error("Provided path is not correct" + e.toString());
		} catch (Exception e) {
			LOG.error("Unable to create DAM Folders with stack trace : " + e.toString());
		}
		return damNodes;

	}

	public ArrayList<Tag> generateTags(Session session, ResourceResolver rr, String path, String councilName, String councilTitle) throws GirlScoutsException {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		final String tagPath = "/etc/tags";
		try {
			TagManager manager = (TagManager) rr.adaptTo(TagManager.class);
			tags.add(manager.createTag(tagPath + "/" + councilName, councilTitle, ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "categories", "Categories", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "program-level", "Program Level", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "forms_documents", "Forms & Documents", ""));
			session.save();
		} catch (InvalidTagFormatException e){
			LOG.error("Unable to create Tag with correct format: " + e.toString());
		} catch (Exception e){
			e.printStackTrace();
		}
		return tags;
	}
	
	public ArrayList<Group> generateGroups(Session session, ResourceResolver rr, String councilName, String councilTitle) throws GirlScoutsException {
		ArrayList<Group> groupList = new ArrayList<Group>();
		String homePath = "/home/groups";
		try {
			LOG.error("SJSHEFKJEFW");
			UserManager manager = (UserManager) rr.adaptTo(UserManager.class);
			Group councilAuthors = manager.createGroup(councilName + "-authors", councilName + "-authors", homePath + "/" + councilName);
			Group councilReviewers = manager.createGroup(councilName + "-reviewers", councilName + "-reviewers", homePath + "/" + councilName);
			groupList.add(councilAuthors);
			groupList.add(councilReviewers);
			Group c = (Group) manager.findByHome(homePath + "/girlscouts-usa/gs-authors");
		c.addMember(councilAuthors);
			LOG.error(c.getID());
			LOG.error("SJSHEFKJEFW");
			session.save();

		} catch(Exception e){
			LOG.error(e.toString());
		}
			return groupList;
	}
	
	private HashMap<String,String> setLangPropertyMap(String path, String langAbbrev){
		HashMap<String,String> propertyMap = new HashMap<String,String>();
		try{
			propertyMap.put("cq:designPath", "/etc/designs/girlscouts-usa-green");
			propertyMap.put("adsPath", path + "/" + langAbbrev + "/ad-page");
			propertyMap.put("calendarPath", path + "/" + langAbbrev + "/event-calendar");
			propertyMap.put("eventLanding", path + "/" + langAbbrev + "/events/event-list");
			propertyMap.put("eventPath", path + "/" + langAbbrev + "/events-repository");
			propertyMap.put("footerTracking", "<script src=\"https://www.girlscouts.org/includes/join/council_ebiz_conversion_include.js\"></script>");
			propertyMap.put("globalLanding", path + "/" + langAbbrev + "/site-search");
			propertyMap.put("hideSignIn", "false");
			propertyMap.put("hideVTKButton", "false");
			propertyMap.put("leftNavRoot", path + "/" + langAbbrev + "/event-list");
			propertyMap.put("locale", "America/New_York");
			propertyMap.put("locationsPath", path + "/" + langAbbrev + "/location");
			propertyMap.put("newsPath", path + "/" + langAbbrev + "/our-council/news");					
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return propertyMap;
	}
	private Node buildFolder(Node node, String folderName, String folderTitle, String primaryType, boolean hasJcrNode) {
		Node folderNode = null;
		try {
			folderNode = node.addNode(folderName, primaryType);
			if (hasJcrNode){
			Node jcrNode = folderNode.addNode("jcr:content", "nt:unstructured");
			
			if (folderTitle == null || folderTitle == "") {
				jcrNode.setProperty("jcr:title", folderName);
			} else {
				jcrNode.setProperty("jcr:title", folderTitle);

			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folderNode;
	}
	
	private Page buildPage(PageManager manager, Session session, String path, String title, String seoTitle, String pageName, String template, String resourceType) {
		Page returnPage = null;
		try {
			returnPage = manager.create(path, pageName, template, title);
			Node jcrNode = session.getNode(returnPage.getPath() + "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			jcrNode.setProperty("sling:resourceType", resourceType);
			if (seoTitle != null) {
				jcrNode.setProperty("seoTitle", title);
			}
		} catch (WCMException e){
			LOG.error("Cannot build page titled " + title + " " + e.toString());
		} catch (Exception e){
			e.printStackTrace();	
			}
		return returnPage;
	}
	
	private Page buildPage(PageManager manager, Session session, String path, String title, String pageName, String template, String resourceType, HashMap<String, String> propertyMap) {
		Page languagePage = null;
		try {

			languagePage = manager.create(path, pageName, "", title);

			Node jcrNode = session.getNode(languagePage.getPath() + "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			if(resourceType != null || resourceType == ""){
			jcrNode.setProperty("sling:resourceType", resourceType);
			}
			
			if (!propertyMap.isEmpty()){
				for(Entry<String, String> map: propertyMap.entrySet()){
					jcrNode.setProperty(map.getKey(), map.getValue());
				}
			}

		} catch (Exception e) {
			LOG.error("Cannot build page titled " + title + " " + e.toString());
		}
		return languagePage;
	}
	
	private Page buildRepository(PageManager manager, Session session, String languagePath, String pageName, String template, String title) {
		Page thisRepositoryPage = null;
		try {
			thisRepositoryPage = manager.create(languagePath, pageName, template, title);
		} catch (Exception e) {
			LOG.error("Cannot build Repository page titled " + title + " " + e.toString());
		}
		return thisRepositoryPage;
	}

}
