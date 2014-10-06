package org.girlscouts.vtk.ejb;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value=SessionPool.class)
// TODO: Implement Real session pool
// TODO: Do not login as admin
public class SessionPool {
    private static final Logger log = LoggerFactory.getLogger(SessionPool.class);
    
    @Reference
    private SlingRepository repository;
   
    //private Session session = null;
    
    GenericObjectPool pool=null;
    
    public Session getSession() {
    	Session session=null;
    	try {
			session= repository.loginAdministrative(null);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return session;
    }
    public void closeSession(Session session) {
    	session.logout();
    }
    
    /*
    @Activate
    void activateX() {
        try {
            //session = repository.loginAdministrative(null);
        	  System.err.println("ActivatePool...");
              PoolableObjectFactory factory = new  SessionFactory(repository) ;
              pool = new GenericObjectPool(factory);
              pool.clear();
              System.err.println("Start "+pool.getMaxActive()+" Num active: "+ pool.getNumActive() +" : "+ pool.getNumIdle());
              pool.setMaxActive(51);
        	  pool.setMaxWait(10);
        	 // pool.setMaxIdle(1);
        	 // pool.setMinIdle(1);
        	 // pool.setMinEvictableIdleTimeMillis(1);
            //  pool.setTimeBetweenEvictionRunsMillis(1);
            
              
            
        } catch (Exception e) {
            log.error("ActivatePool...Serious Error: Cannot login CQ repository!!!");
        }
    }
   
    
    public Session getSessionX() {
        
    	Session session=null;
    	try {
    		System.err.println(pool.getMaxActive()+ " Num active: "+ pool.getNumActive() +" : "+ pool.getNumIdle());
			session = (Session) pool.borrowObject();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return session;
    }
    
    public void closeSessionX(Session session){
    	try{ 
    		//pool.invalidateObject(session);
    		
    		pool.returnObject(session);
    		System.err.println( " Num active: "+ pool.getNumActive() +" : "+ pool.getNumIdle());
			
    	}catch(Exception e){e.printStackTrace();}
    }
    */
}
