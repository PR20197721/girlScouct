package org.girlscouts.vtk.utils;

import java.util.NoSuchElementException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype = false, immediate = true)
@Properties({
	@Property(name = Constants.SERVICE_DESCRIPTION, value = "ModifyNodePermissions"),
        @Property(name="label", value="Girl Scouts VTK Modify Node Permissions"),
        @Property(name="description", value="Girl Scouts VTK Modify Node Permissions")
})
@Service(value = ModifyNodePermissions.class)
public class ModifyNodePermissions {

 private static final Logger log = LoggerFactory.getLogger(ModifyNodePermissions.class);
 @Reference
 private SlingRepository repository;

 public void modifyNodePermissions(String nodePath,String groupName)
 { 
  Session session = null;
  try {
   
   session = repository.loginAdministrative(null);
  
   JcrUtils.getOrCreateByPath(nodePath.substring(0, nodePath.length()-1), "nt:unstructured", session);

   UserManager userMgr = ((org.apache.jackrabbit.api.JackrabbitSession) session).getUserManager();
   AccessControlManager accessControlManager = session.getAccessControlManager();
   Authorizable authorizable  = userMgr.getAuthorizable(groupName); 
   AccessControlPolicyIterator policyIterator = accessControlManager.getApplicablePolicies(nodePath);

   org.apache.jackrabbit.api.security.JackrabbitAccessControlList acl = null;
   
   try {
    acl = (JackrabbitAccessControlList) policyIterator.nextAccessControlPolicy();               
    
   } catch (NoSuchElementException nse) {
    acl = (JackrabbitAccessControlList) accessControlManager.getPolicies(nodePath)[0];
   }
   
   //Remove the Access Control Entry
     /*for (AccessControlEntry e : acl.getAccessControlEntries()) {    
       if (e.getPrincipal().equals(authorizable.getPrincipal()))
      {
           acl.removeAccessControlEntry(e);
      }
                }*/   
   
   //Allow
   /*Privilege[] allowPrivileges = {accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_NODE),
     accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES) };
   
   acl.addEntry(authorizable.getPrincipal(), allowPrivileges, true);   
     */
   
   /*
   //Deny
   Privilege[] denyPrivileges = {accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_NODE),
     accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES) };   
   acl.addEntry(authorizable.getPrincipal(), denyPrivileges, false);
   */
   
   //Grant
   Privilege[] grantPrivileges = {accessControlManager.privilegeFromName(Privilege.JCR_ALL),
     accessControlManager.privilegeFromName(Privilege.JCR_ALL) };   
   acl.addEntry(authorizable.getPrincipal(), grantPrivileges, true);
   
   
      
   //Add Policy
   accessControlManager.setPolicy(nodePath, acl);
   //Remove Policy
   //accessControlManager.removePolicy(nodePath, acl);
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
}
