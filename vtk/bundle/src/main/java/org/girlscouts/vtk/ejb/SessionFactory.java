package org.girlscouts.vtk.ejb;

import javax.jcr.Session;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

@Component
@Service(value=SessionFactory.class)
public class SessionFactory  extends BasePoolableObjectFactory {
	
	//@Reference
    private SlingRepository repository;
		
    		public SessionFactory(){}
			public SessionFactory(SlingRepository repository){ this.repository = repository;}
	
	        public Session makeObject() throws Exception {
	        	System.err.println( "REpository: " +( repository==null) ); 
	            return  repository.loginAdministrative(null);
	        }

	       
	        public void destroyObject(Session obj) throws Exception {
	        	System.err.println("Destroying session...");
	        	if( obj!=null)
	        		obj.logout();
	            obj =null;
	            
	        }
	        
	        
	        @Activate
	        void activate() {
	            System.err.println("Activating session factory");
	        }
	      
	    }

