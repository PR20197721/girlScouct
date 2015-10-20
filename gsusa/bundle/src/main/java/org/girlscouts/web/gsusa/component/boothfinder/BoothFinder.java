package org.girlscouts.web.gsusa.component.boothfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	private static final String FETCH_BOOTH_LIST_API = "iphoneapp/booth_list.asp?";
	private static final long serialVersionUID = -1155198001819957909L;
	private static Logger log = LoggerFactory.getLogger(BoothFinder.class);
	
	private HttpConnectionManager connectionManager;
	private HttpClient httpClient;
	private DocumentBuilderFactory dbFactory;
	
	public static class BoothBasic {
		public String id, distance, location, address1, address2;

		@Override
		public String toString() {
			return "BoothBasic: id=" + id + ";distance=" + distance + ";location=" + location +
				   ";address1 = " + address1 + ";address2=" + address2;
		}
	}
	
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
			List<BoothBasic> booths = finder.getBooths("11361", "180", "100", "distance", 0, 100);
			System.out.println(booths);
		} catch (Exception e) {}
	}
	
	public String getPath(String zipCode) throws Exception {
		GetMethod get = new GetMethod(API_BASE + LOCAL_COUNCIL_COOKIE_SALE_STATUS_API + zipCode.trim());

		try {
		    int resStatus = httpClient.executeMethod(get);
		    if (resStatus != 200) {
			    throw new Exception("Response not 200.");
		    }

		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(get.getResponseBodyAsStream());
		    
		    String path = doc.getElementsByTagName("PreferredPath").item(0).getTextContent();
		    return path;
		} catch (HttpException he) {
			throw new Exception(he);
		} catch (IOException ie) {
			throw new Exception(ie);
		} catch (ParserConfigurationException pce) {
			throw new Exception(pce);
		} catch (SAXException se) {
			throw new Exception(se);
		}
	}
	
	public List<BoothBasic> getBooths(String zipCode, String dateRange, String distance, String sortBy, int pageNum, int numPerPage) throws Exception {
		String apiPath = API_BASE + FETCH_BOOTH_LIST_API + 
				         "z=" + zipCode.trim() + 
				         "&r=" + distance.trim() + 
				         "&t=" + sortBy.trim() + 
				         "&s=" + Integer.toString(pageNum * numPerPage + 1) +
				         "&m=" + Integer.toString(numPerPage) +
				         //"&d=" + dateRange.trim();
				         // TODO: for test:
				         "&d1=2013-10-1&d2=2014-4-30";

		GetMethod get = new GetMethod(apiPath);
		System.out.println(apiPath);
		try  {
		    int resStatus = httpClient.executeMethod(get);
		    if (resStatus != 200) {
			    throw new Exception("Response not 200.");
		    }

		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(get.getResponseBodyAsStream());
		    
		    NodeList dicts = doc.getElementsByTagName("dict");
		    List<BoothBasic> booths = new ArrayList<BoothBasic>();
		    for (int i = 0; i < dicts.getLength(); i++) {
		    	Node dict = dicts.item(i);
		    	BoothBasic booth = new BoothBasic();
		    	NodeList childs = dict.getChildNodes();
		    	String key = "undefined_key";
		    	for (int j = 0; j < childs.getLength(); j++) {
		    		Node child = childs.item(j);
		    		String name = child.getNodeName();
		    		if ("key".equals(name)) {
		    			key = child.getTextContent();
		    		} else if ("string".equals(name)) {
		    			String value = child.getTextContent();

		    			if ("id".equalsIgnoreCase(key)) {
		    				booth.id = value;
		    			} else if ("distance".equalsIgnoreCase(key)) {
		    				booth.distance = value;
		    			} else if ("location".equalsIgnoreCase(key)) {
		    				booth.location = value;
		    			} else if ("address1".equalsIgnoreCase(key)) {
		    				booth.address1 = value;
		    			} else if ("address2".equalsIgnoreCase(key)) {
		    				booth.address2 = value;
		    			}
		    		}
		    	}
		    	booths.add(booth);
		    }
		    
		    return booths;
		} catch (HttpException he) {
			throw new Exception(he);
		} catch (IOException ie) {
			throw new Exception(pce);
		} catch (SAXException se) {
			throw new Exception(se);
		}
	}
}