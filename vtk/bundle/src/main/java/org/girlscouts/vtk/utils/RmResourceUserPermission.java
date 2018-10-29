package org.girlscouts.vtk.utils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.web.councilrollout.impl.CouncilCreatorImpl;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;



@Component(metatype = false, immediate = true)
@Properties({
	@Property(name = Constants.SERVICE_DESCRIPTION, value = "RmResourceUserPermission"),
        @Property(name="label", value="Girl Scouts VTK Modify Node Permissions"),
        @Property(name="description", value="Girl Scouts VTK Modify Node Permissions")
})
@Service(value = RmResourceUserPermission.class)
public class RmResourceUserPermission {

	private static Logger log = LoggerFactory.getLogger(RmResourceUserPermission.class);
 @Reference
 private SlingRepository repository;

 public void modifyNodePermissions(String nodePath,String groupName, boolean isTrue)
 { 
  Session session = null;
  try {

   session = repository.loginAdministrative(null);
  
   UserManager userMgr = ((org.apache.jackrabbit.api.JackrabbitSession) session).getUserManager();
   AccessControlManager accessControlManager = session.getAccessControlManager();
   Authorizable authorizable  = userMgr.getAuthorizable("everyone"); 

	try {
		JackrabbitSession jackSession = (JackrabbitSession) session;
		JackrabbitAccessControlManager acm = (JackrabbitAccessControlManager) jackSession.getAccessControlManager();
		
		
		List<JackrabbitAccessControlList> aclList = new ArrayList<JackrabbitAccessControlList>();
		
		Group group = (Group) userMgr.getAuthorizable(groupName);
		if (group == null) {
			log.error(" group does not exist under correct naming conventions.");
		} else {
			Iterator<Authorizable> iter = group.getMembers();
			while (iter.hasNext()) {
				try{
					Object x = iter.next();
					
					if( x instanceof Group){
						
						Group _group = (Group) x;
						String group_id= _group.getID();
			   			
						if(  !(group_id.contains("-authors") || group_id.contains("-reviewers") ) ) continue;
			  System.err.println("GroupId: "+ group_id);  		 
			    		  Principal principal = _group.getPrincipal();	
			    		  String[] parts = group_id.split("-");
			    		  String council_name = parts[0];
			   council_name="girlscoutsiowa";
			   
			    		  String role = parts[1]; 
			  		  
			    		  if("authors".equals( role) || "reviewers".equals( role ) ){ //TODO CHANGE editors TO authors
			    			 if( isTrue ){
			    			  //aclList.add(new PermissionsSetter(new Rule(principal, "/content/vtkcontent/en/resources2", "READ", true), acm, session).getPrivilegeList());
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/vtkcontent/en/resources2", " WRITE_MODIFY_REPLICATE_DELETE", true), acm, session).getPrivilegeList());
			    			  
			    			 
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/"+council_name +"/en/resources2", "READ", true), acm, session).getPrivilegeList());
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/"+council_name, "READ_WRITE", true), acm, session).getPrivilegeList());
			    			 }else{
			    			  
			    			   aclList.add(new PermissionsSetter(new Rule(principal, "/content/vtkcontent/en/resources2", "WRITE_MODIFY_REPLICATE_DELETE", false), acm, session).getPrivilegeList());
			    				
			    				 
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/"+council_name +"/en/resources2", "WRITE_MODIFY_REPLICATE_DELETE", false), acm, session).getPrivilegeList());
			    			  
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/"+council_name, "REPLICATE_DELETE", false), acm, session).getPrivilegeList());
			    			  
			    			 //check/create folder - /content/dam-resources2/girlscourts-[council]
				    		 JcrUtils.getOrCreateByPath("/content/dam-resources2/girlscourts-"+council_name, "sling:OrderedFolder", session);
				    			  
				    		 //copy from /content/girlscouts-template/en/resources2
			    			 if (!session.nodeExists("/content/"+ council_name +"/en/resources2")) {
			    				 // fail it, fix manually  JcrUtils.getOrCreateByPath("/content/"+ council_name +"/en/resources2", "nt:unstructured", session);
			    				  session.getWorkspace().copy("/content/girlscouts-template/en/resources2", "/content/"+ council_name +"/en/resources2");
			    			 }
			    			}
			    			 
			    		  }
			    		  
			    		  if("authors".equals( role )){
			    			  
			    			 if( !isTrue)
			    				 aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/girlscourts-"+council_name, "REPLICATE", false), acm, session).getPrivilegeList());
			    			 else 	
			    				 aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/girlscourts-"+council_name, "WRITE_READ_DELETE", true), acm, session).getPrivilegeList());
					    		
			    		  }else if("reviewers".equals( role )){
			    			
			    			  //aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/girlscourts-"+council_name, "REPLICATE", false), acm, session).getPrivilegeList());
			    			  aclList.add(new PermissionsSetter(new Rule(principal, "/content/dam-resources2/girlscourts-"+council_name, "WRITE_READ_DELETE_REPLICATE", true), acm, session).getPrivilegeList());
					    		
			    		  }
			    		  
			    		  
						
					}
					session.save();
				}catch(Exception e){e.printStackTrace();}
				
			}
		}
		
		
		//Policies are all generated into a list and for loop binds policies to their respective nodes
		for(JackrabbitAccessControlList l: aclList) {
			try{
				if( l==null) continue;
				acm.setPolicy(l.getPath(), l);
				session.save();
				
			}catch(Exception e){
				System.err.println("Failed to set policy on " + l.getPath());
				e.printStackTrace();
			}
			
		}
		session.save();
		
		
	
	} catch (Exception e) {
		e.printStackTrace();
	}

   session.save();      

  } catch (RepositoryException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  } finally {
   if (session != null)
    session.logout();
  }
 }
 
 
 
