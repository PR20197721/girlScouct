package org.girlscouts.gsactivities.dataimport.impl;

import org.girlscouts.gsactivities.dataimport.EventsImport;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.web.exception.GirlScoutsException;

@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts SF Events Import Service", 
		description = "Import SF Events from FTP File" 
		)
@Service(value = {Runnable.class, EventsImport.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts SF Events Import Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	//@Property( name = "scheduler.expression", label="scheduler.expression", value = "0 * * * * ?",description="cron expression"),
	@Property(name = "scheduler.period",label="scheduler.period", description="time interval in seconds", longValue=2000),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
	@Property(name="scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "server",label = "FTP Server Address", description = "FTP server"),
	@Property(name = "username", label="username"),
	@Property(name = "password", label="password"),
	@Property(name = "directory", label="directory name"),
	@Property(name = "salesforcepath", label="Salesforce redirect path", description="This path is used to redirect links to salesforce")
})

public class EventsImportJobImpl implements Runnable, EventsImport{

	private static Logger log = LoggerFactory.getLogger(EventsImportJobImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	private CouncilMapper councilMap;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;

	private FTPSClient ftp;
	private ResourceResolver rr;
	//configuration fields
	public static final String SERVER = "server";
	public static final String USER = "username";
	public static final String PASSWORD = "password";
	public static final String DIR = "directory";
	public static final String LAST_UPD = "lastUpdated";
	public static final String SALESFORCEPATH = "salesforcepath";
	//parent keys in Json file
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String ACTION = "action";
	public static final String PAYLOAD = "payload";
	//payload keys in json files
	public static final String _councilCode = "councilCode";
	public static final String _member_only = "memberOnly";
	public static final String _id = "id";
	public static final String _title = "title";
	public static final String _tags = "tags";
	public static final String _start = "start";
	public static final String _end = "end";
	public static final String _location = "locationLabel";
	public static final String _address = "address";
	public static final String _description = "srchdisp";
	public static final String _details = "details";
	public static final String _image = "image";
	public static final String _color = "color";
	public static final String _register = "register";
	public static final String _region = "region";
	public static final String _visibleDate = "visibleDate";
	public static final String _thumbImage = "thumbImage";
	public static final String _priceRange = "priceRange";
	private static final String _globalTagNamespace = "sf-activities";
	private static final String _timezone ="timezone";

	//The filename of the zip file is in this format: gsevents-yyyy-MM-ddTHHmmSS.zip
	public static final String ZIP_REGEX = "gsevents-\\d{4}-\\d{2}-\\d{2}T\\d{6}.zip";
	public static final SimpleDateFormat ZIP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HHmmSS");
	public static final SimpleDateFormat NO_TIMEZONE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final SimpleDateFormat ERROR_LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");

	public static final int TIME_OUT_IN_SEC = 300;
	public static final int TIME_OUT_IN_MS = 0;
	public static final String CONFIG_PATH = "/etc/data-import/girlscouts/salesforce-events";
	public static final String[] ILLEGAL_CHAR_MAPPING = JcrUtil.HYPHEN_LABEL_CHAR_MAPPING;
	public static final String DEFAULT_REPL_CHAR = "_";

	private String server, user, password, directory, salesforcePath;
	//keep track of the latest time stamp of import.
	private Date lastUpdated= new Date(Long.MIN_VALUE);
	//map to track the error
	private Map<String, Map<String, Exception>> errors;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.server=OsgiUtil.toString(configs.get(SERVER), null);
		this.user=OsgiUtil.toString(configs.get(USER), null);
		this.password=OsgiUtil.toString(configs.get(PASSWORD), null);
		this.directory=OsgiUtil.toString(configs.get(DIR), null);
		this.salesforcePath= OsgiUtil.toString(configs.get(SALESFORCEPATH), null);
//		log.info("Configure: ftp server="+server+"; username="+user+"; password="+password+"; directory="+directory);	
		log.info("Configure: ftp server="+server+"; username="+user+"; directory="+directory+"salesforcePath=" + salesforcePath);	
		ftp = new FTPSClient();
		ftp.setDefaultTimeout(TIME_OUT_IN_MS);
		try {
			rr= resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
		readTimeStamp();
		resetDate();
	}
	
	public Date getTimeStamp() {
		readTimeStamp();
		return lastUpdated;
	}
	
	private void readTimeStamp(){
		try {
			ValueMap props = rr.getResource(CONFIG_PATH).adaptTo(ValueMap.class);
			if (props.containsKey(LAST_UPD)) {
				Date date = props.get(LAST_UPD, Date.class);
				lastUpdated = date==null ? lastUpdated : date;
				log.info("Last import time stamp=" + ZIP_DATE_FORMAT.format(lastUpdated));	
			}
		} catch (Exception e) {
			log.error("Fail to read " + CONFIG_PATH);
		}
	}
	private void writeTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(lastUpdated);
		try {
			Session session = rr.adaptTo(Session.class);
			Node configNode = JcrUtil.createPath(CONFIG_PATH, "nt:unstructured", session);
			configNode.setProperty(LAST_UPD, calendar);
			session.save();
			log.info("lastUpdated " + ZIP_DATE_FORMAT.format(lastUpdated)+"written to "+CONFIG_PATH);
		} catch (Exception e) {
			log.error("Fail to store the timeStamp.");
			e.printStackTrace();
		}

	}

	private void getFiles() throws IOException {
		readTimeStamp();
		log.info("checking files after "+ZIP_DATE_FORMAT.format(lastUpdated));
		//control connection being idle can cause disconnecting from server during data transfer
		//this is to send NOOP every TIME_OUT secs.
		ftp.setControlKeepAliveTimeout(TIME_OUT_IN_SEC); 

		if (ftp.changeWorkingDirectory(directory)) {
			log.info("Successfully switch to directory " + ftp.printWorkingDirectory());
			//tracking the latest timeStamp for this update
			ftp.setFileType(FTP.BINARY_FILE_TYPE, FTP.NON_PRINT_TEXT_FORMAT); 
			
			Date latest = lastUpdated;
			FTPFile[] files = ftp.listFiles();
			log.info(ftp.getReplyString());
			errors = new HashMap<String, Map<String,Exception>>();
			List<String> newFiles = new ArrayList<String>();
			for (FTPFile file:files) {
				String zipName = file.getName();
				int type = file.getType();
				//log.info("Checking Individual File: " + zipName);
				if (type == FTPFile.FILE_TYPE && zipName.matches(ZIP_REGEX)) {
					try {
						Date date = ZIP_DATE_FORMAT.parse(zipName.substring(9, zipName.indexOf(".zip")));
						if(date.after(lastUpdated)){
							if(date.after(latest)){//update latest date
								latest = date;
							}
							newFiles.add(zipName);
						} 
					}catch(ParseException e){
						log.error("Fail to read date in the zip name: "+zipName, e);
					}
				}
			}
			Collections.sort(newFiles);//sort to make sure you read the old data first
			for (int i = 0; i < newFiles.size(); i++) {
				String zip = newFiles.get(i);
				Map<String,Exception> errorMap = new HashMap<String, Exception>();
				errors.put(zip, errorMap);
				try {
					readZip(zip);
				} catch (Exception e) {
					log.error("Exception thrown when retrieving the zip file: "+zip, e);
					errorMap.put("error",  e);
				}
				//clean errors list if no error recorded
				if (errorMap.size() == 0) {
					errors.remove(zip);
				}
			}

			log.info("finish retrieving up to date files.");
			lastUpdated=latest;
			writeTimeStamp();
		}else{
			log.error("Not able to find the directory /"+directory+" on the server");
		}


	}
	private void readZip(String zipName) throws Exception {
		log.info("Retrieving " + zipName);
		InputStream input = ftp.retrieveFileStream(zipName);
		if (input == null) { 
			throw new GirlScoutsException(null, "data connection cannot be opened.");
		}
		try {
			ZipInputStream inputStream = new ZipInputStream(input);
			ZipEntry entry = inputStream.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				if(fileName.startsWith("__macosx")) continue;
				//read instructions.json
				if(fileName.endsWith("instructions.json")){
					log.info("Reading " + fileName);
					String jsonString = IOUtils.toString(inputStream, "UTF-8").trim();
					log.info("JSON Length " + jsonString.length());
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						try {
							final JSONObject jsonObject = jsonArray.getJSONObject(i);
							parseJson(jsonObject);
						} catch(Exception e) {//catch inside loop, continue to next file
							log.error(e.getMessage());
							errors.get(zipName).put("entry"+i, e);
						}
					}
					log.info("End of " + fileName);
					//TODO: if we only need to read one instruction.json", break here
					break;
				}
				entry = inputStream.getNextEntry();
			}
			inputStream.close();
		} catch (Exception e) {
			throw new GirlScoutsException(e, "Error reading file:"+zipName);
		} finally {
			if (!ftp.completePendingCommand()) {
				throw new GirlScoutsException(null,	"FTP CompletePendingCommand returns false." + " File " + zipName + " transfer failed.");
			}
			input.close();
			log.info("End of reading " + zipName);
		}
	}

	private void parseJson(JSONObject jsonObject) throws JSONException, GirlScoutsException {
		String action =jsonObject.getString("action");
		JSONObject payload = jsonObject.getJSONObject("payload");
		if (action.equalsIgnoreCase("PUT")) {
			//We do the whole process with two steps: first create the node in author, then we replicate it. 
			//Let's try to create the node first in author
			String newPath;
			try {
				//This is where we create the node.
				newPath = put(payload);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new GirlScoutsException(e, "Fail to PUT the payload: "+payload);
			}
			//Then we replicate the node here
			try {
				replicateNode(newPath);
			} catch (Exception e) {
				throw new GirlScoutsException(e, "Fail to ACTIVIATE the Node from author with path: " + newPath);
			}
		} else if (action.equalsIgnoreCase("DELETE")) {
			try {
				delete(payload);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new GirlScoutsException(e, "Fail to DELETE the payload: "+payload);
			}
		} else {
			throw new GirlScoutsException(null, "No PUT or DELETE action found in the json object: "+jsonObject);
		}
	}
	
	private void replicateNode(String newPath) throws GirlScoutsException {
		Session session = rr.adaptTo(Session.class);
		try {
			replicator.replicate(session, ReplicationActionType.ACTIVATE, newPath);
		} catch (NullPointerException npe) {
			log.error("Replicator is null for path: " + newPath);
			throw new GirlScoutsException(npe, "Fail to Activates the path with NPE: " + newPath);
		} catch (Exception e) {
			//e.printStackTrace();
			throw new GirlScoutsException(e, "Fail to Activates the path: " + newPath + ". " + e.getMessage());
		}
		log.info("<---------EVENT ACTIVIATED: " + newPath + " -------->");
	}
	
	private String put(JSONObject payload) throws GirlScoutsException {

		String councilCode = getString(payload, _councilCode);
		String id = getString(payload, _id);
		String title = getString(payload, _title);
		String start = getString(payload, _start);

		if (councilCode == null || id == null || title == null || start == null) {
			throw new GirlScoutsException(null, "Required fields (councilCode/id/title/start) missing.");
		}
		
		String councilName = councilMap.getCouncilName(councilCode);
		if (councilName == null) {
			throw new GirlScoutsException(null, "No mapping found for council code: " + councilCode);
		}

		Calendar calendar = Calendar.getInstance();
		Date startDate;
		try {
			startDate = NO_TIMEZONE_FORMAT.parse(start);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new GirlScoutsException(e,"start date format error: " + start);
		} 

		
		//We use calendar now only for its YEAR field to create the directory.
		//We just save the whole string to the data node now.
		calendar.setTime(startDate);
		int year = calendar.get(Calendar.YEAR);	
		String parentPath = "/content/" + councilName + "/en/sf-events-repository/" + year;
		Session session =  rr.adaptTo(Session.class);
		
		//Create event page
		try {
			//need session.save() to persist
			JcrUtil.createPath(parentPath, "cq:Page", session);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new GirlScoutsException(e, "Fail to get/create parent path: " + parentPath);
		}

		//check existence to see if it is a add or update
		Page eventPage = getEvent(parentPath, id);//query the id to get most accurate result
		boolean isAdd = (eventPage == null);//if eventPage not null, then it is an update

		try {
			if (isAdd) {
				//create new event page, using template, can edit through scaffolding later 
				eventPage = rr.adaptTo(PageManager.class).create(parentPath, null, "/apps/girlscouts/templates/event-page", title, false);
			} 
			Node jcrNode = eventPage.getContentResource().adaptTo(Node.class);
			//add/update title
			jcrNode.setProperty("jcr:title", title);
			//add/update tags
			try {
				JSONArray jsonTags = payload.getJSONArray(_tags);
				if (jsonTags != null) { 
					int len = jsonTags.length();
					String[] tags = new String[len];
					for (int i = 0; i < len; i++) { 
						String tagString = _globalTagNamespace + ":" +jsonTags.getString(i);
						if (rr.adaptTo(TagManager.class).resolve(tagString) != null){
							tags[i]= tagString;
						} else {
							//if invalid tags found, throw exception and the event page is not saved
							throw new GirlScoutsException(null, "Invalid Tag String: " + tagString + ", no such tag exist under /etc/tags");
						}
					} 
					jcrNode.setProperty("cq:tags", tags);
				} 
			} catch(JSONException e) {
				log.info("no tags found for the payload");
			}
			//add data node
			Node dataNode = JcrUtil.createPath(jcrNode, "data", false, null, "nt:unstructured", session, false);
			setNodeProperties(id, dataNode, start, payload);
			session.save();
		} catch (WCMException e) {
			e.printStackTrace();
			throw new GirlScoutsException(e,"Fail to create event page under "+parentPath);

		} catch (RepositoryException e){
			e.printStackTrace();
			throw new GirlScoutsException(e, "Exception throw when adding data to"+eventPage.getPath()+"/jcr:content");
		}
		if (isAdd) {
			log.info("<---------EVENT ADDED: Event page [path="+eventPage.getPath()+"; eid="+id+"] created successfully.-------->");	
		} else {
			log.info("<---------EVENT UPDATED: Event page [path="+eventPage.getPath()+"; eid="+id+"] updated successfully.-------->");	
		}
		return eventPage.getPath();
	}
	
	private void setNodeProperties(String id, Node dataNode, String start, JSONObject payload) throws RepositoryException, GirlScoutsException {
		dataNode.setProperty("eid", id);
		dataNode.setProperty("start", start);
		dataNode.setProperty("address", getString(payload, _address));
		dataNode.setProperty("details", getString(payload, _details));
		dataNode.setProperty("locationLabel", getString(payload, _location));
		dataNode.setProperty("region", getString(payload, _region));
		dataNode.setProperty("srchdisp", getString(payload, _description));
		dataNode.setProperty("memberOnly", getString(payload, _member_only));
		dataNode.setProperty("timezone", getString(payload, _timezone));
		String registerVal = getString(payload, _register);
		if (registerVal != null)
			registerVal = registerVal.trim();
		//they stated that it's always Field NA in Salesforce, we may not need an if case at all
		//We will keep it for now
		if (!"Field NA in Salesforce".equals(registerVal)) {
			dataNode.setProperty("register", registerVal);
		} else {
			dataNode.setProperty("register", salesforcePath + id);
//			dataNode.setProperty("register", "https://gsuat-gsmembers.cs17.force.com/members/Event_join?EventId=" + id);
//			dataNode.setProperty("register", "https://gsmembers.force.com/members/Event_join?EventId=" + id);
		}
		String colorVal = getString(payload, _color);
		//they stated that it's always "Field NA in Salesforce".
		
		if (!"Field NA in Salesforce".equals(colorVal)) {
			dataNode.setProperty("color", colorVal);
		} else {
			dataNode.setProperty("color", "#00AE58");
		}
		String thumbImageVal = getString(payload, _thumbImage);
		if (thumbImageVal == null || "Field NA in Salesforce".equals(thumbImageVal)) {
			dataNode.setProperty("thumbImage", "/content/dam/girlscouts-shared/images/events/GS_ART_profiles_123x123.png");
		} else {
			dataNode.setProperty("thumbImage", thumbImageVal);
		}
		String priceRangeVal = getString(payload, _priceRange);
		if (priceRangeVal != null) {
			dataNode.setProperty("priceRange", priceRangeVal);
		}
		//We will use the image link provided. if no image is provide, we use a default image
		String imageVal = getString(payload, _image);
		if (imageVal == null) {
			//use default image
			dataNode.setProperty("image", "/content/dam/girlscouts-shared/images/events/GS_servicemark_602x237.png");
		} else {
			dataNode.setProperty("image", imageVal);
		}
		String end = getString(payload, _end);
		if (end != null) {
			dataNode.setProperty("end", end);
		}
		String visibleDate = getString(payload, _visibleDate);
		if (visibleDate != null) {
			dataNode.setProperty("visibleDate", visibleDate);
		}
	}
	
	private void delete(JSONObject payload) throws GirlScoutsException, WCMException, ReplicationException {
		String councilCode = getString(payload,_councilCode);
		String id = getString(payload, _id);
		if (councilCode == null || id == null) {
			throw new GirlScoutsException(null, "Required fields (councilCode/id) missing.");
		}
		String councilName = councilMap.getCouncilName(councilCode);
		if (councilName == null) {
			throw new GirlScoutsException(null, "No mapping found for council code: "+councilCode);
		}
		String searchPath = "/content/" + councilName + "/en/sf-events-repository";
		Page pageToDelete = getEvent(searchPath, id);
		if (pageToDelete == null) {
			throw new GirlScoutsException(null, "Event [id="+id+"] could not be found under "+searchPath);
		}
		Session session = rr.adaptTo(Session.class);
		replicator.replicate(session, ReplicationActionType.DEACTIVATE, pageToDelete.getPath());
		rr.adaptTo(PageManager.class).delete(pageToDelete, false);

		log.info("<---------EVENT DELETED: Event page [id=" + id + ",path=" + pageToDelete.getPath() + "] deleted successfully.--------->");


	}
	private Page getEvent(String path, String id) {
//		String sql = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE(s, '"
//				+ path + "') AND [eid]='"+id+"'";
		String sql = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, '"
				+ path + "') AND [eid]='" + id + "'";
		log.info("SQL " + sql);
		try {
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				return it.next().getParent().getParent().adaptTo(Page.class);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	private boolean ftpLogin() throws SocketException, IOException{
		if (ftp == null) return false;
		if (server == null || server.isEmpty()) return false;
		ftp.connect(server); //TODO: need port number!
		// After connection attempt, check the reply code to verify
		// success.
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			log.error("FTP server refused connection.");
			return false;
		}
		log.info("Connected to " + server + ".");	     
		log.info(ftp.getReplyString());
		
		ftp.enterLocalPassiveMode();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.execPBSZ(0);
        ftp.execPROT("P");

		if (!ftp.login(user, password)) {
			log.error("FTP Login failed.");
			return false;
		}
		log.info("FTP login successfully.");
		return true;

	}
	public void run() {
		//TODO: Change the following if case so that it uses policy = ConfigurationPolicy.REQUIRE to check for publishing servers 
		//More info: http://aemfaq.blogspot.com/search/label/runmode
		//http://stackoverflow.com/questions/19292933/creating-osgi-bundles-with-different-services
		if (isPublisher()) {
			return;
		}
		log.info("Running SF Events Importer..." );
		try{
			if (ftpLogin()) {
				getFiles();
			} else {
				throw new GirlScoutsException(null, "ftpLogin() return false. Failed to login."); 
			}
		} catch(IOException e) {
			log.error("IOException caught during access to FTP server",e);
			//e.printStackTrace();
		} catch(Exception e) {
			log.error("Exception caught during access to FTP server",e);
			//e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.logout();
					ftp.disconnect();
					log.info("Disconnected from the FTP server");
				} catch (IOException ioe) {
					log.error("IOException catched when trying to disconnect ftp server");
				}
			} 
			log(errors);
		}
	}
	
	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
        }
		return false;
	}
	
	private void log(Map<String, Map<String, Exception>> errors) {
		if(errors == null || errors.isEmpty()) return;
		Date rightNow = new Date();
		try {
			Session session = rr.adaptTo(Session.class);
			String path = CONFIG_PATH + "/error-log/" + ERROR_LOG_DATE_FORMAT.format(rightNow);
			Node dateNode = JcrUtil.createPath(path, "nt:unstructured", session);
			for (String zipName : errors.keySet()) {
				Map<String, Exception> errMap = errors.get(zipName);
				if (errMap != null && !errMap.isEmpty()) {
					Node zipNode = dateNode.addNode(zipName, "nt:unstructured");
					for (String key:errMap.keySet()) {
						zipNode.setProperty(key, genErrorMsg(errMap.get(key)));
					}
				}
			}
			session.save();
			log.info("Some event files import failed on " + rightNow + ". Error node stored: " + path);

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	//recursively trace error messages.
	private String genErrorMsg(Throwable exception) {
		String msg = exception.getMessage();
		if (exception.getCause() == null) {
			return msg;
		}
		return msg + "\n" + genErrorMsg(exception.getCause());
	}

	private String getString(JSONObject payload, String key) {
		try {
			return payload.has(key) ? payload.getString(key) : null;
		} catch (JSONException e) {
			return null;
		}

	}



	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		this.ftp=null;
		resetDate();
		rr.close();
		log.info("Events Import Service Deactivated.");
	}
	//set the date to "the epoch", namely January 1, 1970, 00:00:00 GMT.
	private void resetDate() {
		lastUpdated= new Date(0);
	}





}
