package org.girlscouts.web.councilupdate.impl;

import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.girlscouts.web.councilupdate.TemporaryUserDeleter;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.OsgiUtil;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Expired User Deletion Service", 
		description = "Delete temporary users that have reached their expiration date" 
		)
@Service(value = {Runnable.class, TemporaryUserDeleter.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts Expired User Deletion Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
	@Property(name = "scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "userspath", label="Path to temporary users")
})

public class TemporaryUserDeleterImpl implements Runnable, TemporaryUserDeleter{
	
	private static Logger log = LoggerFactory.getLogger(TemporaryUserDeleterImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;
	Map<String, Object> serviceParams = new HashMap<String, Object>();
	//configuration fields
	public static final String USERPATH = "userspath";
	
	private String usersPath;
	private Map<String, Map<String, Exception>> errors;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.usersPath = OsgiUtil.toString(configs.get(USERPATH), null);
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
	}
	
	public void run() {
		//TODO: Change the following if case so that it uses policy = ConfigurationPolicy.REQUIRE to check for publishing servers 
		//More info: http://aemfaq.blogspot.com/search/label/runmode
		//http://stackoverflow.com/questions/19292933/creating-osgi-bundles-with-different-services
		if (isPublisher()) {
			return;
		}
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			Resource usersRes = rr.resolve(usersPath);
			if (usersRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				log.error("Temp Users node not found");
				return;
			}

			Resource dateRes = getDateRes(usersRes, rr);
			if (dateRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				log.info("No expired users today for deletion");
				return;
			}

			Node dateNode = dateRes.adaptTo(Node.class);
			String[] users = getUsers(dateNode);
			if (users.length < 1) {
				log.info("No expired users found for deletion today");
				return;
			}
			Session session = rr.adaptTo(Session.class);
			String userString = "";
			String status = "Success";
			Node reportNode = null;

			try{
				if (dateNode.hasNode("report")) {
					reportNode = dateNode.getNode("report");
				} else {
					reportNode = dateNode.addNode("report", "nt:unstructured");
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			for (String s : users) {
				try{
					userString = s;

					UserManager userManager = ((JackrabbitSession) session).getUserManager();
					User tempUser = (User) userManager.getAuthorizable(userString);
					Iterator<Group> tempUserGroups = tempUser.memberOf();
					Group group;
					// Remove user from all its groups
					while (tempUserGroups.hasNext()) {
						group = tempUserGroups.next();
						group.removeMember(tempUser);
						replicator.replicate(session, ReplicationActionType.ACTIVATE, group.getPath());
					}
					// Deactivate and Delete User
					replicator.replicate(session, ReplicationActionType.DEACTIVATE, tempUser.getPath());
					tempUser.remove();
					log.info("User: " + userString + " has been deleted");
				} catch (Exception e) {
					log.error("An error occurred while deleting user: " + userString);
					try {
						status = "Completed with errors";
						Node detailedReportNode = reportNode.addNode(s, "nt:unstructured");
						detailedReportNode.setProperty("message", e.getMessage());
					} catch (Exception e1) {
						log.error("Temporary User Deleter - An exception occurred while creating error node");
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}

			try {
				reportNode.setProperty("status", status);
				session.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} finally {
			// Close resource resolver
			try {
				rr.close();
			} catch (Exception e) {
				log.error("error while closing resource resolver: ", e);
			}
		}
	}
	
	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
        }
		return false;
	}
	
	private Resource getDateRes(Resource r, ResourceResolver rr) {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(today);
		Resource dateRes = rr.resolve(r.getPath() + "/" + dateString);
		return dateRes;
	}
	
	private String[] getUsers(Node n){
		try{
			if(n.hasProperty("users")){
				Value[] values = n.getProperty("users").getValues();
				String[] users = new String[values.length];
				for(int i=0; i<values.length; i++){
					users[i] = values[i].getString();
				}
				return users;
			}else{
				return new String[0];
			}
		}catch(Exception e){
			e.printStackTrace();
			return new String[0];
		}
	}
	
	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("Temporary User Deletion Service Deactivated.");
	}
	
}