	class Rule {
		Principal principal;
		String contentPath;
		String permission;
		String glob;
		boolean isAllow;
		
		Rule(Principal principal, String contentPath, String permission, boolean isAllow) {
			this.principal = principal;
			this.contentPath = contentPath;
			this.permission = permission;
			this.glob = null;
			this.isAllow = isAllow;
		}
		
		Rule(Principal principal, String contentPath, String permission, String glob, boolean isAllow) {
			this.principal = principal;
			this.contentPath = contentPath;
			this.permission = permission;
			this.glob = glob;
			this.isAllow = isAllow;
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
			    
			    }catch(javax.jcr.PathNotFoundException pnfe){
	    	    	//TODO explain and notify 
	    	    	System.err.println("PATH not found ISSUE. Check if council exists... ");
	    	    	
			    } catch (NoSuchElementException e) {
			    	jacp = (JackrabbitAccessControlPolicy) this.manager.getPolicies(this.rule.contentPath)[0];
			    }
				if (this.rule.glob != null) {
					Map<String, Value> restrictions = new HashMap<String, Value>();
					ValueFactory vf = session.getValueFactory();
					restrictions.put("rep:glob", vf.createValue(this.rule.glob));
					((JackrabbitAccessControlList) jacp).addEntry(this.rule.principal, privileges, rule.isAllow, restrictions);
	
				} else if (this.rule.glob == null) {
					
					
					((JackrabbitAccessControlList) jacp).addEntry(this.rule.principal, privileges, rule.isAllow);	
				}	
				
			} else {
				log.error("Privilege(s) not found for given Permission rule " + this.rule.permission);
				throw new NullPointerException();			
				}			
			} catch (NullPointerException e) {
				log.error("Null Pointer Exception thrown: " + e.toString());			
			} catch (ClassCastException e) {
				log.error("Key of wrong type submitted for privilegeMap: " + e.toString());			
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
			    map.put("WRITE_MODIFY_REPLICATE_DELETE", new Privilege[]{
			    		manager.privilegeFromName(Privilege.JCR_WRITE),
			    		manager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE), 
			    		manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), 
			    		manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), 
			    		manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), 
			    		manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), 
			    		manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT),
			    		manager.privilegeFromName(Privilege.JCR_REMOVE_NODE)});
			    
			    map.put("READ_WRITE_MODIFY_DELETE", new Privilege[]{
			    		
			    		manager.privilegeFromName(Privilege.JCR_READ),
			    		manager.privilegeFromName(Privilege.JCR_WRITE),
			    		manager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES), 
			    		manager.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT), 
			    		manager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), 
			    		manager.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT), 
			    		manager.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT),
			    		manager.privilegeFromName(Privilege.JCR_REMOVE_NODE)});
			   
			    
			    
			} catch (RepositoryException e) {
				log.error("Error occurred while generating privileges in Privilege Map: " + e.toString());
			}			
			return map;			
		}
		
	}
}
