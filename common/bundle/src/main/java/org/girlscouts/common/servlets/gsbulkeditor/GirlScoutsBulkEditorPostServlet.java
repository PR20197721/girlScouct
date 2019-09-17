package org.girlscouts.common.servlets.gsbulkeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.AccessControlException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.time.*;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.servlets.post.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.events.search.GSDateTime;
import org.girlscouts.common.events.search.GSDateTimeFormat;
import org.girlscouts.common.events.search.GSDateTimeFormatter;
import org.girlscouts.common.events.search.GSDateTimeZone;
import org.girlscouts.common.util.PageReplicationUtil;
import org.joda.time.DateTimeZone;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.apache.sling.api.servlets.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Girl Scouts Bulk Editor POST Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/services/gsbulkeditor/import" })
public class GirlScoutsBulkEditorPostServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DOCUMENT_PARAM = "document";
    public static final String INSERTEDRESOURCETYPE_PARAM = "insertedResourceType";
    public static final String ROOTPATH_PARAM = "./rootPath";
    public static final String IMPORT_TYPE_PARAM="importType";
    public static final String YEAR_PARAM = "year";

    public static final String DEFAULT_SEPARATOR = ",";
    public static final String META_DATA_LOCATION = "/jcr:content/metadata";
    
    private final String JCR_CONTENT = "jcr:content";
    private final String CQ_PAGE = "cq:Page";
    private final String CQ_PAGE_CONTENT = "cq:PageContent";
    private final String CQ_SCAFFOLDING = "cq:scaffolding";
    private final String CQ_TEMPLATE = "cq:template";
    private final String EVENT_TEMPLATE = "/apps/girlscouts/templates/event-page";
    private final String NT_UNSTRUCTURED = "nt:unstructured";
    private final String SLING_FOLDER = "sling:Folder";
    private final String DELAYED_ACTIVATION_PATH = "/etc/gs-activations/delayed";
    private final String STAG_ACTIVATION = "gs-stag-activation";
    private final String FORMS_TAG_DOMAIN = "forms_documents";
    private final String CATEGORIES_TAG_DOMAIN = "categories";
    private final String PROGRAM_LEVEL_TAG_DOMAIN = "program-level";
	/**
	 * Common path prefix
	 */
	public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

	/**
	 * Common path prefix
	 */
	public static final String PROPERTIES_PARAM = "cols";

	public static final String TIDY_PARAM = "tidy";

	public static final String DEFAULT_TIMEZONE = "US/Eastern";

	/**
	 * Property name replacements
	 */
	private static Map<String, String> PROPERTY_NAME_REPLACEMENTS = new HashMap<String, String>();
	static {
		PROPERTY_NAME_REPLACEMENTS.put("\\.", "_DOT_");
	}

    @Reference 
	private Replicator replicator;

	private Packaging packaging;
	private WorkflowService workflowService;

	@Reference
	private ResourceResolverFactory resolverFactory;
    
    int contactDelayInterval = 40;
    int documentDelayInterval = 10;
    int repDelayTimeInMinutes = 1;
    
    private Map<String, Object> serviceParams;
    
    private static Logger log = LoggerFactory.getLogger(GirlScoutsBulkEditorPostServlet.class);


	@Activate
	private void activate(ComponentContext context) {
		log.info("Girlscouts GS Bulkeditor POST Servlet Activated.");
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "gsbulkeditor");
		BundleContext bundleContext = context.getBundleContext();
		ServiceReference packagingRef = bundleContext.getServiceReference(Packaging.class.getName());
		packaging = (Packaging) bundleContext.getService(packagingRef);
		ServiceReference workflowServiceRef = bundleContext.getServiceReference(WorkflowService.class.getName());
		workflowService = (WorkflowService) bundleContext.getService(workflowServiceRef);
	}

    public void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response)
            throws ServletException, IOException {
		log.debug("Girlscouts GS Bulkeditor POST Servlet processing.");
		ResourceResolver rr = null;
		HtmlResponse htmlResponse = new HtmlResponse();
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			if (request.getRequestParameter(DOCUMENT_PARAM) != null) {
				InputStream in = request.getRequestParameter(DOCUMENT_PARAM).getInputStream();
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in, "windows-1252"));

				//request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM);
				//request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM).getString();
				String rootPath = request.getRequestParameter(ROOTPATH_PARAM) != null
						? request.getRequestParameter(ROOTPATH_PARAM).getString() : null;

				String importType = request.getRequestParameter(IMPORT_TYPE_PARAM) != null
						? request.getRequestParameter(IMPORT_TYPE_PARAM).getString() : "";
				String year = request.getRequestParameter(YEAR_PARAM) != null
						? request.getRequestParameter(YEAR_PARAM).getString() : "";
				if (rootPath != null) {
					Node rootNode = null;
	
					Resource resource = rr.getResource(rootPath);
	                if(resource!=null) {
	                    rootNode = resource.adaptTo(Node.class);
	                }
	
	                if(rootNode!=null) {
	                    //counter is "session unique id: created items will have their own unique ids, which will
	                    //avoid the case to have duplicate node names (using [1], [2] to differentiate).
	                	
	            		//GS: We don't want to let anyone import with a depth less than two
	                	int rootDepth = 0;
	                	String councilRoot = null;
	                    String councilName = "";
	                	try{
	                		rootDepth = rootNode.getDepth();
	                        //GS: Used to determine contact scaffolding property
	                        councilRoot = rootNode.getAncestor(2).getPath();
							htmlResponse.setStatus(500, "Could not determine council root");
	                        if(councilRoot.indexOf("/") != -1 && !councilRoot.endsWith("/")){
	                        	councilName = councilRoot.substring(councilRoot.lastIndexOf("/") + 1,councilRoot.length());
	                        }
	                	}catch(Exception e){
							htmlResponse.setStatus(500, "Can not Determine Root Node Depth and/or Council Name");
	                        htmlResponse.send(response, true);
							log.error("Can not Determine Root Node Depth and/or Council Name: " + e);
	                        return;
	                	}
	                	if(rootDepth >= 2){
							final CSVParser parser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"')
									.withIgnoreQuotations(false)
									.build();
							final CSVReader csvR = new CSVReaderBuilder(bufferReader).withCSVParser(parser).build();
		                    String[] headerArr = csvR.readNext();
		                    if (headerArr != null) {
		                        
		                    	List<String> headers = new LinkedList<String>(Arrays.asList(headerArr));
		                    	try{
			                    	//GS - We can't make lots of asset packages
			            			if(importType.equals("contacts")){
				                		
										htmlResponse = updateContacts(headers, rootPath, councilName, csvR, rr);
			            			} else if(importType.equals("events")){
										htmlResponse = updateEvents(headers, rootPath, year, csvR, rr);
			            			} else if(importType.equals("documents")){
										htmlResponse = updateFormMetadata(headers, rootPath, csvR, rr);
			            			} else{
										htmlResponse.setStatus(500, "Unsupported import type.");
			            			}
		            			
		                    	} catch(Exception e){
									htmlResponse.setStatus(500,
											"General Exception occured while processing content: " + e.getMessage());
									log.error("General Exception occured while processing content: " + e);
		                			throw e;
		                		} finally {
		                			//adminResolver.close();
		                		}
		                            
		                       
		                    } else {
								htmlResponse.setStatus(500, "Empty document");
		                    }
	                	} else {
							htmlResponse.setStatus(500, "Please increase the depth of the root path to at least two");
	                	}
	                } else {
						htmlResponse.setStatus(500, "Invalid root path");
	                }
	            } else {
					htmlResponse.setStatus(500, "No root path provided");
	            }
            
        } else {
				htmlResponse.setStatus(500, "No document provided");
			}
        
		} catch (Exception e) {
			log.error("Bulk Editor failed due to: " + e.getMessage());
			htmlResponse.setStatus(500, "Error occured : " + e.getMessage());
		} finally {
			try {
				rr.close();
			} catch (Exception e1) {
				log.error("Failed to close resource resolver: " + e1);
			}
		}

        htmlResponse.send(response, true);
    }
    
    
    //This method processes the contacts file
	public HtmlResponse updateContacts(List<String> headers, String rootPath, String councilName, CSVReader csvR,
			ResourceResolver adminResolver) {
    	
    	List<String> replicationList = new ArrayList<String>();
    	Map<String,List<Contact>> teamMap = new HashMap<String,List<Contact>>();
		HtmlResponse response = new HtmlResponse();
    	Node rootNode = null;
    	
    	//Acquire root node. If doesn't exist resturn error
    	Resource resource = adminResolver.getResource(rootPath);
        if(resource!=null) {
        	rootNode = resource.adaptTo(Node.class);
        } else{
			response.setStatus(200, "Root node does not exist. Process aborted");
        	return response;
        }
        
    	//Parse CSV file line by line
    	String[] nextLine = null;
    	int lineCount = 2;
    	try{
	    	while((nextLine = csvR.readNext()) != null){
	    		//Convert array to list
	    		List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
	    		clearNullValues(values, headers.size());
	    		Contact contact = new Contact();
				for (int i = 0; i < headers.size(); i++) {
					String value = values.get(i);
					String header = headers.get(i);
					if (Contact.NAME_PROP.equals(header)) {
						if(value == null || value.trim().isEmpty()){
							response.setStatus(500, "Missing required field jcr:content/jcr:title at line:" + lineCount
									+ ". Process aborted");
				            return response;
						}
						contact.setName(value);
					} else if (Contact.JOB_TITLE_PROP.equals(header)) {
						contact.setJobTitle(value);
					} else if (Contact.EMAIL_PROP.equals(header)) {
						contact.setEmail(value);
					} else if (Contact.PHONE_PROP.equals(header)) {
						contact.setPhone(value);
					} else if (Contact.TEAM_PROP.equals(header)) {
						if(value == null || value.trim().isEmpty()){
							response.setStatus(500, "Missing required field jcr:content/team at line:" + lineCount
									+ ". Process aborted");
				            return response;
						}
						contact.setTeam(value);
					} else{
						//Do nothing
					}
	    		}
				String jcrName = getJcrName(contact.getName().trim());
				String jcrTeamName = getJcrName(contact.getTeam().trim());
	    		if(jcrName == null || jcrTeamName == null || jcrName.isEmpty() || jcrTeamName.isEmpty()){
					response.setStatus(500, "Required fields blank at line:" + lineCount + ". Process aborted");
		            return response;
				}
				contact.setPath(jcrName);
	    		List<Contact> contactList = teamMap.get(contact.getTeam());
	    		if(contactList != null){
	    			for(Contact check: contactList) {
	    				String tempPath = contact.getPath();
	    				if(tempPath.equals(check.getPath())) {
	    					contact.setPath(tempPath + "_1");
	    				}
	    			}
	    			contactList.add(contact);
	    		} else{
	    			List<Contact> newList = new ArrayList<Contact>();
	    			newList.add(contact);
	    			teamMap.put(contact.getTeam(), newList);
	    		}
	    		lineCount++;
	    	}
    	} catch(IOException e){
			response.setStatus(500, "Critical Issue Parsing the input file at:" + lineCount + ". Process aborted");
			log.error("Critical Issue Parsing the input file at:" + lineCount + ". Process aborted", e);
            return response;
    	}
    	
    	//Try to create a backup contact package 
		response = backupContacts(rootPath, rootNode);
        if(response.getStatusCode() == 500){
        	return response;
        }
    	
    	//Delete all the old contacts
    	try{
            if(rootNode.getProperty("jcr:content/sling:resourceType").getString().equals("girlscouts/components/contact-placeholder-page")){
            	Page rootPage = resource.adaptTo(Page.class);
            	Iterator<Page> oldChildren = rootPage.listChildren();
            	while(oldChildren.hasNext()){
            		Page child = oldChildren.next();
            		replicator.replicate(rootNode.getSession(), ReplicationActionType.DELETE, child.getPath());
            		Resource childRes = adminResolver.getResource(child.getPath());
            		Node childNode = childRes.adaptTo(Node.class);
            		log.error("#########DELETED CONTACTS NODE CHILD NODE PATH IS: " + childNode.getPath());
            		childNode.remove();
            	}
            }
            //If deleting the contacts does not work create an error and exit method
        }catch(Exception e){
			response = new HtmlResponse();
			response.setStatus(500,
					"Failed to delete original contact data. Process Aborted due to error: " + e.getMessage());
			log.error("Failed to delete original contact data. Process Aborted due to error: ", e);
            return response;
        }
    	
    	
    	//Now that old contacts have been cleared create new contact nodes
    	try {
	    	for(String team : teamMap.keySet()){
				String teamPath = (team == null || team.equals("")) ? "none" : team;
				String teamPathName = getJcrName(teamPath);
				Node teamNode = null;
				Node teamContentNode = null;
				try{
					teamNode = rootNode.addNode(teamPathName,CQ_PAGE);
					teamContentNode = teamNode.addNode(JCR_CONTENT,CQ_PAGE_CONTENT);
					teamContentNode.setProperty("jcr:title",team);
					teamContentNode.setProperty("hideInNav", true);
				}catch(ItemExistsException e){
					log.error("GSBulkEditor error: ", e);
					try{
						teamNode = rootNode.getNode(teamPathName);
					}catch(Exception e1){
						log.error("GSBulkEditor error: ", e1);
						e1.printStackTrace();
						break;
					}
				}
				List<Contact> contacts = teamMap.get(team);
				replicationList.add(teamNode.getPath());
				for(Contact contact : contacts){
					if(contact.getPath() != null && !contact.getPath().equals("")){
						Node contactNode = teamNode.addNode(contact.getPath(),CQ_PAGE);
						Node contactContentNode = contactNode.addNode(JCR_CONTENT,CQ_PAGE_CONTENT);
						
							contactContentNode.setProperty("cq:scaffolding","/etc/scaffolding/" + councilName + "/contact");
						
						contactContentNode.setProperty("sling:resourceType","girlscouts/components/contact-page");
						contactContentNode.setProperty("jcr:title",contact.getName());
						contactContentNode.setProperty("title",contact.getJobTitle());
						contactContentNode.setProperty("phone",contact.getPhone());
						contactContentNode.setProperty("email",contact.getEmail());
						contactContentNode.setProperty("team",contact.getTeam());
						contactContentNode.setProperty("hideInNav", true);
						replicationList.add(contactNode.getPath());
					}
				}
			}
    	} catch (RepositoryException e) {
			response.setStatus(500, "Critical Error While Writing Data to Repository. Process Aborted" + e.getMessage()
					+ " With exception: " + e.getClass().getName());
			log.error("Critical Error While Writing Data to Repository. Process Aborted: ", e);
            return response;
		}
    	
    		
    	//Upon successful writing try to save the rootNode if not successful abort
        try{
			rootNode.getSession().save();
        } catch(Exception e){
			response = new HtmlResponse();
			response.setStatus(500, "Unable to save Contacts. Process Aborted");
			log.error("Unable to save Contacts. Process Aborted: ", e);
            	return response;
        }
        
        //Activate contacts 
		response = staggerActivate(replicationList, contactDelayInterval, repDelayTimeInMinutes, adminResolver);
    	
        if(response == null){
        	int mins = lineCount / documentDelayInterval;
        	mins++;
			response = new HtmlResponse();
			response.setStatus(200, "Contacts updated successfully. The data will finish replicating in approximately "
					+ mins + " minutes.");
        }
        
    	return  response;
    }
    
	public HtmlResponse updateEvents(List<String> headers, String rootPath, String year, CSVReader csvR,
			ResourceResolver adminResolver) {
    	
		HtmlResponse response = new HtmlResponse();
    	TagManager tagManager = null;
    	Node rootNode = null;
    	String councilName = null;
    	
    	HashMap<String, Tag> existingCategories = new HashMap<String, Tag>();
    	HashMap<String, Tag> existingProgramLevels = new HashMap<String, Tag>();
    	Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    	List<Event> eventList = new ArrayList<Event>();
    	Tag categoriesTag = null;
    	Tag programLevelTag = null;
		List<String> replicationList = new ArrayList<String>();
    	
    	//Check for year and root node
    	if(!year.equals("")){
	    	Resource rootRes = adminResolver.getResource(rootPath + "/" + year);
	    	if(rootRes == null){
	    		rootRes = adminResolver.getResource(rootPath);
	    		if(rootRes == null){
					response.setStatus(500, "Root path does not exist. Process aborted");
	                return response;
	    		}else{
	        		Node parentNode = rootRes.adaptTo(Node.class);
	        		Node newYearNode = null;
	        		try{
	        			newYearNode = parentNode.addNode(year,CQ_PAGE);
	        			newYearNode.addNode(JCR_CONTENT,CQ_PAGE_CONTENT);
						parentNode.getSession().save();
	        		}catch(Exception e){
						response.setStatus(500, "Failed to create year node");
						log.error("Failed to create year node:", e);
	                    return response;
	        		}
	    		}
	    	}
	    	
	    	rootNode = rootRes.adaptTo(Node.class);
        	
        	
        } else{
			response.setStatus(500, "Year parameter is mandatory for importing events. Please provide year parameter");
            return response;
        }
    	
    	rootPath = rootPath + "/" + year;
    
    	
        //Get sets of existing categories + program levels
        try {
        	tagManager = adminResolver.adaptTo(TagManager.class);
			councilName = rootNode.getAncestor(2).getName();
			String categoriesTagId = councilName + ":categories";
			String programLevelTagId = councilName + ":program-level";
			categoriesTag = tagManager.resolve(categoriesTagId);
			programLevelTag = tagManager.resolve(programLevelTagId);
			
			if(categoriesTag != null){
				Iterator<Tag> listOfChildren = categoriesTag.listChildren();
				
				while(listOfChildren.hasNext()){
					Tag tempTag = listOfChildren.next();
					existingCategories.put(tempTag.getTitle(), tempTag);
				}
				
			} else{
				categoriesTag = tagManager.createTag(categoriesTagId, "Categories", "");
				replicator.replicate(rootNode.getSession(), ReplicationActionType.ACTIVATE, categoriesTag.getPath());
				
			}
			
			if(programLevelTag != null){
				Iterator<Tag> listOfChildren = programLevelTag.listChildren();
				while(listOfChildren.hasNext()){
					Tag tempTag = listOfChildren.next();
					existingProgramLevels.put(tempTag.getTitle(), tempTag);
				}
			} else{
				programLevelTag = tagManager.createTag(programLevelTagId, "Program Level", "");
				replicator.replicate(rootNode.getSession(), ReplicationActionType.ACTIVATE, programLevelTag.getPath());
			}
			
		} catch (RepositoryException e) {
			response.setStatus(500, "Unable to access the repository. Critical error. Process aborted");
			log.error("Unable to access the repository. Critical error. Process aborted", e);
        	return response;
		} catch (AccessControlException e) {
			response.setStatus(500, "Unable to modify tags. Critical error. Process aborted");
			log.error("Unable to modify tags. Critical error. Process aborted", e);
        	return response;
		} catch (InvalidTagFormatException e) {
			response.setStatus(500, "Ivalid tag format. Critical error. Process aborted");
			log.error("Ivalid tag format. Critical error. Process aborted", e);
        	return response;
		} catch (ReplicationException e) {
			response.setStatus(500, "Unable to replicate tag, Critical Error. Process aborted.");
			log.error("Unable to replicate tag, Critical Error. Process aborted.", e);
        	return response;
		}
        
    	//Convert headers to JCR properties
		headers = replaceEventHeaders(headers);
    	
    	//Check if file is empty
        
    	
    	//Parse the file line by line
    	//Convert date + time to single date
        //Keep track of new tags to be created and activated later
    	String[] nextLine = null;
    	int lineCount = 2;
    	try{
    		while((nextLine = csvR.readNext()) != null){
    			List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
    			clearNullValues(values, headers.size());
    			Event event = new Event();
				for (int i = 0; i < headers.size(); i++) {
					String value = values.get(i);
					String header = headers.get(i);
					log.debug("Processing header: " + header + ", value: " + value);
    				if (header.equals(Event.END_DATE)){
    					event.setEndDate(value);
    				} else if (header.equals(Event.END_TIME)){
    					event.setEndTime(value);
    				} else if (header.equals(Event.START_DATE)){
    					if(value == null || value.trim().isEmpty()){
							response.setStatus(500,
									"Missing required field Start Date at line " + lineCount + ". Process aborted");
    			        	return response;
    					}
    					event.setStartDate(value);
    				} else if (header.equals(Event.START_TIME)){
    					if(value == null || value.trim().isEmpty()){
							response.setStatus(500,
									"Missing required field Start Time at line " + lineCount + ". Process aborted");
    			        	return response;
    					}
    					event.setStartTime(value);
    				} else if (header.equals(Event.REGISTRATION_OPEN_DATE)){
    					event.setRegOpenDate(value);
    				} else if (header.equals(Event.REGISTARTION_OPEN_TIME)){
    					event.setRegOpenTime(value);
    				} else if (header.equals(Event.REGISTRATION_CLOSE_DATE)){
    					event.setRegCloseDate(value);
    				} else if (header.equals(Event.REGISTRATION_CLOSE_TIME)){
    					event.setRegCloseTime(value);
    				} else if (header.equals(Event.TIMEZONE)) {
						if (value == null || value.trim().isEmpty()){
							value = DEFAULT_TIMEZONE;
						}
						event.setTimezone(value);
						event.addDataPair(header.replace("jcr:content/data/", ""), value);
					} else if (header.equals(Event.IMAGE)){
    					event.setImagePath(value);
    				} else if (header.equals(Event.PATH)){
    					event.setPath(value);
    				} else if (header.equals(Event.CATEGORIES)){
    					List<String> tagTitleList = new LinkedList<String>(Arrays.asList(value.split(";")));
    					for(String tagTitle : tagTitleList){
    						Set<String> keys = existingCategories.keySet();
    						if(!keys.contains(tagTitle)){
    							existingCategories.put(tagTitle, null);
    						}
    					}
    					event.setCategories(tagTitleList);
    				} else if (header.equals(Event.PROGRAM_LEVELS)){
    					List<String> tagTitleList = new LinkedList<String>(Arrays.asList(value.split(";")));
    					for(String tagTitle : tagTitleList){
    						Set<String> keys = existingProgramLevels.keySet();
    						if(!keys.contains(tagTitle)){
    							existingProgramLevels.put(tagTitle, null);
    						}
    					}
    					event.setProgramLevels(tagTitleList);
    				} else if (header.equals(Event.TITLE)){
    					if(value == null || value.trim().isEmpty()){
							response.setStatus(500,
									"Missing required field Title at line " + lineCount + ". Process aborted");
    			        	return response;
    					}
    					event.setTitle(value);
    				} else if (header.equals(Event.LOCATION_NAME)){
    					if(value == null || value.trim().isEmpty()){
							response.setStatus(500,
									"Missing required field Location Name at line " + lineCount + ". Process aborted");
    			        	return response;
    					}
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				} else if (header.equals(Event.TEXT)){
    					if(value == null || value.trim().isEmpty()){
							response.setStatus(500,
									"Missing required field Text at line " + lineCount + ". Process aborted");
    			        	return response;
    					}
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
					} else {
    					if(value.isEmpty()){
        					continue;
						}
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				}
				}
    			//Convert the date and time + timezone to single date string fields and add to the values map
    			String message = event.updateDataPairs();
    			if(message != null){
					response.setStatus(500,
							"Content error:  " + message + " at line " + lineCount + ". Process aborted");
    	        	return response;
    			}
    			
    			eventList.add(event);
    			lineCount++;
    		}
    	} catch(IOException e){
			response.setStatus(500, "Input/Output error at line " + lineCount + ". Process aborted");
			log.error("Input/Output error at line " + lineCount + ". Process aborted", e);
        	return response;
    	} catch(NullPointerException e){
			response.setStatus(500, "General error at line " + lineCount + ". Process aborted");
			log.error("General error at line " + lineCount + ". Process aborted", e);
        	throw e;
    	} catch(Exception e){
			response.setStatus(500, "General error at line " + lineCount + ". Process aborted");
			log.error("General error at line " + lineCount + ". Process aborted", e);
        	return response;
    	}
    	
    	  	
    	//Once the file has been parsed successfully add the events to the repository
    	//Add and Activate any new tags
    	try {
	    	Set<String> tagSet = existingCategories.keySet();
	    	for(String tagName : tagSet){
	    		Tag content = existingCategories.get(tagName);
	    		if(content == null){
	    			String tagId = councilName + ":categories/" + getJcrName(tagName);
	    			Tag temp = tagManager.createTag(tagId, tagName, "");
	    			replicator.replicate(rootNode.getSession(), ReplicationActionType.ACTIVATE, temp.getPath());
	    			existingCategories.put(tagName, temp);
	    		}
	    	}
	    	
	    	tagSet = existingProgramLevels.keySet();
	    	for(String tagName : tagSet){
	    		Tag content = existingProgramLevels.get(tagName);
	    		if(content == null){
	    			String tagId = councilName + ":program-level/" + getJcrName(tagName);
	    			Tag temp = tagManager.createTag(tagId, tagName, "");
	    			existingProgramLevels.put(tagName, temp);
	    		}
	    	}
    	} catch (AccessControlException e) {
			response.setStatus(500, "Unable to modify tags. Critical error. Process aborted");
			log.error("Unable to modify tags. Critical error. Process aborted", e);
        	return response;
		} catch (InvalidTagFormatException e) {
			response.setStatus(500, "Invalid tag format. Critical error. Process aborted");
			log.error("Invalid tag format. Critical error. Process aborted", e);
        	return response;
		} catch (ReplicationException e) {
			response.setStatus(500, "Unable to replicate newly created tag. Critical error. Process aborted");
			log.error("Unable to replicate newly created tag. Critical error. Process aborted", e);
        	return response;
		} catch (RepositoryException e) {
			response.setStatus(500, "Unable to replicate newly created tag. Critical error. Process aborted");
			log.error("Unable to replicate newly created tag. Critical error. Process aborted", e);
        	return response;
		}
    	
    	//Add any events that have a blank path
    	//Throw error if path of an event is not found
    	int lineCounter = 2;
    	try {
	    	
	    	for(Event event : eventList){
	    		Node contentNode = null;
	    		if(event.getPath() == null || event.getPath().isEmpty()){
	    			//Create new event node
	    			String id = event.getStartDate();
	    			id = id.replaceAll("[^0-9]", "-");
	    			String eventName = getJcrName(event.title);
	    			eventName = eventName + "_" + id;
	    			while(rootNode.hasNode(eventName)){
	    				eventName = eventName + "1";
	    			}
	    			
	    			Node eventNode = rootNode.addNode(eventName, CQ_PAGE);
	    			contentNode = eventNode.addNode(JCR_CONTENT, CQ_PAGE_CONTENT);
	    			contentNode.setProperty("sling:resourceType", "girlscouts/components/event-page");
	    			contentNode.setProperty(CQ_TEMPLATE, EVENT_TEMPLATE);
	    			contentNode.setProperty(CQ_SCAFFOLDING, "/etc/scaffolding/" + councilName + "/event");
	    			event.setPath(eventNode.getPath());
	    			
	    			 
	    		} else{
	    			//Check if path is correct
	    			Resource eventResource = adminResolver.getResource(event.getPath() + "/" + JCR_CONTENT);
	    			if(eventResource != null){
	    				contentNode = eventResource.adaptTo(Node.class);
	    					
	    				
	    			} else{
						response.setStatus(500, "Path at line " + lineCounter
	    	                    +" points to an event that doesn't exist. Fix path or remove altogether if creating new event. Process aborted");
	    	        	return response;
	    			}
	    			
	    		}
	    		
	    		contentNode.setProperty("jcr:title", event.getTitle());
				
				//Set the data values
	    		Node dataNode = null;
	    		if(contentNode.hasNode("data")){
	    			dataNode = contentNode.getNode("data");
	    		} else{
	    			dataNode = contentNode.addNode("data", NT_UNSTRUCTURED);
	    		}
				
				
				Map<String, String> valueMap = event.getDataPairs();
				Set<String> valueKeys = valueMap.keySet();
				for(String key : valueKeys){
					dataNode.setProperty(key, valueMap.get(key));
				}
				
				Map<String, Calendar> dateMap = event.getDatePairs();
				Set<String> dateKeys = dateMap.keySet();
				for(String key : dateKeys){
					dataNode.setProperty(key, dateMap.get(key));
				}
				Node imageNode = null;
				if(dataNode.hasNode("image")){
					imageNode = dataNode.getNode("image");
				} else{
					imageNode = dataNode.addNode("image", NT_UNSTRUCTURED);
				}
				
				
				imageNode.setProperty("fileReference", event.getImagePath());
				
				//Set Tags
				String[] categories = new String[0];
				String[] programLevels = new String[0];
				if(event.getCategories() != null){
					categories = tagListToIdArray(event.getCategories(), existingCategories);
				}
				
				if(event.getProgramLevels() != null){
					programLevels = tagListToIdArray(event.getProgramLevels(), existingProgramLevels);
				}
				
				String[] allTags = (String[])ArrayUtils.addAll(categories, programLevels);
				
				contentNode.setProperty("cq:tags", allTags);
				
				replicationList.add(event.getPath());
	    		
	    		lineCounter ++;
	    	}
	    	
	    	
			rootNode.getSession().save();
    	} catch (RepositoryException e) {
			response.setStatus(500, "Encountered repository error at line " + lineCounter
                    +". Process aborted." + e.getMessage());
			log.error("Encountered repository error at line " + lineCounter + ". Process aborted.", e);
        	return response;
		}
    	
    	
		response = delayedActivate(rootNode, replicationList, adminResolver);
    	
    	if(response == null){
			response = new HtmlResponse();
			response.setStatus(200, "Events updated successfully. The data will replicate overnight.");
        }
    	
    	
    	return  response;
    }
    
    
	public HtmlResponse updateFormMetadata(List<String> headers, String rootPath, CSVReader csvR,
			ResourceResolver adminResolver) {
    	
		HtmlResponse response = new HtmlResponse();
    	List<String> replicationList = new ArrayList<String>();
    	List<Document> documentList = new ArrayList<Document>();
    	Node rootNode = null;
    	
    	
    	//Acquire root node. If doesn't exist return error
    	Resource resource = adminResolver.getResource(rootPath);
        if(resource!=null) {
            rootNode = resource.adaptTo(Node.class);
        } else{
			response.setStatus(500, "Root node does not exist. Process aborted");
        	return response;
        }
    	
    	//Replace headers
		headers = replaceFormHeaders(headers);
    	
    	
    	//Parse file
    	Set<String> tagSet = new HashSet<String>();
    	
    	String[] nextLine = null;
    	int lineCount = 2;
    	try{
    		while((nextLine = csvR.readNext()) != null){
    			List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
    			clearNullValues(values, headers.size());
    			Document doc = new Document();
				for (int i = 0; i < headers.size(); i++) {
					String value = values.get(i);
					String header = headers.get(i);
					if (header.equals(Document.TITLE_PROP)) {
    					value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    					doc.setTitle(value);	
					} else if (header.equals(Document.PATH_PROP)) {
    					doc.setPath(value);
					} else if (header.equals(Document.TAGS_PROP)) {
    					if(!value.trim().isEmpty()){
    						value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    						String[] tagVals = value.split(",");
    						List<String> taglist = new LinkedList<String>(Arrays.asList(tagVals));
    						List<String> cleanList = new ArrayList<String>();
    						for(String tag : taglist){
    							if(tag.contains(":forms_documents")) {
    								tagSet.add(tag);
    								cleanList.add(tag);
    							}
    						}
    						doc.setTags(cleanList);
    					}
					} else if (header.equals(Document.DESCRIPTION_PROP)) {
    					value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    					doc.setDescription(value);
    				} else{
    					
    				}
				}
    			documentList.add(doc);
    			lineCount++;
    		}
    	} catch(IOException e){
			response.setStatus(500, "Input/Output error at line " + lineCount + ". Process aborted");
			log.error("Input/Output error at line " + lineCount + ". Process aborted", lineCount, e);
        	return response;
    	} catch(IllegalArgumentException e){
			response.setStatus(500, "Issue with formatting date or time at line " + lineCount + ". Process aborted");
			log.error("Issue with formatting date or time at line " + lineCount + ". Process aborted", lineCount, e);
        	return response;
    	} catch(Exception e){
			response.setStatus(500, "General error at line " + lineCount + ". Process aborted");
			log.error("General error at line " + lineCount + ". Process aborted", lineCount, e);
        	return response;
    	}
    	
    	int lineCounter = 2;
    	try {
    		//Once ready to commit changes activate any newly created tags
    		TagManager tagManager = adminResolver.adaptTo(TagManager.class);
    		String damName = rootNode.getAncestor(3).getName();
    		String councilName = getDamCouncilName(damName);
    		
    		for(String tag : tagSet){
    			if(tag != null && !tag.trim().isEmpty()){
    				String[] firstSplit = tag.split(":");
    				if(firstSplit.length == 2){
    					String cName = firstSplit[0];
    					String tagBody = firstSplit[1];
    					if(councilName.equals(cName)){
    						String[] secondSplit = tagBody.split("/");
    						if(secondSplit.length == 2){
    							String tagDomain = secondSplit[0];
    							String tagName = secondSplit[1];
    							tagName = tagName.replaceAll("[^A-Za-z0-9]", " ");
    							tagName = WordUtils.capitalize(tagName);
    							if(FORMS_TAG_DOMAIN.equals(tagDomain) 
    									|| CATEGORIES_TAG_DOMAIN.equals(tagDomain)
    									|| PROGRAM_LEVEL_TAG_DOMAIN.equals(tagDomain)){
    								if(tagManager.canCreateTag(tag)){
    									Tag result = tagManager.createTag(tag, tagName, "");
    									replicator.replicate(rootNode.getSession(), ReplicationActionType.ACTIVATE, result.getPath());
    								}
    							} else{
									response.setStatus(500, "Tag domain for " + tag
											+ " does not match the forms_documents domain. Process aborted");
    	        		        	return response;
    							}
    						} else{
    							if(tagManager.canCreateTag(tag)){
									response.setStatus(500, "Improperly formatted tag " + tag + ". Process aborted");
	    	    		        	return response;
    							}
    						}
    					} else{
							response.setStatus(500, "Tag " + tag + " does not match the council tag namespace: "
									+ councilName + " Process aborted");
        		        	return response;
    					}
    				} else{
						response.setStatus(500, "Improperly formatted tag " + tag + ". Process aborted");
    		        	return response;
    				}
    				
    			}
    		}
    		
    		
	    	//Once file parsed successfully commit changes to memory
	    	
	    	for(Document doc : documentList){
	    		//Check that document exists
	    		Resource docResource = adminResolver.getResource(doc.getPath());
	    		if(docResource != null){
	    			Node docNode = docResource.adaptTo(Node.class);
	    			Node contentNode = null;
	    			Node metaDataNode = null;
	    			
					if(docNode.hasNode(JCR_CONTENT)){
						contentNode = docNode.getNode(JCR_CONTENT);
					} else{
						contentNode = docNode.addNode(JCR_CONTENT);
					}
					
					if(contentNode.hasNode("metadata")){
						metaDataNode = contentNode.getNode("metadata");
					} else{
						metaDataNode = contentNode.addNode("metadata");
					}
					
					if(metaDataNode.hasProperty(Document.TITLE_PROP)){
						metaDataNode.getProperty(Document.TITLE_PROP).remove();
					}
					
					if(metaDataNode.hasProperty(Document.DESCRIPTION_PROP)){
						metaDataNode.getProperty(Document.DESCRIPTION_PROP).remove();
					}
					
					metaDataNode.setProperty(Document.TITLE_PROP, doc.getTitle());
					metaDataNode.setProperty(Document.DESCRIPTION_PROP, doc.getDescription());
					if(doc.getTags() != null){
						metaDataNode.setProperty(Document.TAGS_PROP, (String[])doc.getTags().toArray(new String[0]));
					}
					replicationList.add(doc.getPath() + "/jcr:content/metadata");
	    			
	    		} else{
					response.setStatus(500, "Document at line " + lineCounter
							+ " not found. Please check that the path is correct. Process aborted");
	            	return response;
	    		}
	    		
	    		lineCounter++;
	    		
	    	}
	    	
			rootNode.getSession().save();
    	} catch(InvalidTagFormatException e){
			response.setStatus(500,
					"Improperly formatted tag encountered. Process aborted with message: " + e.getMessage());
			log.error("Improperly formatted tag encountered. Process aborted with message: ", e);
        	return response;
    	} catch (RepositoryException e) {
			response.setStatus(500, "Document at line " + lineCounter
					+ " encountered issue writing to repository. Process aborted with message: " + e.getMessage());
			log.error(
					"Document at line " + lineCount
							+ " encountered issue writing to repository. Process aborted with message: ",
					lineCounter, e);
        	return response;
		} catch (ReplicationException e) {
			response.setStatus(500, "Encountered issues replicating newly created tags. Process aborted with message: "
					+ e.getMessage());
			log.error("Encountered issues replicating newly created tags. Process aborted with message: ", e);
        	return response;
		}
    	
    	//Activate contacts 
		response = staggerActivate(replicationList, documentDelayInterval, repDelayTimeInMinutes, adminResolver);
    	
        if(response == null){
        	int mins = lineCounter / documentDelayInterval;
        	mins++;
			response = new HtmlResponse();
			response.setStatus(200, "Documents updated successfully. The data will finish replicating in approximately "
					+ mins + " minutes.");
        }
    	
    	return response;
    }
    
    private String getDamCouncilName(String damName){
    	String result = damName;
    	switch(damName){
    		case "southern-appalachian":
    	    	result = "girlscoutcsa";
        		break;
    		case "NE_Texas":
    			result = "gsnetx";
        		break;
    		case "nc-coastal-pines-images-":
    			result = "girlscoutsnccp";
        		break;
    		case "wcf-images":
    			result = "gswcf";
    			break;
    		case "oregon-sw-washington-" :
    			result = "girlscoutosw";
    			break;
    		
    	}
    	
    	
    	if(damName.contains("girlscouts-")){
    		result = damName.replaceAll("girlscouts-", "");
    	} 
    	
    	
    	return result;
    	
    	
    }
    
    
    
	private HtmlResponse delayedActivate(Node rootNode, List<String> replicationList, ResourceResolver adminResolver) {
    	
		HtmlResponse response = new HtmlResponse();
    	
    	String delayNodeName = PageReplicationUtil.getDateRes();
    	Resource resource = adminResolver.getResource(DELAYED_ACTIVATION_PATH);
    	if(resource != null){
    		
    		try{
	    		Node delayedParent = resource.adaptTo(Node.class);
	    		Node delayedNode = delayedParent.addNode(delayNodeName, SLING_FOLDER);
	    		String[] paths = replicationList.toArray(new String[0]);
	    		delayedNode.setProperty("activate", true);
	    		delayedNode.setProperty("crawl", true);
	    		delayedNode.setProperty("delayActivation", true);
	    		delayedNode.setProperty("status", "delayed");
	    		delayedNode.setProperty("pages", paths);
				delayedParent.getSession().save();
    		} catch(RepositoryException e){
				response.setStatus(200, "Critical Activation Error. Error while setting up delayed activation");
				log.error("Critical Activation Error. Error while setting up delayed activation: ", e);
            	return response;
    		}
    		
    	} else{
			response.setStatus(200, "Critical Activation Error. Delayed activation node is not available");
        	return response;
    	}
    	
    	
    	return null;
    }
    
	private HtmlResponse staggerActivate(List<String> replicationList, int interval, int delayTimeInMinutes,
			ResourceResolver adminResolver) {
    	 
		HtmlResponse response = new HtmlResponse();
    	try {
    		Node parentNode = null;
	    	Node etcNode = adminResolver.getResource("/etc").adaptTo(Node.class);
	    	String stagNodeName = PageReplicationUtil.getDateRes();
	    	if(etcNode.hasNode(STAG_ACTIVATION)){
	    		parentNode = etcNode.getNode(STAG_ACTIVATION);
	    	} else{
	    		parentNode = etcNode.addNode(STAG_ACTIVATION);
	    	}
	    	
	    	String[] activations = replicationList.toArray(new String[0]);
	    	
	    	Node stagNode = parentNode.addNode(stagNodeName);
	    	
	    	stagNode.setProperty("delay", delayTimeInMinutes);
	    	stagNode.setProperty("interval", interval);
	    	stagNode.setProperty("state", "intiated");
	    	stagNode.setProperty("activations", activations);
			etcNode.getSession().save();
	    	
	    	
			// Kick off the workflow to replicate the created nodes
			WorkflowSession wfSession = workflowService.getWorkflowSession(adminResolver.adaptTo(Session.class));
	
	    	WorkflowModel model;
		
			model = wfSession.getModel("/etc/workflow/models/staggered-activate/jcr:content/model");
			WorkflowData data = wfSession.newWorkflowData("JCR_PATH", stagNode.getPath());
			

			wfSession.startWorkflow(model, data);
		} catch (WorkflowException e) {
			response.setStatus(200,
					"Critical Activation Error. Failed to initiate workflow. Due to error: " + e.getMessage());
			log.error("Critical Activation Error. Failed to initiate workflow. Due to error: ", e);

        	return response;
		} catch (RepositoryException e) {
			response.setStatus(200,
					"Critical Activation Error. Failed while accessing repository. Due to error: " + e.getMessage());
			log.error("Critical Activation Error. Failed while accessing repository. Due to error: ", e);
        	return response;
		}
       
        
        return response;
    }

    private List<String> replaceEventHeaders(List<String> headers){
    	if(!headers.isEmpty()){
    		String allHeaders = headers.remove(0);
    		for(String header : headers){
    			allHeaders = allHeaders + "|" + header;
    		}
    		
    		allHeaders = allHeaders.replaceAll("Title","jcr:content/jcr:title")
			.replaceAll("Start Date","jcr:content/data/start-date")
			.replaceAll("Start Time","jcr:content/data/start-time")
			.replaceAll("End Date","jcr:content/data/end-date")
			.replaceAll("End Time","jcr:content/data/end-time")
			.replaceAll("Time Zone","jcr:content/data/timezone")
			.replaceAll("Registration Open Date","jcr:content/data/regOpen-date")
			.replaceAll("Registration Open Time","jcr:content/data/regOpen-time")
			.replaceAll("Registration Close Date","jcr:content/data/regClose-date")
			.replaceAll("Registration Close Time","jcr:content/data/regClose-time")
			.replaceAll("Region","jcr:content/data/region")
			.replaceAll("Location Name","jcr:content/data/locationLabel")
			.replaceAll("Address","jcr:content/data/address")
			.replaceAll("Text","jcr:content/data/details")
			.replaceAll("Search Description","jcr:content/data/srchdisp")
			.replaceAll("Color","jcr:content/data/color")
			.replaceAll("Registration","jcr:content/data/register")
			.replaceAll("Categories","jcr:content/cq:tags-categories")
			.replaceAll("Program Levels","jcr:content/cq:tags-progLevel")
			.replaceAll("Path","jcr:path")
			.replaceAll("Image","jcr:content/data/imagePath")
			.replaceAll("Program Type","jcr:content/data/progType")
			.replaceAll("Grades","jcr:content/data/grades")
			.replaceAll("Girl Fee","jcr:content/data/girlFee")
			.replaceAll("Adult Fee","jcr:content/data/adultFee")
			.replaceAll("Minimum Attendance","jcr:content/data/minAttend")
			.replaceAll("Maximum Attendance","jcr:content/data/maxAttend")
			.replaceAll("Program Code","jcr:content/data/programCode");
			log.debug("allHeaders: " + allHeaders);
    		return new LinkedList<String>(Arrays.asList(allHeaders.split("\\|")));	
    		
    	}
    	return headers;
    		
    	
    }
    
    private List<String> replaceFormHeaders(List<String> headers){
    	if(!headers.isEmpty()){
    		String allHeaders = headers.remove(0);
    		for(String header : headers){
    			allHeaders = allHeaders + "|" + header;
    		}
    		
    		allHeaders = allHeaders.replaceAll("Title","dc:title")
    				.replaceAll("Description","dc:description")
    				.replaceAll("Categories","cq:tags")
    				.replaceAll("Path","jcr:path");
    		
    		return new LinkedList<String>(Arrays.asList(allHeaders.split("\\|")));	
    		
    	}
    	
    	return headers;
    		
    	
    }
    private String getJcrName(String title){
    	if(title != null){
    		String name = title.toLowerCase().replaceAll("[^A-Za-z0-9]","-");
    		return name;
    	} 
    	
    	return null;
    }
    private String[] tagListToIdArray(List<String> tagNames, Map<String,Tag> map){
    	String[] result = new String[tagNames.size()];
    	int i = 0;
    	for(String tagName : tagNames){
    		Tag tag = map.get(tagName);
    		String tagId = tag.getTagID();
    		result[i] = tagId;
    		i++;
    	}
    	
    	return result;
    }
    
    private void clearNullValues(List<String> list, int size){
    	int i = 0;
    	for(i = 0; i < list.size(); i++){
    		if(list.get(i) == null){
    			list.set(i, "");
    		}else{
    			String val = list.get(i);
    			list.set(i, val.trim());
    		}
    	}
    	
    	for(int j = i; j <size; j++){
    		list.add("");
    	}
    }
    
	private HtmlResponse backupContacts(String rootPath, Node rootNode) {
    	
		HtmlResponse response = new HtmlResponse();
    	try{
			//Start by creating a package under the root node, in case we need to roll back
    		JcrPackageManager jcrPM = packaging.getPackageManager(rootNode.getSession());
    		String packageName = rootPath.replaceAll("/","-");
    		GSDateTime gdt = new GSDateTime();
    		GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyyMMddHHmm");
    		String dateString = dtfOut.print(gdt);  
    		if(jcrPM == null){
    			System.err.println("Null PackageManager");
    		}
    		JcrPackage jcrP = jcrPM.create("GirlScouts","spreadsheet-prebuild-" + packageName.toLowerCase(), dateString);
    		JcrPackageDefinition jcrPD = jcrP.getDefinition();
    		DefaultWorkspaceFilter filter = new DefaultWorkspaceFilter();
    		PathFilterSet pfs = new PathFilterSet(rootPath);
    		filter.add(pfs);
    		jcrPD.setFilter(filter, false);
    		PrintWriter pkgout = new PrintWriter(System.out);
    		jcrPM.assemble(jcrP, new DefaultProgressListener(pkgout));
		} catch(ItemExistsException ie) {
			response.setStatus(500,
					"Failed to create contact backup package due to recent run of contacts bulkeditor. Please try again in two minutes");
			log.error(
					"Failed to create contact backup package due to recent run of contacts bulkeditor. Please try again in two minutes: ",
					ie);
			return response;
		}
    		catch(Exception e){
			response.setStatus(500, "Failed to create contact backup package due to " + e.getClass().getName()
					+ " with message " + e.getMessage());
			log.error("Failed to create contact backup package due to ", e);
            return response;
		}
    	return response;
    }
    
    private class Contact{
		public static final String NAME_PROP="jcr:content/jcr:title";
		public static final String JOB_TITLE_PROP="jcr:content/title";
		public static final String PHONE_PROP="jcr:content/phone";
		public static final String EMAIL_PROP="jcr:content/email";
		public static final String TEAM_PROP="jcr:content/team";
		
		private String name;
		private String jobTitle;
		private String phone;
		private String email;
		private String team;
		private String path;
		
		public Contact(){
			name = null;
			jobTitle = null;
			phone = null;
			email = null;
			team = null;
			path = null;
		}
		
		public String getName(){
			return name;
		}
		
		public String getJobTitle(){
			return jobTitle;
		}
		
		public String getPhone(){
			return phone;
		}
		
		public String getEmail(){
			return email;
		}
		
		public String getTeam(){
			return team;
		}
		
		public String getPath(){
			return path;
		}
		
		public void setName(String name){
			this.name = name;
		}
		
		public void setJobTitle(String jobTitle){
			this.jobTitle = jobTitle;
		}
		
		public void setPhone(String phone){
			this.phone = phone;
		}
		
		public void setEmail(String email){
			this.email = email;
		}
		
		public void setTeam(String team){
			this.team = team;
		}
		
		public void setPath(String path){
			this.path = path;
		}
	}

	private class Event{
    	public static final String TITLE = "jcr:content/jcr:title";
    	public static final String START_DATE = "jcr:content/data/start-date";
    	public static final String START_TIME = "jcr:content/data/start-time";
    	public static final String END_DATE = "jcr:content/data/end-date";
    	public static final String END_TIME = "jcr:content/data/end-time";
    	public static final String REGISTRATION_OPEN_DATE = "jcr:content/data/regOpen-date";
    	public static final String REGISTARTION_OPEN_TIME = "jcr:content/data/regOpen-time";
    	public static final String REGISTRATION_CLOSE_DATE = "jcr:content/data/regClose-date";
    	public static final String REGISTRATION_CLOSE_TIME = "jcr:content/data/regClose-time";
    	public static final String TIMEZONE = "jcr:content/data/timezone";
    	public static final String LOCATION_NAME = "jcr:content/data/locationLabel";
    	public static final String TEXT = "jcr:content/data/details";
    	public static final String CATEGORIES = "jcr:content/cq:tags-categories";
    	public static final String PROGRAM_LEVELS = "jcr:content/cq:tags-progLevel";
    	public static final String PATH = "jcr:path";
    	public static final String IMAGE = "jcr:content/data/imagePath";
    	private String path;
    	
    	//Needs to be placed at the jcr:content level
    	private String title;
    	
    	//Dates
    	private String startDate;
    	private String startTime;
    	private String endDate;
    	private String endTime;
    	private String regOpenDate;
    	private String regOpenTime;
    	private String regCloseDate;
    	private String regCloseTime;
    	
    	//Timezone
    	private String timezone;
    	
    	
    	//Tags
    	private List<String> programLevels;
    	private List<String> categories;
    	
    	//Image has special placement
    	private String imagePath;
    	
    	private Map<String, String> dataPairs;
    	private Map<String, Calendar> datePairs;
    	
    	public Event(){
    		this.dataPairs = new HashMap<String,String>();
    		this.datePairs = new HashMap<String,Calendar>();
    		this.startDate = "";
    		this.startTime = "";
    		this.regOpenDate = "";
    		this.regOpenTime = "";
    		this.regCloseDate = "";
    		this.regCloseTime = "";
    		this.endDate = "";
    		this.endTime = "";
    		
    	}
    	
    	public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		
		public void setStartTime(String startTime){
			this.startTime = startTime;
		}

		public String getEndDate() {
			return endDate;
		}
		
		public void setEndTime(String endTime){
			this.endTime = endTime;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getRegOpenDate() {
			return regOpenDate;
		}

		public void setRegOpenDate(String regOpenDate) {
			this.regOpenDate = regOpenDate;
		}
		
		public void setRegOpenTime(String regOpenTime){
			this.regOpenTime = regOpenTime;
		}

		public String getRegCloseDate() {
			return regCloseDate;
		}

		public void setRegCloseDate(String regCloseDate) {
			this.regCloseDate = regCloseDate;
		}
		
		public void setRegCloseTime(String regCloseTime){
			this.regCloseTime = regCloseTime;
		}

		public void setTimezone(String timezone){
    		this.timezone = timezone;
		}

		public String getTimezone(){
    		return this.timezone;
		}

		public List<String> getProgramLevels() {
			return programLevels;
		}

		public void setProgramLevels(List<String> programLevels) {
			this.programLevels = programLevels;
		}

		public List<String> getCategories() {
			return categories;
		}

		public void setCategories(List<String> categories) {
			this.categories = categories;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public Map<String, String> getDataPairs() {
			return dataPairs;
		}

		public void addDataPair(String key, String value){
    		this.dataPairs.put(key, value);
    	}
		
		public Map<String,Calendar> getDatePairs(){
			return this.datePairs;
		}
		
		public String updateDataPairs(){
			
			Calendar start = null;
			try{
				start = convertDateAndTime(this.startDate, this.startTime);
			}  catch(IllegalArgumentException e){
				log.error("Issue with formatting of start date:" + this.startDate + " or time:" + this.startTime, e);
				return "Issue with formatting of start date:" + this.startDate + " or time:" + this.startTime;
	    	}
			if(start != null){
				this.datePairs.put("start", start);
			}
		
			if(!this.endDate.isEmpty() && this.endTime.isEmpty()){
				return "End Date does not have a correponding End Time";
			}
			if(this.endDate.isEmpty() && !this.endTime.isEmpty()){
				return "End Time does not have a correponding End Date";
			}
			
			Calendar end = null;
			try{
				end = convertDateAndTime(this.endDate, this.endTime);
			}  catch(IllegalArgumentException e){
				log.error("Issue with formatting of end date:" + this.endDate + " or time:" + this.endTime, e);
				return "Issue with formatting of end date:" + this.endDate + " or time:" + this.endTime;
	    	}
			if(end != null){
				this.datePairs.put("end", end);
			}
		
			if(!this.regOpenDate.isEmpty() && this.regOpenTime.isEmpty()){
				return "Registration Open Date does not have a correponding Registration Open Time";
			}
			if(this.regOpenDate.isEmpty() && !this.regOpenTime.isEmpty()){
				return "Registration Open Time does not have a correponding Registration Open Date";
			}
			Calendar regOpen = null;
			try{
				regOpen = convertDateAndTime(this.regOpenDate, this.regOpenTime);
			}  catch(IllegalArgumentException e){
				log.error("Issue with formatting of registration open date:" + this.regOpenDate + " or time:"
						+ this.regOpenTime, e);
				return "Issue with formatting of registration open date:" + this.regOpenDate + " or time:"
						+ this.regOpenTime;
	    	}
			
			if(regOpen != null){
				this.datePairs.put("regOpen", regOpen);
			}
			
			
			if(!this.regCloseDate.isEmpty() && this.regCloseTime.isEmpty()){
				return "Registration Close Date does not have a correponding Registration Close Time";
			}
			if(this.regCloseDate.isEmpty() && !this.regCloseTime.isEmpty()){
				return "Registration Close Time does not have a correponding Registration Close Date";
			}
			Calendar regClose = null;
			try{
				regClose = convertDateAndTime(this.regCloseDate, this.regCloseTime);
			}  catch(IllegalArgumentException e){
				log.error("Issue with formatting of registration close date:" + this.regCloseDate + " or time:"
						+ this.regCloseTime, e);
				return "Issue with formatting of registration close date:" + this.regCloseDate + " or time:"
						+ this.regCloseTime;
	    	}
			
			if(regClose != null){
				this.datePairs.put("regClose", regClose);
			}
			
			return null;
		}
		
		private Calendar convertDateAndTime(String date, String time){
			if(date == null || time == null || date.isEmpty() || time.isEmpty()){
				return null;
			}
			
			String formatDate = date;
			String[] dateSplit = date.split("/");
			LocalDate localDate = null;
			if(dateSplit.length == 3){
				String endYear = dateSplit[2];
				if(endYear.length() < 4){
					endYear = "20" + endYear;
				}
				formatDate = dateSplit[0] + "/" + dateSplit[1] + "/" + endYear;
				localDate = LocalDate.of(Integer.parseInt(endYear), Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]));
			}

			String timePattern = "h:m a";
			LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern(timePattern));

			ZonedDateTime zonedDateTime = null;
			if (!(localDate == null || localTime == null)){
				zonedDateTime = ZonedDateTime.of(localDate, localTime, ZoneId.of(this.timezone));
			}

			String zonedDateTimeString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			log.debug("ZonedDateTime: " + zonedDateTimeString);
			GSDateTime dt = GSDateTime.parse(zonedDateTimeString);

        	
        	return dt.getCalendar();
        	//return result;
        	
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
    	
    	
    }
    
    private class Document{

		public static final String TITLE_PROP="dc:title";
    	public static final String DESCRIPTION_PROP="dc:description";
    	public static final String TAGS_PROP="cq:tags";
    	public static final String PATH_PROP="jcr:path";
    	
    	private String title;
    	private String fileName;
    	private String description;
    	private String path;
    	
    	public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		private List<String> tags;
    	
    	public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}
    }
}