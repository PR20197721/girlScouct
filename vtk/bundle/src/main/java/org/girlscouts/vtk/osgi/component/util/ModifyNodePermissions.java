package org.girlscouts.vtk.osgi.component.util;

import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import java.util.NoSuchElementException;

@Component(metatype = false, immediate = true)
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "ModifyNodePermissions"), @Property(name = "label", value = "Girl Scouts VTK Modify Node Permissions"), @Property(name = "description", value = "Girl Scouts VTK Modify Node Permissions")})
@Service(value = ModifyNodePermissions.class)
public class ModifyNodePermissions {
    private static final Logger log = LoggerFactory.getLogger(ModifyNodePermissions.class);
    @Reference
    private SlingRepository repository;

    public void modifyNodePermissions(String nodePath, String groupName) {
        Session session = null;
        try {
            session = repository.loginAdministrative(null);
            JcrUtils.getOrCreateByPath(nodePath, "nt:unstructured", session);
            UserManager userMgr = ((org.apache.jackrabbit.api.JackrabbitSession) session).getUserManager();
            AccessControlManager accessControlManager = session.getAccessControlManager();
            Authorizable authorizable = userMgr.getAuthorizable(groupName);
            AccessControlPolicyIterator policyIterator = accessControlManager.getApplicablePolicies(nodePath);
            org.apache.jackrabbit.api.security.JackrabbitAccessControlList acl = null;
            try {
                acl = (JackrabbitAccessControlList) policyIterator.nextAccessControlPolicy();

            } catch (NoSuchElementException nse) {
                acl = (JackrabbitAccessControlList) accessControlManager.getPolicies(nodePath)[0];
            }
            //Grant
            Privilege[] grantPrivileges = {accessControlManager.privilegeFromName(Privilege.JCR_ALL), accessControlManager.privilegeFromName(Privilege.JCR_ALL)};
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
            if (session != null) {
                session.logout();
            }
        }
    }
}
