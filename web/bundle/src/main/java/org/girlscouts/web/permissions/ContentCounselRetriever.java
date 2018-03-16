package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.girlscouts.web.permissions.CounselFolder.Permission;
import org.girlscouts.web.permissions.dto.CounselDTO;
import org.girlscouts.web.permissions.dto.FolderDTO;
import org.json.JSONException;
import org.json.JSONObject;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/*
 * Used to get counsel names by grabbing all the nodes under /content with a type of "page".
 */
public class ContentCounselRetriever {
	
	public static final String BASE_COUNSEL_PATH = "/content";
	public static final String BASE_DAM_PATH = "/content/dam";
	public static final String[] IGNORED_DAM_FOLDERS = new String[] {"vtk", "usa", "demo", "we-retail", ":"};
	
	private List<Counsel> counselList = null;
	private List<FolderDTO> availableDamFolders = null;
	private List<FolderDTO> explicitlyDeniedDamFolders = null;
	private String damRootNodeState = null;
	private final Session adminSession;
	private final QueryBuilder queryBuilder;
	
	public ContentCounselRetriever(Session session, QueryBuilder builder) {
		if(session == null) {
			throw new IllegalStateException("No Admin session available!");
		}
		this.adminSession = session;
		this.queryBuilder = builder;
	}
	
	public List<Counsel> getCounsels() throws CounselPermissionModificationException{
		if(counselList == null) {
			try {
				loadCounsels();
			}catch (RepositoryException re) {
				// Empty counsel list so we don't have a partial list or keep blowing up.
				counselList = new ArrayList<Counsel>();
				throw new CounselPermissionModificationException("Couldn't load repository data to get counsel list.", re);
			}
		}
		return counselList;
	}
	
	private void loadCounsels() throws RepositoryException {
		counselList = new ArrayList<Counsel>();

		// Add the special "commonCounsel" for common permissions:
		counselList.add(new CommonCounsel(adminSession));
		
		// Query the rest of the counsels
		Map<String, Object> predicateMap = new HashMap<>();
		predicateMap.put("path", BASE_COUNSEL_PATH);
		predicateMap.put("path.flat", "true");
		predicateMap.put("type", "cq:Page");
				
		com.day.cq.search.Query query = (com.day.cq.search.Query) queryBuilder.createQuery(PredicateGroup.create(predicateMap), adminSession);
		
		query.setHitsPerPage(200);
		SearchResult queryResult = query.getResult();
		
        Iterator<Node> nodes = queryResult.getNodes();
		
        while(nodes.hasNext()) {
        		Node node = nodes.next();
        		String nodeName = node.getName();
        		// Exclude vtk and usa.
        		if(!Stream.of(IGNORED_DAM_FOLDERS).anyMatch(nodeName.toLowerCase()::contains)) {
        			
        			// testing statement.
        			//if(nodeName.equals("gsnetx"))
        			counselList.add(new Counsel(node, adminSession));
        		}
		}
	}
	
	public List<FolderDTO> getExplicitlyDeniedDamFolders() throws CounselPermissionModificationException{
		if(explicitlyDeniedDamFolders == null) {
			try {
				loadExplicitlyDeniedDamFolders();
			}catch (RepositoryException re) {
				explicitlyDeniedDamFolders = new ArrayList<FolderDTO>();
				throw new CounselPermissionModificationException("Couldn't load denied dam folders.", re);
			}
		}
		return explicitlyDeniedDamFolders;
	}
	
	// Returns a list of DAM folders that have a deny explictly set for gs-authors or gs-reviewers.
	private static final String DENIED_DAM_FOLDERS_SQL2 = new StringBuilder()
			.append(" SELECT grandParent.* FROM [rep:DenyACE] AS child ")
			
			.append(" INNER JOIN [rep:ACL] AS parent ")
			.append(" ON ISCHILDNODE(child,parent) ")
			
			.append(" INNER JOIN [sling:Folder] AS grandParent ")
			.append(" ON ISCHILDNODE(parent,grandParent) ")
			
			.append(" WHERE ISCHILDNODE(grandParent, [/content/dam]) ")
				.append(" AND ( ")
					.append(" child.[rep:principalName] = 'gs-authors' ")
					.append(" OR ")
					.append(" child.[rep:principalName] = 'gs-reviewers' ")
				.append(")")
			.toString();
	
	private void loadExplicitlyDeniedDamFolders() throws RepositoryException, CounselPermissionModificationException {
		explicitlyDeniedDamFolders = new ArrayList<>();
		
		QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
		QueryResult result = queryManager.createQuery(DENIED_DAM_FOLDERS_SQL2, Query.JCR_SQL2).execute();

		Iterator<?> nodes = result.getNodes();
        while(nodes.hasNext()) {
        		Node node;
        		Object nodeObj = nodes.next();
        		if(nodeObj instanceof Node) {
        			node = (Node) nodeObj;
        		}else {
        			continue;
        		}
	    		String nodeName = node.getName();
	
	    		// Exclude vtk and usa.
	    		if(!Stream.of(IGNORED_DAM_FOLDERS).anyMatch(nodeName.toLowerCase()::contains)) {
	    			FolderDTO availableFolder = new FolderDTO();
        			availableFolder.setPath(node.getPath());
        			explicitlyDeniedDamFolders.add(availableFolder);
	    		}
        }
		
	}
	
