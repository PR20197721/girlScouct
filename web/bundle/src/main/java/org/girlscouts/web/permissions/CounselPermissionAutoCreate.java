package org.girlscouts.web.permissions;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CounselPermissionAutoCreate {
	
	
	@Reference
	private SlingRepository repository;
	
	@Activate
	protected void activate() {
		LOGGER.info("Starting CounselPermissionAutoCreate Process...");
		//modifyPermissions();
		LOGGER.info("CounselPermissionAutoCreate Complete...");
	}
	
	private void modifyPermissions() {
		LOGGER.info("This is where we would be modifying permissions...");
		try {
			//Session adminSession = repository.loginService("/", "admin");
			Session adminSession = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
			for(Counsel counsel : new ContentCounselRetriever(adminSession, null).getCounsels()) {
				LOGGER.info("Found counsel: " + counsel.getName());
			}
		} catch (CounselPermissionModificationException | RepositoryException e) {
			LOGGER.error("Unable to modify counsel access controls." + e.getMessage(), e);
		}
	}

	
	private static final Logger LOGGER = LoggerFactory.getLogger(CounselPermissionAutoCreate.class);
	
}