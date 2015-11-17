package org.girlscouts.web.gsusa.component.boothfinder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component(label = "Girl Scouts Cookie Booth Finder", description = "Find nearby cookie booth", metatype = true, immediate = true)
@Service(BoothFinder.class)
@Properties({
        @Property(name = "apiBasePath", label = "API Base Path", description = "The base path of Girl Scouts cookie booth API"),
        @Property(name = "connectionTimeout", intValue = 10000, label = "Connection Timeout", description = "Timeout for connecting to the API server in milliseconds."),
        @Property(name = "socketTimeout", intValue = 10000, label = "Socket Timeout", description = "Timeout for the reponse from the API server in milliseconds.")
})
public class BoothFinder {
    // TODO: use configuration. Pay attention to the trailing slash. 
    private static final String API_BASE = "http://www.girlscoutcookies.org/";
    private static final String LOCAL_COUNCIL_COOKIE_SALE_STATUS_API = "ajax_GetCouncilInfo.asp?ZIPCode=";
    private static final String FETCH_BOOTH_LIST_API = "iphoneapp/booth_list.asp?";
    private static final String SAVE_CONTACT_ME_INFORMATION_API = "ajax_customer_insert.asp?";
    private static final String websiteIdentify = "Website";
    @SuppressWarnings("unused")
	private static final long serialVersionUID = -1155198001819957909L;
    private static Logger log = LoggerFactory.getLogger(BoothFinder.class);
    
    private HttpConnectionManager connectionManager;
    private HttpClient httpClient;
    private int connectionTimeout, socketTimeout;
    private DocumentBuilderFactory dbFactory;
    private String apiBasePath;
    
    public static class BoothBasic {
        public String id, distance, location, address1, address2, dateStart, dateEnd, timeOpen, timeClose;

        
        @Override
        public String toString() {
            return "BoothBasic: id=" + id + ";distance=" + distance + ";location=" + location +
                   ";address1=" + address1 + ";address2=" + address2 +
                   ";dateStart=" + dateStart + ";dateEnd=" + dateEnd +
                   ";timeOpen=" + timeOpen + ";timeClose=" + timeClose;
        }
    }
    
    public static class Council {
        public String code, name, abbrName, cityStateZip,
            url, cookieSaleStartDate, cookieSaleEndDate,
            preferredPath, path2Method, cookiePageUrl,
            cookieSaleContactEmail;
        
        
        
        public Council() {
			this.code = "";
			this.name = "";
			this.abbrName = "";
			this.cityStateZip = "";
			this.url = "";
			this.cookieSaleStartDate = "";
			this.cookieSaleEndDate = "";
			this.preferredPath = "";
			this.path2Method = "";
			this.cookiePageUrl = "";
			this.cookieSaleContactEmail = "";
		}

		@Override
        public String toString() {
            return "Council: code=" + code + ";name=" + name + ";abbrName=" + abbrName +
                   ";cityStateZip=" + cityStateZip + ";url=" + url +
                   ";cookieSaleStartDate=" + cookieSaleStartDate +
                   ";cookieSaleEndDate=" + cookieSaleEndDate +
                   ";preferredPath=" + preferredPath + ";path2Method=" + path2Method +
                   ";cookiePageUrl=" + cookiePageUrl + ";cookieSaleContactEmail=" + cookieSaleContactEmail;
                    
        }
    }
    
    @Modified
    @Activate
    public void updateConfig(ComponentContext context) {
        dbFactory = DocumentBuilderFactory.newInstance();
        @SuppressWarnings("rawtypes")
        Dictionary dict = context.getProperties();
    	apiBasePath = (String)dict.get("apiBasePath");
    	if (null == apiBasePath || apiBasePath.isEmpty()) {
    		if (!apiBasePath.endsWith("/")) {
    			apiBasePath += "/";
    		}
    		apiBasePath = API_BASE;
    	}

    	connectionTimeout = (Integer)dict.get("connectionTimeout");
    	socketTimeout = (Integer)dict.get("socketTimeout");
        connectionManager = new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(connectionManager);
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(connectionTimeout);
        params.setSoTimeout(socketTimeout);
        connectionManager.setParams(params);
        log.info("HttpConnctionManager start up. Connection Timeout = " + Integer.toString(connectionTimeout) +
        		" Socket Timeout = " + Integer.toString(socketTimeout));
    }
    
