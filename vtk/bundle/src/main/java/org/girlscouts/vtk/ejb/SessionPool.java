package org.girlscouts.vtk.ejb;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
// TODO: Implement Real session pool
// TODO: Do not login as admin
public class SessionPool {
    private static final Logger log = LoggerFactory.getLogger(SessionPool.class);
    @Reference
    private SlingRepository repository;
    
    private Session session = null;
    
    @Activate
    void activate() {
        try {
            session = repository.loginAdministrative(null);
        } catch (RepositoryException e) {
            log.error("Serious Error: Cannot login CQ repository!!!");
        }
    }
    
    public Session getSession() {
        return session;
    }
}
