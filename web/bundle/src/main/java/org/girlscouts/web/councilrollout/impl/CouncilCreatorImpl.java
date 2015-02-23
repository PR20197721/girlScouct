package org.girlscouts.web.councilrollout.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.CouncilCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.Replicator;
import com.day.cq.security.Authorizable;
import com.day.cq.security.Group;
import com.day.cq.security.UserManager;

import javax.jcr.security.Privilege;

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

	public List<Page> generateSite(Session session, ResourceResolver rr, String contentPath, String councilName, String councilTitle) {
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		String councilPath = contentPath + "/" + councilName;

		try {
			PageManager manager = (PageManager) rr.adaptTo(PageManager.class);
			// Create Council HomePage
			pages.add(buildPage(manager, session, contentPath, councilTitle, null, councilName, "", "foundation/components/page", null));
			propertyMap = setLangPropertyMap(councilPath, "en");
			Page englishPage = buildPage(manager, session, councilPath, councilTitle, null, "en", "","girlscouts/components/homepage", propertyMap);

			String languagePath = englishPage.getPath();

			pages.add(englishPage);
			pages.add(buildPage(manager, session, languagePath, "Ad Page", null, "ad-page", "", "girlscouts/components/ad-list-page", null));
			pages.add(buildPage(manager, session, languagePath, "Search | " + councilTitle, "Search | " + councilTitle, "site-search", "", "girlscouts/components/three-column-page", null));
			pages.add(buildPage(manager, session, languagePath, "Map", null, "map", "", "girlscouts/components/map", null));
			pages.add(buildPage(manager, session, languagePath, "404", null, "404", "", "girlscouts/components/error-page", null));
			pages.add(buildRepository(manager, session, languagePath, "events-repository", "", "Events Repository"));
			pages.add(buildRepository(manager, session, languagePath, "contacts", "", "Contacts"));
			pages.add(buildRepository(manager, session, languagePath, "milestones", "", "Milestones"));

	        session.save();
	        
		    } catch (Exception e) {
		    	LOG.error("Error occurred during Page Building Procedure: " + e.toString());
		    	e.printStackTrace();
		    }
		return pages;
	}

	public List<Node> generateScaffolding(Session session, ResourceResolver rr, String councilName) {

		ArrayList<Node> scaffoldings = new ArrayList<Node>();
		final String scaffoldingPath = "/etc/scaffolding";
		final String scaffoldingPrototype = "girlscouts-prototype";

		try {
			Node scaffoldingFolder = session.getNode(scaffoldingPath);
			Node councilFolder = buildFolder(scaffoldingFolder, councilName, null, "nt:folder", false);

			if (scaffoldingFolder.hasNode(scaffoldingPrototype)) {
				Node n = session.getNode(scaffoldingPath + "/" + scaffoldingPrototype);

				if (scaffoldingFolder.getNode(scaffoldingPrototype).hasNodes()) {
					NodeIterator i = n.getNodes();

					while (i.hasNext()) {
						Node scaffoldingNode = i.nextNode();
						String targetPath = scaffoldingNode.getNode("jcr:content").getProperty("cq:targetPath").getValue().getString();
						String type = scaffoldingNode.getName();
						scaffoldingNode = JcrUtil.copy(scaffoldingNode, councilFolder, type);
						targetPath = targetPath.replace(scaffoldingPrototype, councilName);
						scaffoldingNode.getNode("jcr:content").setProperty("cq:targetPath", targetPath);
						scaffoldings.add(scaffoldingNode);
					}
				} else {
					LOG.error(scaffoldingPrototype + "folder contains no nodes, cannot copy scaffolding");
					throw new RepositoryException();
				}
			} else {
				LOG.error(scaffoldingPrototype + "folder not found, cannot copy scaffolding");
				throw new PathNotFoundException();
			}
			session.save();
			
		} catch (PathNotFoundException e) {
			LOG.error("Path not found during scaffolding generation: " + e.toString());
			e.printStackTrace();
		} catch (RepositoryException e) {
			LOG.error("Error with Repository: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("Unable to create DAM Folders with stack trace: " + e.toString());
			e.printStackTrace();
		}
		return scaffoldings;
	}

	public List<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle) {
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

	public List<Tag> generateTags(Session session, ResourceResolver rr, String councilName, String councilTitle) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		final String tagPath = "/etc/tags";

		try {
			TagManager manager = (TagManager) rr.adaptTo(TagManager.class);
			tags.add(manager.createTag(tagPath + "/" + councilName, councilTitle, ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "categories", "Categories", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "program-level", "Program Level", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "forms_documents", "Forms & Documents", ""));
			session.save();

		} catch (InvalidTagFormatException e) {
			LOG.error("Unable to create Tag with correct format: " + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tags;
	}

	public List<Node> generateDesign(Session session, ResourceResolver rr, String councilName, String councilTitle) {
		ArrayList<Node> design = new ArrayList<Node>();
		final String designPath = "/etc/designs";
		final String designPrototypePath = "girlscouts-prototype";

		try {
			Node designsFolder = session.getNode(designPath);
			if (designsFolder.hasNode(designPrototypePath)) {
				Node prototypeDesign = designsFolder.getNode(designPrototypePath);
				Node councilDesign = JcrUtil.copy(prototypeDesign, designsFolder, "girlscouts-" + councilName);
				buildCSSFile(councilDesign, councilName, councilTitle, "text/css", "utf-8");				
				design.add(councilDesign);
				councilDesign.getNode("jcr:content").setProperty("jcr:title", councilTitle);

			} else {
				LOG.error("design prototype folder not found");
				throw new PathNotFoundException();
			}
			session.save();
			
		} catch (PathNotFoundException e) {
			LOG.error("Path not found during design generation: " + e.toString());
			e.printStackTrace();
		} catch (RepositoryException e) {
			LOG.error("Error with Repository: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("Cannot generate design: " + e.toString());
			e.printStackTrace();
		}
		return design;
	}

	public List<Group> generateGroups(Session session, ResourceResolver rr,	String councilName, String councilTitle) {
		ArrayList<Group> groupList = new ArrayList<Group>();
		final String homePath = "/home/groups";
		final String girlscoutsPath = "girlscouts-usa";
		final String allAuthorsGroup = "gs-authors";
		final String allReviewersGroup = "gs-authors";

		try {
			UserManager manager = (UserManager) rr.adaptTo(UserManager.class);
			Group councilAuthors = manager.createGroup(councilName + "-authors", councilName + "-authors", homePath + "/" + councilName);
			Group councilReviewers = manager.createGroup(councilName + "-reviewers", councilName + "-reviewers", homePath + "/" + councilName);
			groupList.add(councilAuthors);
			groupList.add(councilReviewers);
			Group gsAuthors = (Group) manager.findByHome(homePath + "/" + girlscoutsPath + "/" + allAuthorsGroup);
			gsAuthors.addMember(councilAuthors);
			Group gsReviewers = (Group) manager.findByHome(homePath + "/" + girlscoutsPath + "/" + allReviewersGroup);
			gsReviewers.addMember(councilReviewers);
			
			String authorsProfilePath = councilAuthors.getProfile().getPath();
			session.getNode(authorsProfilePath).setProperty("givenName", councilTitle + " Authors");
			String reviewersProfilePath = councilReviewers.getProfile().getPath();
			session.getNode(reviewersProfilePath).setProperty("givenName", councilTitle + " Reviewers");
			
			buildPermissions(session, councilName, councilAuthors);
			buildPermissions(session, councilName, councilReviewers);
			
			session.save();
		} catch (Exception e) {
			LOG.error("Error occurred during council Group creation" + e.toString());
		}
		return groupList;
	}

	private void buildPermissions(Session session, String councilName, Group councilGroup) {
		final String AUTHORS = councilName + "-authors";
		final String REVIEWERS = councilName + "-reviewers";

		try {
			JackrabbitSession jackSession = (JackrabbitSession) session;
			JackrabbitAccessControlManager acm = (JackrabbitAccessControlManager) session.getAccessControlManager();
			List<JackrabbitAccessControlList> aclList = new ArrayList<JackrabbitAccessControlList>();
			org.apache.jackrabbit.api.security.user.UserManager manager = jackSession.getUserManager();
			//Converting Group to a Principal to be used as an argument for permission creation
			Principal principal = manager.getAuthorizable(councilGroup.getID()).getPrincipal();			
			String groupName = principal.getName();

			if(groupName.equals(AUTHORS)) {
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName, "READ_WRITE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en", "MODIFY", "*/jcr:content*"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam/" + councilName, "READ_WRITE_MODIFY_REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/tags/" + councilName, "READ_WRITE_MODIFY_REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/scaffolding/" + councilName, "READ"), acm, session).getPrivilegeList());
			}
			if(groupName.equals(REVIEWERS)) {
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName, "READ_WRITE_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en", "MODIFY", "*/jcr:content*"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam/" + councilName, "READ_WRITE_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/tags/" + councilName, "READ_WRITE_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/scaffolding/" + councilName, "READ"), acm, session).getPrivilegeList());			
			}
			//Policies are all generated into a list and for loop binds policies to their respective nodes
			for(JackrabbitAccessControlList l: aclList) {
					acm.setPolicy(l.getPath(), l);
			}
			
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	class Rule {
		Principal principal;
		String contentPath;
		String permission;
		String glob;
		
		Rule(Principal principal, String contentPath, String permission) {
			this.principal = principal;
			this.contentPath = contentPath;
			this.permission = permission;
			this.glob = null;
		}
		
		Rule(Principal principal, String contentPath, String permission, String glob) {
			this.principal = principal;
			this.contentPath = contentPath;
			this.permission = permission;
			this.glob = glob;
		}
	}
		
	class PermissionsSetter {
		Rule rule;
		JackrabbitAccessControlManager manager;
		Session session;
		
		PermissionsSetter(Rule rule, JackrabbitAccessControlManager manager, Session session) {
			this.rule = rule;
			this.manager = manager;
			this.session = session;
		}
		
		private JackrabbitAccessControlList getPrivilegeList(){
			Map<String, Privilege[]> privilegesMap = new HashMap<String, Privilege[]>();
			JackrabbitAccessControlPolicy jacp = null;

			privilegesMap = setPrivilegesMap(manager);

			try {
			if(privilegesMap.get(this.rule.permission) != null) {
				Privilege[] privileges = privilegesMap.get(this.rule.permission); 			
			    try {
				jacp = (JackrabbitAccessControlPolicy) this.manager.getApplicablePolicies(this.rule.contentPath).nextAccessControlPolicy();
			    } catch (NoSuchElementException e) {
			    	jacp = (JackrabbitAccessControlPolicy) this.manager.getPolicies(this.rule.contentPath)[0];
			    }
				if (this.rule.glob != null) {
					Map<String, Value> restrictions = new HashMap<String, Value>();
					ValueFactory vf = session.getValueFactory();
					restrictions.put("rep:glob", vf.createValue(this.rule.glob));
					((JackrabbitAccessControlList) jacp).addEntry(this.rule.principal, privileges, true, restrictions);
				} else if (this.rule.glob == null) {
					((JackrabbitAccessControlList) jacp).addEntry(this.rule.principal, privileges, true);	
				}	
				
			} else {
				LOG.error("Privilege(s) not found for given Permission rule " + this.rule.permission);
				throw new NullPointerException();			
				}			
			} catch (NullPointerException e) {
				LOG.error("Null Pointer Exception thrown: " + e.toString());			
			} catch (ClassCastException e) {
				LOG.error("Key of wrong type submitted for privilegeMap: " + e.toString());			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (JackrabbitAccessControlList) jacp;
	}
		
		private Map<String, Privilege[]> setPrivilegesMap(JackrabbitAccessControlManager manager) {
			Map<String, Privilege[]> map = new HashMap<String, Privilege[]>();
			
			try {
			map.put("READ", new Privilege[]{manager.privilegeFromName(Privilege.JCR_READ)});
			map.put("MODIFY", new Privilege[]{manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_REMOVE_NODE)});
			map.put("READ_WRITE", new Privilege[]{manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT)});
			map.put("READ_WRITE_REPLICATE_DELETE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_WRITE)});
			map.put("READ_WRITE_MODIFY_REPLICATE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT)});
			//map.put("READ_WRITE_MODIFY_REPLICATE_DELETE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT)});
			} catch (RepositoryException e) {
				LOG.error("Error occurred while generating privileges in Privilege Map: " + e.toString());
			}			
			return map;			
		}
		
	}
	
	private HashMap<String, String> setLangPropertyMap(String path, String langAbbrev) {
		HashMap<String, String> propertyMap = new HashMap<String, String>();

		try {
			propertyMap.put("cq:designPath", "/etc/designs/girlscouts-usa-green");
			propertyMap.put("adsPath", path + "/" + langAbbrev + "/ad-page");
			propertyMap.put("calendarPath", path + "/" + langAbbrev + "/event-calendar");
			propertyMap.put("eventLanding", path + "/" + langAbbrev	+ "/events/event-list");
			propertyMap.put("eventPath", path + "/" + langAbbrev + "/events-repository");
			propertyMap.put("footerTracking", "<script src=\"https://www.girlscouts.org/includes/join/council_ebiz_conversion_include.js\"></script>");
			propertyMap.put("globalLanding", path + "/" + langAbbrev + "/site-search");
			propertyMap.put("headerImagePath", "");
			propertyMap.put("hideSignIn", "false");
			propertyMap.put("hideVTKButton", "false");
			propertyMap.put("leftNavRoot", path + "/" + langAbbrev + "/events/event-list");
			propertyMap.put("locale", "America/New_York");
			propertyMap.put("locationsPath", path + "/" + langAbbrev + "/location");
			propertyMap.put("newsPath", path + "/" + langAbbrev + "/our-council/news");
			propertyMap.put("timezone", "US/Eastern");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return propertyMap;
	}

	private Node buildFolder(Node node, String folderName, String folderTitle, String primaryType, boolean hasJcrNode) {
		Node folderNode = null;

		try {
			folderNode = node.addNode(folderName, primaryType);
			
			if (hasJcrNode) {
				Node jcrNode = folderNode.addNode("jcr:content", "nt:unstructured");

				if (folderTitle == null || folderTitle == "") {
					jcrNode.setProperty("jcr:title", folderName);
				} else {
					jcrNode.setProperty("jcr:title", folderTitle);
				}
			}
		} catch (ItemExistsException e) {
			LOG.error("Folder/Node already exists, cannot create folder. " + e.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return folderNode;
	}

	private Page buildPage(PageManager manager, Session session, String path, String title, String seoTitle, String pageName, String template, String resourceType, HashMap<String, String> propertyMap) {
		Page returnPage = null;

		try {
			returnPage = manager.create(path, pageName, template, title);
			Node jcrNode = session.getNode(returnPage.getPath() + "/jcr:content");
			// Set sling:resourceType of the jcr:content of HomePage node
			if (resourceType != null && resourceType != "") {
				jcrNode.setProperty("sling:resourceType", resourceType);
			}
			if (seoTitle != null && seoTitle != "") {
				jcrNode.setProperty("seoTitle", seoTitle);
			}
			if (!propertyMap.isEmpty() && propertyMap != null) {
				for (Entry<String, String> map : propertyMap.entrySet()) {
					jcrNode.setProperty(map.getKey(), map.getValue());
				}
			}
		} catch (WCMException e) {
			LOG.error("Cannot build page titled " + title + " " + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnPage;
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

	private Node buildCSSFile(Node folderNode, String councilName, String councilTitle, String mimeType, String encoding) {
		Node CSSNode = null;

		try {
			File CSSFile = buildFile(councilName + ".css", "/* COUNCIL-SPECIFIC STYLING FOR " + councilTitle + " */");
			CSSNode = folderNode.addNode(CSSFile.getName(), "nt:file");
			//Setting data for jcr:content of css file
			Node resNode = CSSNode.addNode("jcr:content", "nt:resource");
			resNode.setProperty("jcr:mimeType", mimeType);
			resNode.setProperty("jcr:encoding", encoding);
			resNode.setProperty("jcr:data", new FileInputStream(CSSFile));
			Calendar lastModified = Calendar.getInstance();
			lastModified.setTimeInMillis(CSSFile.lastModified());
			resNode.setProperty("jcr:lastModified", lastModified);
		} catch (RepositoryException e) {
			LOG.error("Error with repository: " + e.toString());
		} catch (FileNotFoundException e) {
			LOG.error("CSS File not found: " + e.toString());
		} catch (Exception e) {
			LOG.error("Error occurred while building CSS File: " + e.toString());
		}
		return CSSNode;
	}

	private File buildFile(String fileName, String text) {
		File file = null;
		
		try {
			file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();
		} catch (Exception e) {
			LOG.error("Error occurred while building generic File: " + e.toString());
		}
		return file;
	}
}