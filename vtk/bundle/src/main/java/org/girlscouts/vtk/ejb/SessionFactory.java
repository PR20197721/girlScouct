package org.girlscouts.vtk.ejb;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = SessionFactory.class)
public class SessionFactory {
	private static final Logger log = LoggerFactory
			.getLogger(SessionFactory.class);

	@Reference
	private SlingRepository repository;
	
	@Reference
	 public ResourceResolverFactory resourceFactory;

	public ResourceResolver getResourceResolver(){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
		
		ResourceResolver adminResolver = null;
		try {
			adminResolver = resourceFactory.getServiceResourceResolver(paramMap);
		} catch (org.apache.sling.api.resource.LoginException e) {
			e.printStackTrace();
		}
		return adminResolver;
	}
	
	public void closeResourceResolver( ResourceResolver rr ){
		rr.close();
		
	}
}
