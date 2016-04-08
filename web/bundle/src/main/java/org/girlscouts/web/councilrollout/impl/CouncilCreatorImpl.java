package org.girlscouts.web.councilrollout.impl;

import java.io.StringBufferInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.Query;
import javax.jcr.security.Privilege;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.CouncilCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Jackrabbit User APIs
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Authorizable;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;

@Component
@Service(value = CouncilCreator.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.councilcreator", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts council rollout service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })

public class CouncilCreatorImpl implements CouncilCreator {
	private static Logger LOG = LoggerFactory.getLogger(CouncilCreatorImpl.class);
	
	@Reference
    private LiveRelationshipManager relationshipManager;

	@Reference
	private RolloutManager rolloutMgr;
	/**
	 * Creates the layout of the site (national pages)
	 * 
	 * @param  contentPath  path leading up to council root, e.g. "/content/"
	 * @param  councilName  the domain name of the council
	 * @param  councilTitle  the full name of the council
	 * @return a list containing all pages that were created
	 */
	public List<Page> generateSite(Session session, ResourceResolver rr, String contentPath, String councilName, String councilTitle) {
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		String councilPath = contentPath + "/" + councilName;
		try {
			Node contentNode = session.getNode(contentPath);
			
			if (!contentNode.hasNode(councilName)) {
				PageManager pageManager = (PageManager) rr.adaptTo(PageManager.class);
				// Create Council HomePage
			
				pages.add(buildPage(pageManager, session, contentPath, councilTitle, null, councilName, "", "foundation/components/page", null));
				propertyMap = setLangPropertyMap(councilPath, "en", councilName);
				Page englishPage = buildPage(pageManager, session, councilPath, councilTitle, null, "en", "","girlscouts/components/homepage", propertyMap);

				String languagePath = englishPage.getPath();

				pages.add(englishPage);
				pages.addAll(buildLiveCopyPages(pageManager, rr, contentNode, contentPath, "girlscouts-template", councilPath, "en"));
				pages.add(buildPage(pageManager, session, languagePath, "Events", null, "events", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/placeholder-page", null));
				pages.add(buildPage(pageManager, session, languagePath + "/events", "Event List", null, "event-list", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/three-column-page", null));
				pages.add(buildPage(pageManager, session, languagePath + "/events", "Event Calendar", null, "event-calendar", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/three-column-page", null));
				pages.add(buildPage(pageManager, session, languagePath, "Camp", null, "camp", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/placeholder-page", null));
				pages.add(buildPage(pageManager, session, languagePath, "Our Council", null, "our-council", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/placeholder-page", null));
				pages.add(buildPage(pageManager, session, languagePath + "/our-council", "News", null, "news", "/apps/girlscouts/templates/three-column-page", "girlscouts/components/three-column-page", null));
				pages.add(buildContactPage(pageManager, session, languagePath + "/our-council", councilTitle, councilName));
				pages.add(buildThankYouPage(pageManager, session, languagePath + "/our-council/contact-us", councilTitle));
				pages.add(buildWebToCasePage(pageManager, session, languagePath, councilTitle));
				pages.add(buildThankYouPage(pageManager, session, languagePath + "/our-council/web-to-case", councilTitle));
				pages.add(buildPage(pageManager, session, languagePath, "Ad Page", null, "ad-page", "", "girlscouts/components/ad-list-page", null));
				pages.add(buildPage(pageManager, session, languagePath, "Search | " + councilTitle, "Search | " + councilTitle, "site-search", "", "girlscouts/components/three-column-page", null));
				pages.add(buildPage(pageManager, session, languagePath, "Map", null, "map", "", "girlscouts/components/map", null));
				pages.add(buildPage(pageManager, session, languagePath, "404", null, "404", "", "girlscouts/components/error-page-404", null));
				pages.add(buildRepositoryPage(pageManager, session, languagePath, "events-repository", "", "Events Repository"));
				pages.add(buildRepositoryPage(pageManager, session, languagePath, "contacts", "", "Contacts"));
				pages.add(buildRepositoryPage(pageManager, session, languagePath, "milestones", "", "Milestones"));
				
				Node enJcrContentNode = rr.getResource(councilPath + "/en/jcr:content").adaptTo(Node.class);
				Node enContentNode = enJcrContentNode.addNode("content", "nt:unstructured");
				Node subParNode = enContentNode.addNode("styled-subpar");
				subParNode.setProperty("cssClasses", "row home-section");
				Node breakingNewsNode = enContentNode.addNode("breaking-news");
				breakingNewsNode.setProperty("sling:resourceType", "girlscouts/components/breaking-news");
				breakingNewsNode.setProperty("newstype","None");
				
				Node enHeaderNode = enJcrContentNode.addNode("header", "nt:unstructured");
				Node globalNavNode = enHeaderNode.addNode("global-nav");
				globalNavNode.setProperty("links", new String[]{
						"ABOUT GIRL SCOUTS|||" + languagePath + "/about-girl-scouts||||||ABOUT GIRL SCOUTS|||ABOUT GIRL SCOUTS", 
						"EVENTS|||" + languagePath + "/events||||||EVENTS|||EVENTS", 
						"COOKIES|||" + languagePath + "/cookies||||||COOKIES|||COOKIES", 
						"VOLUNTEER|||" + languagePath + "/for-volunteers||||||VOLUNTEER|||VOLUNTEER", 
						"CAMP|||" + languagePath + "/camp||||||CAMP|||CAMP", 
						"OUR COUNCIL|||" + languagePath + "/our-council||||||OUR COUNCIL|||OUR COUNCIL"
						});
				
				Node searchBoxNode = enHeaderNode.addNode("search-box");
				searchBoxNode.setProperty("searchAction", "globalLanding");
				searchBoxNode.setProperty("sling:resourceType", "girlscouts/components/search-box");

				//session.save();
			}
			else {
				LOG.error("Council Pages already exist.");
				throw new Exception();
			}
		
		} catch (Exception e) {
		    	LOG.error("Error occurred during Page Building Procedure: " + e.toString());
		    	e.printStackTrace();
		}
		return pages;
	}

	/**
	 * Creates site scaffolding and sets target paths
	 * 
	 * @param  councilName  the full name of the council
	 * @return a list containing the scaffolding nodes that were created
	 */
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
			//session.save();
			
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

	/**
	 * Creates DAM folder and subfolders for the site TODO: Images
	 * 
	 * @param  path  path to the parent directory e.g. "/content/dam"
	 * @param  councilName  the domain name of the council
	 * @param  councilTitle  the full name of the council
	 * @return a list containing the folder nodes that were created
	 */
	public List<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle) {
		ArrayList<Node> damNodes = new ArrayList<Node>();

		try {
			Node damNode = session.getNode(path + "/" + "dam");
			Node councilNode = buildFolder(damNode, "girlscouts-" + councilName, councilTitle, "sling:OrderedFolder", true);
			damNodes.add(councilNode);
			damNodes.add(buildFolder(councilNode, "documents", "Forms and Documents", "sling:OrderedFolder", true));
			Node imageNode = buildFolder(councilNode, "images", "Images", "sling:OrderedFolder", true);
			damNodes.add(imageNode);
			
			Node bannersNode = buildFolder(imageNode, "banners", "Banners", "sling:OrderedFolder", true);
			damNodes.add(bannersNode);
			Node bannersLarge = buildFolder(bannersNode, "large", "Large", "sling:OrderedFolder", true);
			damNodes.add(bannersLarge);
			Node bannersMedium = buildFolder(bannersNode, "medium", "Medium", "sling:OrderedFolder", true);
			damNodes.add(bannersMedium);
			Node bannersSmall = buildFolder(bannersNode, "small", "Small", "sling:OrderedFolder", true);
			damNodes.add(bannersSmall);
			
			Node logoNode = buildFolder(imageNode, "logo", "Logos", "sling:OrderedFolder", true);
			damNodes.add(logoNode);
			Node logoLarge = buildFolder(logoNode, "large", "Large", "sling:OrderedFolder", true);
			damNodes.add(logoLarge);
			Node logoMedium = buildFolder(logoNode, "medium", "Medium", "sling:OrderedFolder", true);
			damNodes.add(logoMedium);
			Node logoSmall = buildFolder(logoNode, "small", "Small", "sling:OrderedFolder", true);
			damNodes.add(logoSmall);
			
			//session.save();
			
		} catch (PathNotFoundException e) {
			LOG.error("Provided path is not correct" + e.toString());
		} catch (Exception e) {
			LOG.error("Unable to create DAM Folders with stack trace : " + e.toString());
		}
		return damNodes;
	}

	/**
	 * Creates tag folder and categories
	 * 
	 * @param  councilName  the full name of the council
	 * @param  councilTitle the domain of the council
	 * @return a list containing the nodes that were created
	 */
	public List<Tag> generateTags(Session session, ResourceResolver rr, String councilName, String councilTitle) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		final String tagPath = "/etc/tags";

		try {
			TagManager manager = (TagManager) rr.adaptTo(TagManager.class);
			tags.add(manager.createTag(tagPath + "/" + councilName, councilTitle, ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "categories", "Categories", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "program-level", "Program Level", ""));
			tags.add(manager.createTag(tagPath + "/" + councilName + "/" + "forms_documents", "Forms & Documents", ""));
			//session.save();

		} catch (InvalidTagFormatException e) {
			LOG.error("Unable to create Tag with correct format: " + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tags;
	}

	/**
	 * Creates design folder, CSS file, favicon, and content
	 * 
	 * @param  councilName  the full name of the council
	 * @param  councilTitle the domain of the council
	 * @return a list containing the nodes that were created
	 */
	public List<Node> generateDesign(Session session, ResourceResolver rr, String councilName, String councilTitle) {
		ArrayList<Node> design = new ArrayList<Node>();
		final String designPath = "/etc/designs";
		final String designPrototypePath = "girlscouts-prototype";

		try {
			Node designsFolder = session.getNode(designPath);
			if (designsFolder.hasNode(designPrototypePath)) {
				Node prototypeDesign = designsFolder.getNode(designPrototypePath);
				Node councilDesign = JcrUtil.copy(prototypeDesign, designsFolder, "girlscouts-" + councilName); 
				StringBufferInputStream stream = new StringBufferInputStream("/* COUNCIL-SPECIFIC STYLING FOR " + councilTitle + " */");
				Calendar date = Calendar.getInstance();
				JcrUtils.putFile(councilDesign, councilName + ".css", "text/css", stream, date);
				design.add(councilDesign);
				councilDesign.getNode("jcr:content").setProperty("jcr:title", councilTitle);

			} else {
				LOG.error("design prototype folder not found");
				throw new PathNotFoundException();
			}
			//session.save();
			
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

	/**
	 * Creates user groups and sets inheritance
	 * 
	 * @param  councilName  the full name of the council
	 * @param  councilTitle the domain of the council
	 * @return a list containing the user groups that were created
	 */
	public List<String> generateGroups(Session session, final String councilName, String councilTitle) {
		ArrayList<String> groupList = new ArrayList<String>();
		final String homePath = "/home/groups";
		final String girlscoutsPath = "girlscouts-usa";
		final String allAuthorsGroup = "gs-authors";
		final String allReviewersGroup = "gs-reviewers";
		try {
			UserManager userManager = ((JackrabbitSession) session).getUserManager();
			Principal principalAuthors = new Principal() {
		        public String getName() {
		          return councilName + "-authors";
		        }
		    };
		    Principal principalReviewers = new Principal() {
		        public String getName() {
		          return councilName + "-reviewers";
		        }
		    };
			Group councilAuthors = userManager.createGroup(principalAuthors, homePath + "/" + councilName);
			Group councilReviewers = userManager.createGroup(principalReviewers, homePath + "/" + councilName);
			
			if(userManager.getAuthorizableByPath(homePath + "/" + girlscoutsPath + "/" + allAuthorsGroup) != null && userManager.getAuthorizableByPath(homePath + "/" + girlscoutsPath + "/"  + allReviewersGroup) != null){
				groupList.add("\"" + principalAuthors.getName() + "\"" + "group created under path:\n" + councilAuthors.getPath());
				groupList.add("\"" + principalReviewers.getName() + "\"" + "gorup created under path:\n" + councilReviewers.getPath());		
				Group gsAuthors = (Group) userManager.getAuthorizable(allAuthorsGroup);
				Group gsReviewers = (Group) userManager.getAuthorizable(allReviewersGroup);
				gsAuthors.addMember(councilAuthors);
				gsReviewers.addMember(councilReviewers);
			}
			else {
				groupList.add("None");
				LOG.error(allAuthorsGroup + " or " + allReviewersGroup + " not found."); 
				throw new PathNotFoundException();
			}
			
			//Titles of council Groups set
			String authorsProfilePath = councilAuthors.getPath();
			session.getNode(authorsProfilePath).setProperty("givenName", councilTitle + " Authors");
			String reviewersProfilePath = councilReviewers.getPath();
			session.getNode(reviewersProfilePath).setProperty("givenName", councilTitle + " Reviewers");
			
			//Permissions for council Group are generated here
			buildPermissions(session, councilName, councilAuthors);
			buildPermissions(session, councilName, councilReviewers);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupList;
	}

	/**
	 * Sets council specific user group permissions
	 * 
	 * @param  councilName  domain of the council
	 * @param  councilGroup  the group whose permissions are being set
	 */
	private void buildPermissions(Session session, String councilName, Group councilGroup) {
		final String AUTHORS = councilName + "-authors";
		final String REVIEWERS = councilName + "-reviewers";

		try {
			JackrabbitSession jackSession = (JackrabbitSession) session;
			JackrabbitAccessControlManager acm = (JackrabbitAccessControlManager) session.getAccessControlManager();
			List<JackrabbitAccessControlList> aclList = new ArrayList<JackrabbitAccessControlList>();

			Principal principal = councilGroup.getPrincipal();	
			String groupName = principal.getName();

			if(councilGroup.getID().equals(AUTHORS)) {
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName, "READ_WRITE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en", "MODIFY", "*/jcr:content*"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en/ad-page", "REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en/contacts", "REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en/events-repository", "REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en/milestones", "REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam/girlscouts-" + councilName, "READ_WRITE_MODIFY_REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/tags/" + councilName, "READ_WRITE_MODIFY_REPLICATE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/scaffolding/" + councilName, "READ"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/designs/" + councilName, "READ"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en/our-council/news", "REPLICATE"), acm, session).getPrivilegeList());
			}
			if(councilGroup.getID().equals(REVIEWERS)) {
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName, "READ_WRITE_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/" + councilName + "/en", "MODIFY", "*/jcr:content*"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam/girlscouts-" + councilName, "READ_WRITE_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/tags/" + councilName, "READ_WRITE_MODIFY_REPLICATE_DELETE"), acm, session).getPrivilegeList());
				aclList.add(new PermissionsSetter(new Rule(principal, "/etc/designs/" + councilName, "READ"), acm, session).getPrivilegeList());
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
		
		/**
		 * Used to set permissions for a user group. Used by buildPermissions()
		 * 
		 * @param  rule  a specific rule for a specific user/group
		 * @return a list of rules for the user/group
		 */
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
		
		/**
		 * Generates the privileges map used for getPrivilegesList()
		 * 
		 * @return a map of privileges for a user group
		 */
		private Map<String, Privilege[]> setPrivilegesMap(JackrabbitAccessControlManager manager) {
			Map<String, Privilege[]> map = new HashMap<String, Privilege[]>();
			
			try {
				map.put("READ", new Privilege[]{manager.privilegeFromName(Privilege.JCR_READ)});
				map.put("MODIFY", new Privilege[]{manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_REMOVE_NODE)});
			    map.put("REPLICATE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE)});
				map.put("READ_WRITE", new Privilege[]{manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT)});
				map.put("READ_WRITE_REPLICATE_DELETE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_WRITE), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT)});
			    map.put("READ_WRITE_MODIFY_REPLICATE", new Privilege[]{manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), manager.privilegeFromName(Privilege.JCR_READ), manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT)});
			
			} catch (RepositoryException e) {
				LOG.error("Error occurred while generating privileges in Privilege Map: " + e.toString());
			}			
			return map;			
		}
		
	}
	
	/**
	 * Sets page properties for lang page (e.g. en)
	 * 
	 * @param  path  a path to the lang page (e.g. en)
	 * @param  langAbbrev  used to create path to resources
	 * @return a map of the properties set by this method
	 */
	private HashMap<String, String> setLangPropertyMap(String path, String langAbbrev, String councilDomain) {
		HashMap<String, String> propertyMap = new HashMap<String, String>();

		try {
			propertyMap.put("cq:designPath", "/etc/designs/girlscouts-" + councilDomain);
			propertyMap.put("adsPath", path + "/" + langAbbrev + "/ad-page");
			propertyMap.put("calendarPath", path + "/" + langAbbrev + "/events/event-calendar");
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

	/**
	 * Creates a folder in the jcr and a jcr:content node if needed
	 * 
	 * @param  folderName  The name used in the path to the folder
	 * @param  folderTitle  The visible title of the folder
	 * @return the newly created folder
	 */
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

	/**
	 * Creates a page in the jcr
	 * 
	 * @param  path  a path to the page to be created
	 * @param  title  the visible title not used for the file path
	 * @param  seoTitle  the seoTitle property (when used)
	 * @param  pageName  the name used in the file path
	 * @param  template  path to the template being used (e.g. three-Column Page). Shows up in siteadmin
	 * @param  resourceType  the page's component type (e.g. homepage, placeholder-page)
	 * @return the newly created page
	 */
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
			if (propertyMap != null && !propertyMap.isEmpty()  ) {
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
	
	/**
	 * Creates the contact us page with it's form and properties
	 * @param  path  path to the page's directory
	 * @param  councilTitle  full name of the council
	 * @param  councilName  domain name of the council
	 * @return the newly created contact us page
	 */
	private Page buildContactPage(PageManager manager, Session session, String path, String councilTitle, String councilName){
		Page returnPage = null;
		
		try {
			returnPage = manager.create(path, "contact-us", "/apps/girlscouts/templates/three-column-page", "Contact Us");
			Node jcrNode = session.getNode(returnPage.getPath() + "/jcr:content");
			jcrNode.setProperty("sling:resourceType", "girlscouts/components/three-column-page");
			jcrNode.setProperty("seoTitle", "Contact Us | " + councilTitle);
			
			Node contentNode = jcrNode.addNode("content");
			contentNode.setPrimaryType("nt:unstructured");
			
			Node middleNode = contentNode.addNode("middle");
			middleNode.setPrimaryType("nt:unstructured");
			
			Node parNode = middleNode.addNode("par");
			parNode.setPrimaryType("nt:unstructured");
			parNode.setProperty("sling:resourceType", "foundation/components/parsys");
			
			Node formStartNode = parNode.addNode("form_start");
			formStartNode.setProperty("actionType","foundation/components/form/actions/mail");
			formStartNode.setProperty("formid","contact-us");
			formStartNode.setProperty("from","placeholder@"+councilName+".org");
			formStartNode.setProperty("mailto","placeholder@"+councilName+".org");
			formStartNode.setProperty("sling:resourceType", "foundation/components/form/start");
			formStartNode.setProperty("redirect", path + "/contact-us/thank-you");
			formStartNode.setProperty("subject", "Contact Form");
			
			Node titleNode = parNode.addNode("title");
			titleNode.setPrimaryType("nt:unstructured");
			titleNode.setProperty("sling:resourceType", "girlscouts/components/title");
			
			Node text3Node = parNode.addNode("text_3");
			text3Node.setPrimaryType("nt:unstructured");
			text3Node.setProperty("sling:resourceType", "girlscouts/components/text");
			text3Node.setProperty("text", "<p><b>We'd love to hear from you.</b></p>");
			text3Node.setProperty("textIsRich", "true");
			
			Node textNode = parNode.addNode("text");
			textNode.setProperty("constraintType", "foundation/components/form/constraints/name");
			textNode.setPrimaryType("nt:unstructured");
			textNode.setProperty("jcr:title", "Name");
			textNode.setProperty("name", "name");
			textNode.setProperty("required", true);
			textNode.setProperty("requiredMessage", "Your name is required");
			textNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			textNode.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node text0Node = parNode.addNode("text_0");
			text0Node.setProperty("constraintType", "foundation/components/form/constraint/email");
			text0Node.setPrimaryType("nt:unstructured");
			text0Node.setProperty("jcr:title", "Email");
			text0Node.setProperty("name", "email");
			text0Node.setProperty("required", true);
			text0Node.setProperty("requiredMessage", "Your email address is required");
			text0Node.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			text0Node.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node text2Node = parNode.addNode("text_2");
			text2Node.setProperty("constraintType", "foundation/components/form/constraints/numeric");
			text2Node.setProperty("jcr:description", "Please enter phone number in the following format: 5555555555");
			text2Node.setPrimaryType("nt:unstructured");
			text2Node.setProperty("jcr:title", "Phone");
			text2Node.setProperty("name", "phone");
			text2Node.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			text2Node.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node text5Node = parNode.addNode("text_5");
			text5Node.setProperty("constraintMessage", "Please enter a valid 5-digit Zip Code.");
			text5Node.setProperty("constraintType", "foundation/components/form/constraints/numeric");
			text5Node.setPrimaryType("nt:unstructured");
			text5Node.setProperty("jcr:title", "ZIP Code");
			text5Node.setProperty("name", "zipcode");
			text5Node.setProperty("required", true);
			text5Node.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			text5Node.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node captchaNode = parNode.addNode("Captcha");
			captchaNode.setProperty("constraintMessage", "Invalid Captcha");
			captchaNode.setProperty("jcr:description", "(Please type verification code in the box above. Click Refresh to get new code)");
			captchaNode.setPrimaryType("nt:unstructured");
			captchaNode.setProperty("jcr:title", "Verification Code");
			captchaNode.setProperty("required", true);
			captchaNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			captchaNode.setProperty("sling:resourceType", "girlscouts/components/form/captcha");
			
			Node text1Node = parNode.addNode("text_1");
			text1Node.setPrimaryType("nt:unstructured");
			text1Node.setProperty("jcr:title", "Comments");
			text1Node.setProperty("name", "comments");
			text1Node.setProperty("rows", 4);
			text1Node.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			text1Node.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node submitNode = parNode.addNode("submit");
			submitNode.setProperty("css", "form-btn");
			submitNode.setPrimaryType("nt:unstructured");
			submitNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			submitNode.setProperty("sling:resourceType", "foundation/components/form/submit");
			
			Node formEndNode = parNode.addNode("form_end_1395155284038");
			formEndNode.setPrimaryType("nt:unstructured");
			formEndNode.setProperty("sling:resourceType", "foundation/components/form/end");
			
		} catch (WCMException e) {
			LOG.error("Cannot build contact us page: \n" +  e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnPage;
	}

	/**
	 * Creates a page in the jcr
	 * Specifically used for pages that won't be visible to users, and are just used for hierarchical purposes
	 * 
	 * @param  languagePath  used to create page path
	 * @param  pageName  the name used in the file path
	 * @param  template  path to the template being used (e.g. three-Column Page). Shows up in siteadmin
	 * @param  title  visible page name
	 * @return the newly created page
	 */
	private Page buildRepositoryPage(PageManager manager, Session session, String languagePath, String pageName, String template, String title) {
		Page thisRepositoryPage = null;

		try {
			thisRepositoryPage = manager.create(languagePath, pageName, template, title);
		} catch (Exception e) {
			LOG.error("Cannot build Repository page titled " + title + " " + e.toString());
		}
		return thisRepositoryPage;
	}
	
	/**
	 * Creates all live copies of existing (national) pages
	 * 
	 * @param  rootNode  the root node, in this case located at "/content"
	 * @param  pageName  path to root node
	 * @param  templatePath  relative path from "/content" to the page that is being used as a template (e.g. "girlscouts-template")
	 * @param  councilPath  path to the council that contains the copies
	 * @param  languagePath  relative path from councilPath to lang page "e.g. en"
	 * @return the newly created page
	 */
	private List<Page> buildLiveCopyPages(PageManager manager,  ResourceResolver rr, Node rootNode, String contentPath, String templatePath, String councilPath, String languagePath) {
		ArrayList<Page> copyPages = new ArrayList<Page>();
		final String templateLangPath = templatePath + "/" + languagePath;
//		LiveRelationshipManager relationshipMgr = (LiveRelationshipManager) rr.adaptTo(LiveRelationshipManager.class);
		RolloutConfigManager configMgr = (RolloutConfigManager) rr.adaptTo(RolloutConfigManager.class);
		
		try {			
			if (rootNode.hasNode(templateLangPath)) {
				Resource languageRootRes = rr.resolve(contentPath + "/" + templateLangPath);
				Iterator<Resource> pageItr = languageRootRes.listChildren();
				while (pageItr.hasNext()) {
					Resource childRes = pageItr.next();
					if (childRes.getName().equals("jcr:content")) {
						
					} 
					else {
						String templatePageName = childRes.getName();
						Page templatePage = (Page) languageRootRes.getChild(templatePageName).adaptTo(Page.class);
						Page copyPage = manager.copy(templatePage, councilPath + "/" + languagePath + "/" + templatePageName, templatePageName, false, true);
						copyPages.add(copyPage);
						RolloutConfig gsConfig = configMgr.getRolloutConfig("/etc/msm/rolloutconfigs/gsdefault");
						LiveRelationship relationship= relationshipManager.establishRelationship(templatePage, copyPage, true, false, gsConfig);
						cancelInheritance(rr, councilPath + "/" + languagePath);
						//GSWP-77 c.w.
						rollout(rr,copyPage);
						
					}
				}
			}
			else {
				LOG.error(templatePath + " doesn't exist in repository. Cannot create live copies");
				throw new RepositoryException();
			}
		} catch (RepositoryException e) {
			LOG.error("Repository exception thrown during Live Copy Process " + e.toString());
		} catch (Exception e) {
			LOG.error("Error occurred during Live Copy Process " + e.toString());
			e.printStackTrace();
		}
		return copyPages;
	}
	/**
	 * cancel the Inheritance for certain components
	 *  (nodes with mixin type "cq:LiveSyncCancelled" under national template page)
	 */
	private void cancelInheritance(ResourceResolver rr, String councilPath){
		try {
			String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '"
					+ councilPath + "')";
			LOG.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2);it.hasNext();){
				Resource target = it.next();
                relationshipManager.endRelationship(target,true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**Do an initial rollout on the newly created livecopy
	 *  to trigger the gsreferenceupdate action
	 * @throws WCMException 
	 */
	private void rollout(ResourceResolver rr, Page targetPage) throws WCMException {
		Iterator<Page> childIter = targetPage.listChildren(null,true);
		while(childIter.hasNext()){
			Page child = childIter.next();
			LiveRelationship relationship = relationshipManager.getLiveRelationship(child.adaptTo(Resource.class), false);
			if(relationship!=null){
				rolloutMgr.rollout(rr, relationship, false);
			}
		}
		
	}
	/**
	 * Creates the Salesforce Web to Case page with it's form and properties
	 * @param  languagePath  path to the page's /en directory
	 * @param  councilTitle  full name of the council
	 * @return the newly created web to case page
	 * @author cwu
	 */
	private Page buildWebToCasePage(PageManager manager, Session session, String languagePath, String councilTitle){
		Page returnPage = null;
		String path =languagePath + "/our-council";
		try {
			returnPage = manager.create(path, "web-to-case", "/apps/girlscouts/templates/three-column-page", "Contact Us");
			Node jcrNode = session.getNode(returnPage.getPath() + "/jcr:content");
			jcrNode.setProperty("sling:resourceType", "girlscouts/components/three-column-page");
			jcrNode.setProperty("seoTitle", "Contact Us | " + councilTitle);
			
			Node contentNode = jcrNode.addNode("content");
			contentNode.setPrimaryType("nt:unstructured");
			
			Node middleNode = contentNode.addNode("middle");
			middleNode.setPrimaryType("nt:unstructured");
			
			Node parNode = middleNode.addNode("par");
			parNode.setPrimaryType("nt:unstructured");
			parNode.setProperty("sling:resourceType", "foundation/components/parsys");
			
			Node formStartNode = parNode.addNode("web-to-case-form_start");
			formStartNode.setProperty("actionType","girlscouts/components/form/actions/web-to-case");
			formStartNode.setProperty("formid","web-to-case");
			formStartNode.setProperty("cwrw","cw");
			formStartNode.setProperty("formActionURL","https://www.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8");
			formStartNode.setProperty("sling:resourceType", "foundation/components/form/start");
			formStartNode.setProperty("redirect", path + "/web-to-case/thank-you");

			
			Node titleNode = parNode.addNode("title");
			titleNode.setPrimaryType("nt:unstructured");
			titleNode.setProperty("sling:resourceType", "girlscouts/components/title");
			titleNode.setProperty("title","Contact Us");
			
			Node nameNode = parNode.addNode("text");
			nameNode.setPrimaryType("nt:unstructured");
			nameNode.setProperty("jcr:title", "Contact Name");
			nameNode.setProperty("name", "name");
			nameNode.setProperty("required", true);
			nameNode.setProperty("requiredMessage", "Your name is required");
			nameNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			nameNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			nameNode.setProperty("maxlength", 80);

			
			Node emailNode = parNode.addNode("text_0");
			emailNode.setProperty("constraintType", "girlscouts/components/form/constraints/email-no-whitespace");
			emailNode.setPrimaryType("nt:unstructured");
			emailNode.setProperty("jcr:title", "Email");
			emailNode.setProperty("name", "email");
			emailNode.setProperty("required", true);
			emailNode.setProperty("requiredMessage", "Your email address is required");
			emailNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			emailNode.setProperty("sling:resourceType", "foundation/components/form/text");
			
			Node phoneNode = parNode.addNode("text_1");
			phoneNode.setProperty("constraintType", "girlscouts/components/form/constraints/numeric-no-whitespace");
			phoneNode.setProperty("jcr:description", "Please enter phone number in the following format: 5555555555");
			phoneNode.setPrimaryType("nt:unstructured");
			phoneNode.setProperty("jcr:title", "Phone");
			phoneNode.setProperty("name", "phone");
			phoneNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			phoneNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			phoneNode.setProperty("maxlength", 20);

			Node methodNode = parNode.addNode("dropdown");
			methodNode.setProperty("jcr:title","Preferred Method of Contact");
			methodNode.setPrimaryType("nt:unstructured");
			methodNode.setProperty("name","00NG000000DdNmM");
			methodNode.setProperty("options",new String[]{"Phone","E-mail","Either"});
			methodNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			methodNode.setProperty("sling:resourceType", "foundation/components/form/dropdown");
			
			Node timeNode = parNode.addNode("text_2");
			timeNode.setPrimaryType("nt:unstructured");
			timeNode.setProperty("jcr:title", "Best Time To Call");
			timeNode.setProperty("name", "00NG000000DdNmL");
			timeNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			timeNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			timeNode.setProperty("maxlength", 255);

			
			Node zipNode =  parNode.addNode("text_3");
			zipNode.setProperty("constraintType", "girlscouts/components/form/constraints/numeric-no-whitespace");
			zipNode.setProperty("constraintMessage", "Please enter your 5-digit zip code.");
			zipNode.setPrimaryType("nt:unstructured");
			zipNode.setProperty("jcr:title", "Zip Code");
			zipNode.setProperty("name", "00NG000000DdNmN");
			zipNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			zipNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			zipNode.setProperty("maxlength", 5);
			
			Node typeNode = parNode.addNode("dropdown_0");
			typeNode.setPrimaryType("nt:unstructured");
			typeNode.setProperty("jcr:title", "Type");
			typeNode.setProperty("name","type");
			typeNode.setProperty("required", true);
			typeNode.setProperty("requiredMessage", "Please select a type.");
			typeNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			typeNode.setProperty("sling:resourceType", "foundation/components/form/dropdown-no-edit");
			typeNode.setProperty("optionsLoadPath", "/etc/designs/girlscouts/dropdown_data/case_type_list_value/caseTypes");
			
			
			Node subjectNode = parNode.addNode("text_4");
			subjectNode.setPrimaryType("nt:unstructured");
			subjectNode.setProperty("jcr:title", "Subject");
			subjectNode.setProperty("name", "subject");
			subjectNode.setProperty("required", true);
			subjectNode.setProperty("requiredMessage", "A subject is required.");
			subjectNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			subjectNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			subjectNode.setProperty("maxlength", 255);

			
			Node descriptionNode = parNode.addNode("text_5");
			descriptionNode.setPrimaryType("nt:unstructured");
			descriptionNode.setProperty("jcr:title", "Description");
			descriptionNode.setProperty("name", "description");
			descriptionNode.setProperty("rows", 3);
			descriptionNode.setProperty("required", true);
			descriptionNode.setProperty("requiredMessage", "Please add descriptions.");
			descriptionNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			descriptionNode.setProperty("sling:resourceType", "girlscouts/components/form/text");
			descriptionNode.setProperty("maxlength", 32000);
			descriptionNode.setProperty("jcr:description", "Max length: 32000 characters.");


			
			Node submitNode = parNode.addNode("submit");
			submitNode.setProperty("css", "form-btn");
			submitNode.setPrimaryType("nt:unstructured");
			submitNode.setProperty("sling:resourceSuperType", "foundation/components/form/defaults/field");
			submitNode.setProperty("sling:resourceType", "foundation/components/form/submit");
			
			Node formEndNode = parNode.addNode("web-to-case-form_end");
			formEndNode.setPrimaryType("nt:unstructured");
			formEndNode.setProperty("sling:resourceType", "foundation/components/form/end");
			
		} catch (WCMException e) {
			LOG.error("Cannot build contact us page: \n" +  e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnPage;
	}

	/**
	 * Creates the Thank You Page to Accompany the Web-To-Case Page
	 * @param  languagePath  path to the page's /en directory
	 * @param  councilTitle  full name of the council
	 * @return the newly created web to case page
	 * @author dlubin
	 */
	private Page buildThankYouPage(PageManager manager, Session session, String languagePath, String councilTitle){
		Page thanksPage = null;
		String thanksPath = languagePath;
		try {
			thanksPage = manager.create(thanksPath, "thank-you", "/apps/girlscouts/templates/three-column-page", "Thank You");
			Node thanksJcrNode = session.getNode(thanksPage.getPath() + "/jcr:content");
			thanksJcrNode.setProperty("sling:resourceType", "girlscouts/components/three-column-page");
			thanksJcrNode.setProperty("seoTitle", "Thank You | " + councilTitle);
			thanksJcrNode.setProperty("hideInNav", true);
			
			Node thanksContentNode = thanksJcrNode.addNode("content");
			thanksContentNode.setPrimaryType("nt:unstructured");
			
			Node thanksMiddleNode = thanksContentNode.addNode("middle");
			thanksMiddleNode.setPrimaryType("nt:unstructured");
			
			Node thanksParNode = thanksMiddleNode.addNode("par");
			thanksParNode.setPrimaryType("nt:unstructured");
			thanksParNode.setProperty("sling:resourceType", "foundation/components/parsys");
			
			Node thanksTextNode = thanksParNode.addNode("text");
			thanksTextNode.setPrimaryType("nt:unstructured");
			thanksTextNode.setProperty("sling:resourceType", "girlscouts/components/text");
			thanksTextNode.setProperty("text", "<h3>Thank you for contacting us, we have received your inquiry and will be in touch shortly!</h3>");
			thanksTextNode.setProperty("textIsRich", "true");
			
		} catch(Exception e){
			LOG.error("Failed to create Thank You Page: \n" + e.toString());
		}
		
		return thanksPage;
	}
}
