package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/*
 * Used to get counsel names by grabbing all the nodes under /content with a type of "page".
 */
public class ContentCounselRetriever {
	
	public static final String BASE_COUNSEL_PATH = "/content";
	public static final String BASE_DAM_PATH = "/content/dam";
	
	private List<Counsel> counselList = null;
	private List<FolderDTO> availableDamFolders = null;
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
		
		Map<String, Object> predicateMap = new HashMap<>();
		predicateMap.put("path", BASE_COUNSEL_PATH);
		predicateMap.put("path.flat", "true");
		predicateMap.put("type", "cq:Page");
		
		Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), adminSession);
		SearchResult result = query.getResult();
        Iterator<Node> nodes = result.getNodes();
		
        while(nodes.hasNext()) {
        		Node node = nodes.next();
        		String nodeName = node.getName();

        		// Exclude vtk and usa.
        		if(!Stream.of("vtk", "usa", "demo", "we-retail").anyMatch(nodeName.toLowerCase()::contains)) {
        			
        			// testing statement.
        			//if(nodeName.equals("gsnetx"))
        			counselList.add(new Counsel(node, adminSession));
        		}
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
		
		Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), adminSession);
		SearchResult result = query.getResult();
        Iterator<Node> nodes = result.getNodes();
        
        // Get a list dam folders used by other counsels to exclude.
        List<String> usedDamFolders = new ArrayList<>();
        for(Counsel counsel : getCounsels()) {
        		for(CounselFolder folder : counsel.getCounselFolders()) {
        			if(folder.getConfig() == DIRECTORY_CONFIG.DAM) {
        				usedDamFolders.add(folder.getPath());
        			}
        		}
        }
		
        while(nodes.hasNext()) {
        		Node node = nodes.next();
        		String nodeName = node.getName();

        		// Exclude vtk and usa.
        		if(!Stream.of("vtk", "usa", "demo", "we-retail", ":").anyMatch(nodeName.toLowerCase()::contains)) {
        			
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
	
	
}