	public String getDamRootDirectoryPermissionStatus() throws CounselPermissionModificationException{
		if(damRootNodeState == null) {
			try {
				loadDamRootDirectoryPermissionStatus();
			}catch (RepositoryException re) {
				damRootNodeState = "None";
				throw new CounselPermissionModificationException("Couldn't Dam Root Node Status.", re);
			}
		}
		return damRootNodeState;
	}
	
	private void loadDamRootDirectoryPermissionStatus() throws PathNotFoundException, RepositoryException {
		
		Node damPolicyNode = adminSession.getNode(BASE_DAM_PATH + "/rep:policy");
		NodeIterator damPolicyNodeIterator = damPolicyNode.getNodes();

		boolean hasExplicitPermission = false, hasExplicitDeny = false;
		
		while(damPolicyNodeIterator.hasNext()) {
			Node damPolicyChild = damPolicyNodeIterator.nextNode();
			if(damPolicyChild.hasProperty("rep:principalName") && damPolicyChild.hasProperty("rep:privileges")) {
				String principalName = damPolicyChild.getProperty("rep:principalName").getString();
				
				// Check to see if it's associated with our common group.
				if(principalName.equals(CommonCounsel.AUTHORS_NAME) || principalName.equals(CommonCounsel.REVIEWERS_NAME)) {
					
					// Check for a read permission:
					for(Value value : Stream.of(damPolicyChild.getProperty("rep:privileges").getValues()).collect(Collectors.toList())) {
						if(Permission.READ.getJcrPrivileges().contains(value.getString())) {
							
							// Found a valid read permission for author / reviewer.  Check the type to see if it's a grant or deny\
							String nodeType = damPolicyChild.getPrimaryNodeType().toString();
							if(nodeType.equals("rep:GrantACE")) {
								hasExplicitPermission = true;
							}else if(nodeType.equals("rep:DenyACE")) {
								hasExplicitDeny = true;
							}
							
							// We found our permission, break out.
							break;
						}
					}
				}
			}
		}
		
		if(hasExplicitPermission && hasExplicitDeny) {
			damRootNodeState = "Both";
		}else if(hasExplicitPermission) {
			damRootNodeState = "Allowed";
		}else if(hasExplicitDeny) {
			damRootNodeState = "Denied";
		}else {
			damRootNodeState = "None";
		}
	}
	
	public List<FolderDTO> getAvailableDamFolders() throws CounselPermissionModificationException{
		if(availableDamFolders == null) {
			try {
				loadAvailableDamFolders();
			}catch (RepositoryException re) {
				// Empty counsel list so we don't have a partial list or keep blowing up.
				availableDamFolders = new ArrayList<FolderDTO>();
				throw new CounselPermissionModificationException("Couldn't load available dam folders.", re);
			}
		}
		return availableDamFolders;
	}
	
	private void loadAvailableDamFolders() throws RepositoryException, CounselPermissionModificationException {
		availableDamFolders = new ArrayList<FolderDTO>();

		Map<String, Object> predicateMap = new HashMap<>();
		predicateMap.put("path", BASE_DAM_PATH);
		predicateMap.put("path.flat", "true");
		predicateMap.put("type", "sling:orderedFolder");
		
		com.day.cq.search.Query query = (com.day.cq.search.Query) queryBuilder.createQuery(PredicateGroup.create(predicateMap), adminSession);
		query.setHitsPerPage(200);
		SearchResult queryResult = query.getResult();
		
		Iterator<Node> nodes = queryResult.getNodes();
        
        // Get a list dam folders used by other counsels to exclude.
        List<String> usedDamFolders = new ArrayList<>();
        for(Counsel counsel : getCounsels()) {
        		for(CounselFolder folder : counsel.getCounselFolders()) {
        			if(folder.getConfig() == DirectoryConfig.DAM) {
        				usedDamFolders.add(folder.getPath());
        			}
        		}
        }
		
        while(nodes.hasNext()) {
        		Node node = nodes.next();
        		String nodeName = node.getName();

        		// Exclude vtk and usa.
        		if(!Stream.of(IGNORED_DAM_FOLDERS).anyMatch(nodeName.toLowerCase()::contains)) {
        			
        			String folderPath = BASE_DAM_PATH + "/" + nodeName;
        			// Exclude folders used by other counsels.
        			if(!usedDamFolders.contains(folderPath)){
	        			FolderDTO availableFolder = new FolderDTO();
	        			availableFolder.setPath(folderPath);
	        			availableDamFolders.add(availableFolder);
        			}
        		}
		}
	}
	
	public JSONObject toJson() throws CounselPermissionModificationException{
		
		JSONObject returner = new JSONObject();
		try {
			List<CounselDTO> counselDtos = new ArrayList<>();
			for(Counsel counsel : getCounsels()) {
				counselDtos.add(new CounselDTO(counsel));
			}
			returner.put("counsels", counselDtos);
			returner.put("availableFolders", getAvailableDamFolders());
			returner.put("deniedDamFolders", getExplicitlyDeniedDamFolders());
			returner.put("damPermissionSetting", getDamRootDirectoryPermissionStatus());
		} catch (JSONException e) {
			throw new CounselPermissionModificationException("Unable to serialize to json.", e);
		}
		return returner;
	}

	public void setExplicitlyDeniedDamFolders(List<FolderDTO> explicitlyDeniedDamFolders) {
		this.explicitlyDeniedDamFolders = explicitlyDeniedDamFolders;
	}

	public String getDamRootNodeState() {
		return damRootNodeState;
	}

	public void setDamRootNodeState(String damRootNodeState) {
		this.damRootNodeState = damRootNodeState;
	}
	
	
}
