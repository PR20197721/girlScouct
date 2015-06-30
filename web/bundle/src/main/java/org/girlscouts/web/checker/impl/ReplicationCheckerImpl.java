package org.girlscouts.web.checker.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;

import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationStatus;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.girlscouts.web.exception.GirlScoutsException;

@Component
@Service(value = ReplicationChecker.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.checker.replicationchecker", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts replication check service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })

public class ReplicationCheckerImpl implements ReplicationChecker {
	private static Logger LOG = LoggerFactory.getLogger(ReplicationCheckerImpl.class);
	
	@Reference
    private LiveRelationshipManager relationManager;
	
	@Reference
	private SlingSettingsService slingSettings;

	/**
	 * Creates the layout of the site (national pages)
	 * 
	 * @param  contentPath  path leading up to council root, e.g. "/content/"
	 * @param  councilName  the domain name of the council
	 * @param  councilTitle  the full name of the council
	 * @return a list containing all pages that were created
	 */
	public List<Asset> checkAssets(Session authSession, Session pubSession, ResourceResolver rr, String contentPath) 
			throws GirlScoutsException{
		if(!slingSettings.getRunModes().contains("author")){
			LOG.error("REPLICATION CHECKER ERROR: need to run in author mode.");
			throw new GirlScoutsException(new Exception("Replication Checker Run Mode Error"),
					"only runnable in author mode.");
		}
		ArrayList<Asset> nonExistingAssets = new ArrayList<Asset>();
		
		try {
			String sql = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE(s, '"
					+ contentPath + "')";
			LOG.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2);it.hasNext();){
				Resource assetRes = it.next();
				Asset asset = assetRes.adaptTo(Asset.class);
				ReplicationStatus repStatus = assetRes.adaptTo(ReplicationStatus.class);
				if(repStatus.isActivated()){
					if(!existInPublish(pubSession, asset)){
						nonExistingAssets.add(asset);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new GirlScoutsException(e,null);
		} 
		return nonExistingAssets;
		
		
	}
	
	private boolean existInPublish(Session session, Asset asset){
		return if(session.nodeExists(asset.getPath()));
	}

	
}
