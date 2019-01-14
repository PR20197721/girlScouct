package org.girlscouts.gsactivities.dataimport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar; 
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.gsactivities.dataimport.EventsImport;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.common.exception.GirlScoutsException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;


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
	@Property(name = "scheduler.period",label="scheduler.period", description="time interval in seconds", longValue=10),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
	@Property(name="scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "server",label = "FTP Server Address", description = "FTP server"),
	@Property(name = "username", label="username"),
	@Property(name = "password", label="password"),
	@Property(name = "directory", label="directory name"),
	@Property(name = "salesforcepath", label="Salesforce redirect path", description="This path is used to redirect links to salesforce"),
	@Property(name = "errorRecipients", unbounded = PropertyUnbounded.ARRAY, cardinality=10, label = "emails to recieve error notification")
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
	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	public GSEmailService gsEmailService;

	private Map<String, Object> serviceParams;
	//configuration fields
	public static final String SERVER = "server";
	public static final String USER = "username";
	public static final String PASSWORD = "password";
	public static final String DIR = "directory";
	public static final String LAST_UPD = "lastUpdated";
	public static final String SALESFORCEPATH = "salesforcepath";
	public static final String ERROR_RECIPIENTS = "errorRecipients";
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
	
	private StringBuilder errorString;

	private String server, user, password, directory, salesforcePath;
	private String[] errorRecipients;
	//keep track of the latest time stamp of import.
	private Date lastUpdated= new Date(Long.MIN_VALUE);
	//map to track the error
	
	private boolean author;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		configs.get(SERVER);
		this.author = settingsService.getRunModes().contains("author");
		this.server = PropertiesUtil.toString(configs.get(SERVER), null);
		this.user = PropertiesUtil.toString(configs.get(USER), null);
		this.password = PropertiesUtil.toString(configs.get(PASSWORD), null);
		this.directory = PropertiesUtil.toString(configs.get(DIR), null);
		this.salesforcePath = PropertiesUtil.toString(configs.get(SALESFORCEPATH), null);
		this.errorRecipients = PropertiesUtil.toStringArray(configs.get(ERROR_RECIPIENTS));
		log.info("Configure: ftp server=" + server + "; username=" + user + "; directory=" + directory
				+ "salesforcePath=" + salesforcePath);
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		resetDate();
		log.debug("Events Import Service Activated");
	}
	
	public void run() {
		if (this.author) {
			ResourceResolver rr = null;
			FTPSClient ftp = null;
			errorString = new StringBuilder();
			try {
				log.info("Running SF Events Importer...");
				log.info("Creating FTPS Client");
				rr = resolverFactory.getServiceResourceResolver(serviceParams);
				ftp = new FTPSClient(false);
				if (ftpLogin(ftp)) {
					getFiles(rr, ftp);
				} else {
					errorString.append("<p>Unable to login to FTP</p>");
					log.error("ftpLogin() return false. Failed to login.");
					throw new GirlScoutsException(null, "ftpLogin() return false. Failed to login.");
				}
			} catch (Exception e) {
				log.error("Unable to process Events FTP files due to: ", e);
				try {
					gsEmailService.sendEmail("GSActivities Syncing Process Failed Due to Unresolved Errors",
							Arrays.asList(errorRecipients),
							"<p>The following errors were encountered while events were being imported.</p>"
									+ "<p>----------------</p><br>" + errorString.toString());
				} catch (Exception e1) {
					log.error("Unable to send email due to exception being thrown", e1);
				}
			} finally {
				try {
					if (ftp != null && ftp.isConnected()) {
						ftp.logout();
						ftp.disconnect();
						log.info("Disconnected from the FTP server");
						ftp = null;
					}
				} catch (Exception e) {
					log.error("error while closing ftp connection: ", e);
				}
				try {
					if (rr != null) {
						rr.close();
					}
				} catch (Exception e) {
					log.error("error while closing resource resolver: ", e);
				}
			}
		}
	}
	
	public Date getTimeStamp(ResourceResolver rr) {
		readTimeStamp(rr);
		return lastUpdated;
	}
	
	private void readTimeStamp(ResourceResolver rr) {
		try {
			ValueMap props = rr.getResource(CONFIG_PATH).adaptTo(ValueMap.class);
			if (props.containsKey(LAST_UPD)) {
				Date date = props.get(LAST_UPD, Date.class);
				lastUpdated = date==null ? lastUpdated : date;
				log.info("Last import time stamp=" + ZIP_DATE_FORMAT.format(lastUpdated));
			}
		} catch (Exception e) {
			log.error("Failed to read " + CONFIG_PATH);
		}
	}

	private void writeTimeStamp(ResourceResolver rr) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(lastUpdated);
		try {
			Session session = rr.adaptTo(Session.class);
			Node configNode = JcrUtil.createPath(CONFIG_PATH, "nt:unstructured", session);
			configNode.setProperty(LAST_UPD, calendar);
			session.save();
			log.info("The last updated timestamp has been set as " + ZIP_DATE_FORMAT.format(lastUpdated) + " and written to "
					+ CONFIG_PATH);
		} catch (Exception e) {
			errorString.append("<p>Failed to store the timestamp.</p>");
			log.error("Failed to store the timeStamp.", e);
		}
	}

	private void getFiles(ResourceResolver rr, FTPSClient ftp) throws IOException, GirlScoutsException {
		readTimeStamp(rr);
		log.info("Checking files after " + ZIP_DATE_FORMAT.format(lastUpdated));
		//control connection being idle can cause disconnecting from server during data transfer
		// this is to send NOOP every TIME_OUT secs.
		if (ftp.changeWorkingDirectory(directory)) {
			log.info("Successfully switched to directory " + ftp.printWorkingDirectory());
			ftp.setFileType(FTP.BINARY_FILE_TYPE, FTP.NON_PRINT_TEXT_FORMAT);
			Date latest = lastUpdated;
			FTPFile[] files = ftp.listFiles();
			if (files != null) {
				log.info("Retrieving list of " + files.length + " files");
				List<String> newFiles = new ArrayList<String>();
				for (FTPFile file : files) {
					String zipName = file.getName();
					int type = file.getType();
					if (type == FTPFile.FILE_TYPE && zipName.matches(ZIP_REGEX)) {
						try {
							Date date = ZIP_DATE_FORMAT.parse(zipName.substring(9, zipName.indexOf(".zip")));
							if (date.after(lastUpdated)) {
								if (date.after(latest)) {// update latest date
									newFiles.add(zipName);
								}
							}
						} catch (ParseException e) {
							errorString.append("<p>Invalid format for date in zipfile title: " + zipName + ".</p>");
							log.error("Invalid format for date in zipfile title: " + zipName, e);
							throw new GirlScoutsException(e,
									"Invalid file name format supplied. Unable to proceeed without comporromising data integrity.");
						}
					}
				}
				if (!newFiles.isEmpty()) {
					// Sort to make sure the older files are read first
					Collections.sort(newFiles);
					int lastIndex = processFtpFiles(newFiles, rr, ftp);
					if (lastIndex >= 0) {
						String zipName = newFiles.get(lastIndex);
						if (zipName != null) {
							try {
								latest = ZIP_DATE_FORMAT.parse(zipName.substring(9, zipName.indexOf(".zip")));
							} catch (ParseException e) {
								// This is a theoretically unreachable place in
								// the
								// code
								log.error("unable to parse the date of the latest file name.");
							}
						}
					}
					// Check that all the files were processed
					if (lastIndex < newFiles.size() - 1) {
						lastIndex++;
						String failedZipName = newFiles.get(lastIndex);
						errorString.append("<p>Failed to process the file " + failedZipName + " from the server.</p>");
						log.error("Failed to process the file " + failedZipName + " from the server.");
					}
					lastUpdated = latest;
					writeTimeStamp(rr);
				} else {
					log.error("No new files found");
				}
			}
		}else{
			errorString.append("<p>Unable to find the event file directory " + directory + " on the server.</p>");
			log.error("Not able to find the directory /" + directory + " on the server");
		}

	}
	
	private int processFtpFiles(List<String> newFiles, ResourceResolver rr, FTPSClient ftp) {
		//Gather all the up to date activity instruction files in a FIFO queue
		Queue<ZipData> zipQueue = new LinkedBlockingQueue<ZipData>();
		int lastIndex = -1;
		for (String zip : newFiles) {
			log.info("Loading file " + zip);
			try{
				List<JsonObject> output = this.parseFtpInput(ftp, zip);
				ZipData data = new ZipData(zip, output);
				zipQueue.add(data);
				lastIndex++;
			} catch (Exception e) {
				log.error("Exception thrown when loading file " + zip + " into the processing queue: ", e);
				errorString.append("<p>Exception thrown when loading file "+ zip + "into the processing queue: with message: " + e.getMessage() + ".</p>");
				//If a file fails processing prevent the loading of subsequent files to avoid missing deltas
				break;
			}
		}
		//Process all the files that were successfully parsed
		while (!zipQueue.isEmpty()) {
			ZipData data = zipQueue.remove();
			List<JsonObject> listOfObjects = data.getContents();
			if(listOfObjects != null){
				//A number of errors occurring during updating the activities are expected. Therefore none of the errors trigger the process interruption flag
				try {
					processForDelayedActivation(listOfObjects, rr);
				} catch (ValueFormatException e) {
					log.error("Encountered exception while parsing JSON for file " + data.getZipName(), e);
					
					errorString.append("<p>Encountered exception while parsing JSON for file "+ data.getZipName() + "with message: " + e.getMessage() + ".</p>");
				} catch (PathNotFoundException e) {
					log.error("Unable to find path for node while updating activities " + data.getZipName(), e);
					
					errorString.append("<p>Unable to find path for node while updating activities "+ data.getZipName() + "with message: " + e.getMessage() + ".</p>");
				} catch (IllegalStateException e) {
					log.error("Encountered exception while accessing the repository for file " + data.getZipName(), e);
						
					errorString.append("<p>Encountered exception while accessing the repository "+ data.getZipName() + "with message: " + e.getMessage() + ".</p>");
				} catch (RepositoryException e) {
					log.error("Encountered exception while accessing the repository for file " + data.getZipName(), e);
					
					errorString.append("<p>Encountered exception while accessing the repository "+ data.getZipName() + "with message: " + e.getMessage() + ".</p>");
				} catch (Exception e) {
					log.error("Encountered exception while parsing JSON for file " + data.getZipName(), e);
					
					errorString.append("<p>Encountered exception while parsing JSON for file "+ data.getZipName() + "with message: " + e.getMessage() + ".</p>");
				} 
			}
		}
		//Return the index of the last file processed by the method
		return lastIndex;
	}
	
	private void processForDelayedActivation(List<JsonObject> input, ResourceResolver rr) throws ValueFormatException,
			PathNotFoundException, IllegalStateException, RepositoryException {
		Resource etcRes = rr.resolve("/etc");
		Node etcNode = etcRes.adaptTo(Node.class);
        Resource gsPagesRes = rr.resolve("/etc/gs-delayed-activations");
        Node gsPagesNode = null;
        if(gsPagesRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			gsPagesNode = etcNode.addNode("gs-delayed-activations");
        }else{
	        gsPagesNode = gsPagesRes.adaptTo(Node.class);
        }
        ArrayList<String> toActivate = new ArrayList<String>();
    	String [] pagesProp;
    	
    	if(!gsPagesNode.hasProperty("pages")){
    		pagesProp = toActivate.toArray(new String[0]);
    	}else{
    		Value[] propValues = gsPagesNode.getProperty("pages").getValues();
    		for(int j=0; j<propValues.length; j++){
    			if(null != propValues[j]){
    				toActivate.add(propValues[j].getString());
    			}
    		}
		}
		for (JsonObject object : input) {
			parseJson(object, toActivate, rr);
		}
		pagesProp = toActivate.toArray(new String[0]);
    	gsPagesNode.setProperty("pages", pagesProp);
    	Session session = rr.adaptTo(Session.class);
		session.save();
	}
	
	private List<JsonObject> parseFtpInput(FTPSClient ftp, String zip) throws IOException, GirlScoutsException {
		InputStream input = null;
		ArrayList<JsonObject> results = new ArrayList<JsonObject>();
		input = ftp.retrieveFileStream(zip);
		if(input == null){
			return null;
		} else{
			ZipInputStream inputStream = null;
			try {
				inputStream = new ZipInputStream(input);
				ZipEntry entry = inputStream.getNextEntry();
				while (entry != null) {
					String fileName = entry.getName();
					if(fileName.startsWith("__macosx")) continue;
					//read instructions.json
					if(fileName.endsWith("instructions.json")){
						String jsonString = IOUtils.toString(inputStream, "UTF-8").trim();
						JsonParser parser = new JsonParser();
						JsonElement json = parser.parse(jsonString);
						JsonArray jsonArray = json.getAsJsonArray();
						for (int i = 0; i < jsonArray.size(); i++) {
							final JsonObject JsonObject = jsonArray.get(i).getAsJsonObject();
							results.add(JsonObject);
						}
						break;
					}
					entry = inputStream.getNextEntry();
				}
			} catch (Exception e) {
				throw new GirlScoutsException(e, "Error reading file:"+zip);
			} finally {
				if (!ftp.completePendingCommand()) {
					throw new GirlScoutsException(null,	"FTP CompletePendingCommand returns false." + " File " + zip + " transfer failed.");
				}
				try{ 
					if(inputStream != null)
						inputStream.close();
				}catch(Exception e){
					log.error("Error encountered while closing input stream", e);
				}
				input.close();
			}
		}
		return results;
	}

	private void parseJson(JsonObject JsonObject, ArrayList<String> toActivate, ResourceResolver rr) {
		String action = JsonObject.get("action").getAsString();
		JsonObject payload = JsonObject.getAsJsonObject("payload");
		String payloadId = payload.get("id").getAsString();
		if (action.equalsIgnoreCase("PUT")) {
			//We do the whole process with two steps: first create the node in author, then we replicate it. 
			//Let's try to create the node first in author
			String newPath;
			try {
				//This is where we create the node.
				newPath = put(payload, rr);
			} catch (Exception e) {
				log.error("Failed to PUT payload " + payloadId, e);
				return;
			}
			//Add the node to the running activation list
			toActivate.add(newPath);
		} else if (action.equalsIgnoreCase("DELETE")) {
			try {
				delete(payload, rr);
			} catch (Exception e) {
				log.error("Failed to DELETE Payload " + payloadId + " " + e.getMessage());
			}
		} else {
			errorString.append("<p>No Valid action was avilable for payload " + payloadId + "</p>");
			log.error("No Valid action was available for payload " + payloadId);
		}
	}
	
	private String put(JsonObject payload, ResourceResolver rr) throws GirlScoutsException {
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
			errorString.append("<p>Failed to parse date: " + start + "</p>");
			e.printStackTrace();
			throw new GirlScoutsException(e,"start date format error: " + start);
		}
		//We use calendar now only for its YEAR field to create the directory.
		//We just save the whole string to the data node now.
		calendar.setTime(startDate);
		int year = calendar.get(Calendar.YEAR);	
		String parentPath = "/content/" + councilName + "/en/sf-events-repository/" + year;
		Session session = rr.adaptTo(Session.class);
		//Create event page
		try {
			//need session.save() to persist
			JcrUtil.createPath(parentPath, "cq:Page", session);
		} catch (RepositoryException e) {
			errorString.append("<p>Failed when retrieving or creating parent path:  " + parentPath + " on the server due to error: " + e.getMessage() + "</p>");
			e.printStackTrace();
			throw new GirlScoutsException(e, "Fail to get/create parent path: " + parentPath);
		}
		//check existence to see if it is a add or update
		Page eventPage = getEvent(parentPath, id, rr);// query the id to get
														// most accurate result
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
			JsonArray jsonTags = payload.getAsJsonArray(_tags);
			if (jsonTags != null) {
				int len = jsonTags.size();
				String[] tags = new String[len];
				for (int i = 0; i < len; i++) {
					String tagString = _globalTagNamespace + ":" + jsonTags.get(i).getAsString();
					if (rr.adaptTo(TagManager.class).resolve(tagString) != null) {
						tags[i] = tagString;
					} else {
						// if invalid tags found, throw exception and the event
						// page is not saved
						throw new GirlScoutsException(null,
								"Invalid Tag String: " + tagString + ", no such tag exist under /etc/tags");
					}
				} 
				jcrNode.setProperty("cq:tags", tags);
			}
			//add data node
			Node dataNode = JcrUtil.createPath(jcrNode, "data", false, null, "nt:unstructured", session, false);
			setNodeProperties(id, dataNode, start, payload);
			session.save();
		} catch (WCMException e) {
			e.printStackTrace();
			errorString.append("<p>Failed to create event page under "+parentPath+ " due to error: "+ e.getMessage()+ "</p>");
			throw new GirlScoutsException(e,"Fail to create event page under "+parentPath);

		} catch (RepositoryException e){
			errorString.append("<p>Exception throw when adding data to"+eventPage.getPath()+"/jcr:content with error: " + e.getMessage() + "</p>");
			e.printStackTrace();
			throw new GirlScoutsException(e, "Exception throw when adding data to"+eventPage.getPath()+"/jcr:content");
		}
		if (isAdd) {
			log.info(
					"Event ADDED: Event page [path=" + eventPage.getPath() + "; eid=" + id + "] created successfully.");
		} else {
			log.info("Event UPDATED: Event page [path=" + eventPage.getPath() + "; eid=" + id
					+ "] updated successfully.");
		}
		return eventPage.getPath();
	}
	
	private void setNodeProperties(String id, Node dataNode, String start, JsonObject payload)
			throws RepositoryException, GirlScoutsException {
		dataNode.setProperty("eid", id);
		dataNode.setProperty("start", start);
		dataNode.setProperty("address", getString(payload, _address));
		dataNode.setProperty("details", getString(payload, _details));
		dataNode.setProperty("locationLabel", getString(payload, _location));
		dataNode.setProperty("region", getString(payload, _region));
		dataNode.setProperty("srchdisp", getString(payload, _description));
		dataNode.setProperty("memberOnly", getString(payload, _member_only));
		dataNode.setProperty("timezone", getString(payload, _timezone));
		String registerVal = getString(payload,_register);
		//they stated that it's always Field NA in Salesforce, we may not need an if case at all
		//We will keep it for now
		if (!"Field NA in Salesforce".equals(registerVal)) {
			dataNode.setProperty("register", registerVal);
		} else {
			dataNode.setProperty("register", salesforcePath + id);

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
			imageVal = imageVal.replaceAll("https?:\\/\\/www\\.[^\\.]+\\.org", "");
			dataNode.setProperty("imagePath", imageVal);
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
	
	private void delete(JsonObject payload, ResourceResolver rr)
			throws GirlScoutsException, WCMException, ReplicationException {
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
		Page pageToDelete = getEvent(searchPath, id, rr);
		if (pageToDelete == null) {
			throw new GirlScoutsException(null, "Event [id="+id+"] could not be found under "+searchPath);
		}
		Session session = rr.adaptTo(Session.class);
		replicator.replicate(session, ReplicationActionType.DEACTIVATE, pageToDelete.getPath());
		rr.adaptTo(PageManager.class).delete(pageToDelete, false);

		log.info("EVENT DELETED: Event page [id=" + id + ",path=" + pageToDelete.getPath() + "] deleted successfully.");
	}

	private Page getEvent(String path, String id, ResourceResolver rr) {
//		String sql = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE(s, '"
//				+ path + "') AND [eid]='"+id+"'";
		String sql = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, '"
				+ path + "') AND [eid]='" + id + "'";
		//Can be turned on for debugging
		//log.info("SQL " + sql);
		try {
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				return it.next().getParent().getParent().adaptTo(Page.class);	
			}
		} catch (Exception e) {
			errorString.append("<p>Exception thrown when getting event at: " +path+ " with error: " + e.getMessage() + "</p>");
			e.printStackTrace();
		} 
		return null;
	}

	private boolean ftpLogin(FTPSClient ftp) throws SocketException, IOException {
		if (ftp == null)
			return false;
		if (server == null || server.isEmpty())
			return false;
		ftp.setDefaultTimeout(TIME_OUT_IN_MS);
		ftp.setControlKeepAliveTimeout(TIME_OUT_IN_SEC);
		ftp.connect(server, 21);
		// After connection attempt, check the reply code to verify success
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			errorString.append("<p>CRITICAL ERROR: FTP Server refused connection.</p>");
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
			log.error("Unable FTP server.");
			errorString.append("<p>CRITICAL ERROR: Unable to login into FTP server.</p>");
			return false;
		}
		log.info("FTP login successfull.");
		return true;
	}

	private String getString(JsonObject payload, String key) {
		return payload.has(key) ? payload.get(key).getAsString() : null;
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		resetDate();
		log.info("Events Import Service Deactivated.");
	}
	//set the date to "the epoch", namely January 1, 1970, 00:00:00 GMT.
	private void resetDate() {
		lastUpdated= new Date(0);
	}
}