    public Council getCouncil(String zipCode) throws Exception {
        GetMethod get = new GetMethod(apiBasePath + LOCAL_COUNCIL_COOKIE_SALE_STATUS_API + zipCode.trim());

        try {
            int resStatus = httpClient.executeMethod(get);
            if (resStatus != 200) {
                throw new Exception("Response not 200.");
            }

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(get.getResponseBodyAsStream());
            
            Council council = new Council();
            try {
                council.code = doc.getElementsByTagName("CouncilCode").item(0).getTextContent();
                council.name = doc.getElementsByTagName("CouncilName").item(0).getTextContent();
                council.abbrName = doc.getElementsByTagName("CouncilAbbrName").item(0).getTextContent();
                council.cityStateZip = doc.getElementsByTagName("CouncilCityStateZip").item(0).getTextContent();
                council.url = doc.getElementsByTagName("CouncilURL").item(0).getTextContent();
                council.cookieSaleStartDate = doc.getElementsByTagName("CookieSaleStartDate").item(0).getTextContent();
                council.cookieSaleEndDate = doc.getElementsByTagName("CookieSaleEndDate").item(0).getTextContent();
                council.preferredPath = doc.getElementsByTagName("PreferredPath").item(0).getTextContent();
                council.path2Method = doc.getElementsByTagName("Path2Method").item(0).getTextContent();
                council.cookiePageUrl = doc.getElementsByTagName("CookiePageURL").item(0).getTextContent();
                council.cookieSaleContactEmail = doc.getElementsByTagName("CookieSaleContact_Email").item(0).getTextContent();
            } catch (NullPointerException ne) {
                log.error("Error while getting XML element. The response body was: " + get.getResponseBodyAsString());
            }
            return council;
        } catch (HttpException he) {
            throw new Exception(he);
        } catch (IOException ie) {
            throw new Exception(ie);
        } catch (ParserConfigurationException pce) {
            throw new Exception(pce);
        } catch (SAXException se) {
            throw new Exception(se);
        } finally {
            get.releaseConnection();
        }
    }
    
    public List<BoothBasic> getBooths(String zipCode, String dateRange, String distance, String sortBy, int pageNum, int numPerPage, final String queryString) throws Exception {
        String apiPath = apiBasePath + FETCH_BOOTH_LIST_API + 
                         "z=" + zipCode.trim() + 
                         "&r=" + distance.trim() + 
                         "&t=" + sortBy.trim() + 
                         "&s=" + Integer.toString(pageNum * numPerPage + 1) +
                         "&m=" + Integer.toString(numPerPage + 1) + // Query one more record to see if there are more.
                         // TODO: What is the number for "all"?
                         "&d=" + ("all".equalsIgnoreCase(dateRange) ? "365" : dateRange.trim()) +
                         "&f=" + websiteIdentify;

        if (queryString != null && queryString != "") { 
        	Map<String, List<String>> para = splitQuery(queryString);
        	if (para.get("utm_campiagn") != null && para.get("utm_campiagn").get(0) != null) {
        		apiPath += "&utm_campiagn=" + para.get("utm_campiagn").get(0);
        	}
        	if (para.get("utm_medium") != null && para.get("utm_medium").get(0) != null) {
        		apiPath += "&utm_medium=" + para.get("utm_medium").get(0);
        	}
        	if (para.get("utm_source") != null && para.get("utm_source").get(0) != null) {
        		apiPath += "&utm_source=" + para.get("utm_source").get(0);
        	}
        }

        GetMethod get = new GetMethod(apiPath);
        try  {
            int resStatus = httpClient.executeMethod(get);
            
            // TODO: Even if it is returning 200, there is still of error.
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
                        } else if ("Distance".equalsIgnoreCase(key)) {
                            booth.distance = value;
                        } else if ("Location".equalsIgnoreCase(key)) {
                            booth.location = value;
                        } else if ("Address1".equalsIgnoreCase(key)) {
                            booth.address1 = value;
                        } else if ("Address2".equalsIgnoreCase(key)) {
                            booth.address2 = value;
                        } else if ("DateStart".equalsIgnoreCase(key)) {
                            booth.dateStart = value;
                        } else if ("DateEnd".equalsIgnoreCase(key)) {
                            booth.dateEnd = value;
                        } else if ("TimeOpen".equalsIgnoreCase(key)) {
                            booth.timeOpen = value;
                        } else if ("timeClose".equalsIgnoreCase(key)) {
                            booth.timeClose = value;
                        }
                    }
                }
                booths.add(booth);
            }
            
            return booths;
        } catch (HttpException he) {
            throw new Exception(he);
        } catch (IOException ie) {
            throw new Exception(ie);
        } catch (ParserConfigurationException pce) {
            throw new Exception(pce);
        } catch (SAXException se) {
            throw new Exception(se);
        } finally {
            get.releaseConnection();
        }
    }
    
    public String saveContactMeInformation(String zipCode, String email, String firstName, boolean optIn, String phone) throws Exception {
        String apiPath = apiBasePath + SAVE_CONTACT_ME_INFORMATION_API + 
                "ZIPCode=" + zipCode.trim() + 
                "&Email=" + email.trim() + 
                "&FirstName=" + firstName.trim() + 
                "&OptIn=" + (optIn ? "true" : "false") +
                "&Phone=" + phone.replaceAll("[^0-9]", "");

        GetMethod get = new GetMethod(apiPath);
        try  {
            int resStatus = httpClient.executeMethod(get);
            if (resStatus != 200) {
                throw new Exception("Response not 200.");
            }
            
            String resBody = get.getResponseBodyAsString().trim();
            return resBody;
        } catch (HttpException he) {
            throw new Exception(he);
        } catch (IOException ie) {
            throw new Exception(ie);
        } finally {
            get.releaseConnection();
        }
    }
    
    public Map<String, List<String>> splitQuery(String stringQuery) throws UnsupportedEncodingException {
    	final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
    	final String[] pairs = stringQuery.split("&");
    	for (String pair : pairs) {
    	    final int idx = pair.indexOf("=");
    	    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
    	    if (!query_pairs.containsKey(key)) {
    	        query_pairs.put(key, new LinkedList<String>());
    	    }
    	    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
    	    query_pairs.get(key).add(value);
    	}
    	return query_pairs;
    }
}