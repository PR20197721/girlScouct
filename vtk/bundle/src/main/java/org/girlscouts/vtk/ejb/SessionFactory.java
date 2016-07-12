package org.girlscouts.vtk.ejb;

import java.sql.SQLException;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
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
/*
	 @Reference(target = "(&(objectclass=javax.sql.DataSource)(datasource.name=alex))")
	 private DataSource dataSource;
	 
	 
	 public java.sql.Connection getConnection() throws SQLException{
		 return dataSource.getConnection();
	 }
	 */
	public Session getSession() throws RepositoryException, LoginException {
	/*
		try{
			System.err.println( "test Connection: " );
			System.err.println( "test: "+ getConnection() );
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		Session adminSession = repository.loginAdministrative(null);
		Session session = adminSession.impersonate(new SimpleCredentials("admin","admin".toCharArray()));
		adminSession.logout();
		adminSession = null;
		return session;
		
		
		
	}

	public void closeSession(Session session) {
		session.logout();
	}

}
