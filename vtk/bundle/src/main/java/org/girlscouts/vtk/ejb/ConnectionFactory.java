package org.girlscouts.vtk.ejb;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.net.ssl.SSLContext;

import org.apache.felix.scr.annotations.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true, metatype=true)
@Properties ({
        @Property(name="label", value="Girl Scouts Connection Factory"),
        @Property(name="description", value="Girl Scouts Connection Factory")
})
@Service(value = ConnectionFactory.class)
public class ConnectionFactory {
	private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
	private PoolingHttpClientConnectionManager connMrg;
	@Activate
	void activate() {
		connMrg = new PoolingHttpClientConnectionManager();//1, TimeUnit.SECONDS);
		connMrg.setMaxTotal(200);
		connMrg.setDefaultMaxPerRoute(30);	
	}
	
	public CloseableHttpClient getConnection() throws RepositoryException, LoginException, KeyManagementException, NoSuchAlgorithmException {
		
		/*
		SSLContext sslContext = SSLContexts.custom()
		        .useTLS() // Only this turned out to be not enough
		        .build();
		SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(
		        sslContext,
		        new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"},
		        null,
		        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient connection = HttpClients.custom()
		        .setSSLSocketFactory(sf)
		        .setConnectionManager(connMrg)
		        .build();
		*/
		/*
		CloseableHttpClient connection = HttpClients.custom()
				.setConnectionManager(connMrg)
		        .build();
		        */
		
		
		 CloseableHttpClient connection = HttpClientBuilder
			      .create()
			      .setConnectionManager(connMrg)
			      .setSslcontext(SSLContexts.custom().useProtocol("TLSv1.2").build())
			      .build();
		return connection;
	}

	@Deactivate
	public void closeConnection() throws IOException {
		connMrg.close();
	}
}
