package org.girlscouts.web.gsusa.component.boothfinder;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Component(label = "Girl Scouts Cookie Booth Finder", description = "Find nearby cookie booth", metatype = true, immediate = true)
@Service
@Properties({
		@Property(name = "apiBasePath", label = "API Base Path", description = "The base path of Girl Scouts cookie booth API")
})
public class BoothFinder {
	// TODO: use configuration. Pay attention to the trailing slash. 
    private static final String API_BASE = "http://www.girlscoutcookies.org/";
	private static final String LOCAL_COUNCIL_COOKIE_SALE_STATUS_API = "ajax_GetCouncilInfo.asp?ZIPCode=";
	private static final long serialVersionUID = -1155198001819957909L;
	private static Logger log = LoggerFactory.getLogger(BoothFinder.class);
	
	private HttpConnectionManager connectionManager;
	private HttpClient httpClient;
	private DocumentBuilderFactory dbFactory;
	
	@Activate
	public void init() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(connectionManager);
        dbFactory = DocumentBuilderFactory.newInstance();
	}
	
	// TODO: test. Remote later
	public static void main(String[] args) {
		BoothFinder finder = new BoothFinder();
		finder.init();
		try {
		    String path = finder.getPath("10018");
		    System.out.println("path = " + path);
		} catch (Exception e) {}
	}
	
	public String getPath(String zipCode) throws Exception {
		GetMethod get = new GetMethod(API_BASE + LOCAL_COUNCIL_COOKIE_SALE_STATUS_API + zipCode.trim());

		try {
		    int resStatus = httpClient.executeMethod(get);
		    if (resStatus != 200) {
			    throw new Exception("Response not 200.");
		    }
		} catch (HttpException he) {
			throw new Exception(he);
		} catch (IOException ie) {
			throw new Exception(ie);
		}
		
		try {
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(get.getResponseBodyAsStream());
		    
		    String path = doc.getElementsByTagName("PreferredPath").item(0).getTextContent();
		    return path;
		} catch (ParserConfigurationException pce) {
			throw new Exception(pce);
		} catch (SAXException se) {
			throw new Exception(se);
		}
	}
}


















