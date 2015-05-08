package org.girlscouts.vtk.ejb;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true)
@Service(value = ConnectionFactory.class)
public class ConnectionFactory {
	private static final Logger log = LoggerFactory
			.getLogger(SessionFactory.class);

	private PoolingHttpClientConnectionManager connMrg;
	
	@Activate
	void activate() {
	

		connMrg = new PoolingHttpClientConnectionManager();//1, TimeUnit.SECONDS);
		connMrg.setMaxTotal(200);
		connMrg.setDefaultMaxPerRoute(30);
		//connMrg.closeIdleConnections(1,  TimeUnit.SECONDS);
		//connMrg.closeExpiredConnections();
		
		
		
	}
	
	public CloseableHttpClient getConnection() throws RepositoryException, LoginException {

		
		System.err.println("HP_STATS available: "+ connMrg.getTotalStats().getAvailable() +
				" MAX: " + connMrg.getTotalStats().getMax() +
				" leased: " + connMrg.getTotalStats().getLeased() +
				" pending: " + connMrg.getTotalStats().getPending() 
				
				
				);
		
		CloseableHttpClient connection = HttpClients.custom()
				.setConnectionManager(connMrg)
		        .build();
		return connection;
	}

	@Deactivate
	public void closeConnection(CloseableHttpClient connection) throws IOException {

		connection.close();
	}

}
