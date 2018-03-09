package org.girlscouts.web.permissions;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.permissions.CounselFolder.PERMISSION;
import org.girlscouts.web.permissions.CounselFolder.ROLE;
import org.girlscouts.web.servlets.JSONOutputServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

@SlingServlet(paths="/bin/utils/counsel_permission_update", methods="GET")
@SuppressWarnings("serial")
public class CounselPermissionUpdateServlet extends JSONOutputServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CounselPermissionUpdateServlet.class);
	
	@Reference
	private QueryBuilder queryBuilder;
	
	@Override
	protected JSONObject getJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		try {
			
			String requestedFolder = request.getParameter("path");
			boolean pathOverrdide = ("true".equals(request.getParameter("pathOverride")));
			
			String reviewerGroupName = request.getParameter("reviewerGroup");
			String authorGroupName = request.getParameter("authorGroup");	
			String counselName = request.getParameter("counsel");
			
			ResourceResolver resourceResolver = request.getResourceResolver();
			Session adminSession = (Session) resourceResolver.adaptTo(Session.class);
			ContentCounselRetriever initialRetriever = new ContentCounselRetriever(adminSession, queryBuilder);
			List<Counsel> counselList = initialRetriever.getCounsels();

			AccessControlManager accessControlManager;
			UserManager userManager;
			Authorizable reviewerGroup, authorGroup;
			try {
				accessControlManager = adminSession.getAccessControlManager();
				userManager = ((JackrabbitSession) adminSession).getUserManager();
				reviewerGroup = userManager.getAuthorizable(reviewerGroupName);
				authorGroup = userManager.getAuthorizable(authorGroupName);
			} catch (RepositoryException e) {
				LOGGER.error("Unable to get permission utilities!", e);
				return new JSONObject();
			}

			CounselFolder targetFolder;
			if(pathOverrdide) {
				String targetFolderPath = initialRetriever.getAvailableDamFolders().stream().map(FolderDTO::getPath).filter(requestedFolder::equals).findFirst().orElse(null);
				targetFolder = new CounselFolder(DIRECTORY_CONFIG.DAM, targetFolderPath, adminSession, Optional.of(reviewerGroup), Optional.of(authorGroup));
				
				// Mark the counsel now that the path has been overriden.
				markCounselDamOverridePath(counselList.stream().filter(counsel -> counsel.getName().equals(counselName)).findFirst().orElse(null), targetFolder.getPath(), adminSession);
			}else {
				targetFolder = counselList
						.stream()
						.filter(cnsl -> counselName.equals(cnsl.getName()))
						.map(Counsel::getCounselFolders)
						.flatMap(List::stream)
						.filter(fldr -> requestedFolder.equals(fldr.getPath()))
						.findFirst()
						.orElseThrow(() -> new ServletException("Cant find requested folder!"));
			}
			
			for(PERMISSION permission : targetFolder.getConfig().getAuthorRights()) {
				if(targetFolder.missingRights(ROLE.AUTHOR, permission)) {
					try {
						addRights(accessControlManager, adminSession, requestedFolder, authorGroup.getPrincipal(), permission);
					} catch (RepositoryException e) {
						LOGGER.error("Unable to add author permissions!", e);
					}
				}
			}
			for(PERMISSION permission : targetFolder.getConfig().getReviewerRights()) {
				if(targetFolder.missingRights(ROLE.REVIEWER, permission)) {
					try {
						addRights(accessControlManager, adminSession, requestedFolder, reviewerGroup.getPrincipal(), permission);
					} catch (RepositoryException e) {
						LOGGER.error("Unable to add reviwer permissions!", e);
					}
				}
			}
			
			// Query back the corrected data.
			ContentCounselRetriever retriever = new ContentCounselRetriever(adminSession, queryBuilder);
			List<CounselDTO> counselDtos = new ArrayList<>();
			for(Counsel counsel : retriever.getCounsels()) {
				LOGGER.info("Found counsel: " + counsel.getName());
				counselDtos.add(new CounselDTO(counsel));
			}
			
			JSONObject returner = new JSONObject();
			returner.put("counsels", counselDtos);
			returner.put("availableFolders", retriever.getAvailableDamFolders());
			return returner;
			
		} catch (CounselPermissionModificationException | JSONException e) {
			LOGGER.error("Unable to modify counsel access controls." + e.getMessage(), e);
			return new JSONObject();
		} 
		
	}
	
	
	private static void markCounselDamOverridePath(Counsel counsel, String path, Session session) {
		try {
			Node counselConfigRootNode;
			try {
				counselConfigRootNode = session.getNode("/etc/counsel-config");
			} catch (RepositoryException e1) {
				Node etcNode = session.getNode("/etc");
				counselConfigRootNode = etcNode.addNode("counsel-config", "sling:OrderedFolder");
			}
			Node counselConfigNode;
			try {
				counselConfigNode = counselConfigRootNode.getNode(counsel.getName());
			} catch (RepositoryException e1) {
				counselConfigNode = counselConfigRootNode.addNode(counsel.getName(), "nt:unstructured");
			}
			
			counselConfigNode.setProperty("overrideDamDirectory", path);
			session.save();
		} catch (RepositoryException e) {
			LOGGER.error("Unable to set overrideDamDirectory", e);
		}
	}

	private static void addRights(AccessControlManager accessControlManager, Session adminSession, String requestedFolder, Principal principal, PERMISSION permission) throws AccessControlException, RepositoryException {
		// Need to add rights.
		AccessControlList acl;
		try {
			acl = AccessControlUtils.getAccessControlList(adminSession, requestedFolder);
		} catch (RepositoryException e) {
			acl = (AccessControlList) accessControlManager.getPolicies(requestedFolder)[0];
		}
		
		Privilege privArr = accessControlManager.privilegeFromName(permission.getJcrPrivilege());
		acl.addAccessControlEntry(principal, new Privilege[] {privArr});
		accessControlManager.setPolicy(requestedFolder, acl);
		adminSession.save();
	}
	
}
