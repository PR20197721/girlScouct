package apps.wcm.core.components.gsbulkeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.AccessControlException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceResolverFactory;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.events.search.GSDateTime;
import org.girlscouts.web.events.search.GSDateTimeFormat;
import org.girlscouts.web.events.search.GSDateTimeFormatter;
import org.girlscouts.web.events.search.GSDateTimeZone;

import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.day.jcr.vault.fs.api.PathFilterSet;
import com.day.jcr.vault.fs.config.DefaultWorkspaceFilter;
import com.day.jcr.vault.packaging.JcrPackage;
import com.day.jcr.vault.packaging.JcrPackageDefinition;
import com.day.jcr.vault.packaging.JcrPackageManager;
import com.day.jcr.vault.packaging.Packaging;
import com.day.jcr.vault.util.DefaultProgressListener;
import com.opencsv.CSVReader;

/**
 * Servers as base for image servlets
 */
public class POST extends SlingAllMethodsServlet {
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
    
    
    int contactDelayInterval = 40;
    int documentDelayInterval = 10;
    int repDelayTimeInMinutes = 1;

    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response)
            throws ServletException, IOException {

        HtmlResponse htmlResponse = null;
        

        if (request.getRequestParameter(DOCUMENT_PARAM) != null) {
            InputStream in = request.getRequestParameter(DOCUMENT_PARAM).getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in, "cp1252"));

            String insertedResourceType = request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM)!=null ?
                    request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM).getString() : null;
            String rootPath = request.getRequestParameter(ROOTPATH_PARAM)!=null ?
                    request.getRequestParameter(ROOTPATH_PARAM).getString() : null;
                    
            

            String importType = request.getRequestParameter(IMPORT_TYPE_PARAM)!=null ? request.getRequestParameter(IMPORT_TYPE_PARAM).getString() : "";
            String year = request.getRequestParameter(YEAR_PARAM)!=null ? request.getRequestParameter(YEAR_PARAM).getString() : "";
            
            SlingBindings bindings = null;
			SlingScriptHelper scriptHelper = null;
			try{
				bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
				scriptHelper = bindings.getSling();
			}catch(Exception e){
                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                        "Could not resolve sling helper");
                htmlResponse.send(response, true);
                return;
			}
			
			if(scriptHelper == null){
				 htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Could not resolve sling helper");
	             htmlResponse.send(response, true);
	             return;
			}
            
            
        	SlingRepository repo = scriptHelper.getService(SlingRepository.class);
            Session admin = null;

            try {
                admin = repo.loginAdministrative(null);
                ResourceResolver adminResolver = scriptHelper.getService(JcrResourceResolverFactory.class).getResourceResolver(admin);
                
	            if(rootPath!=null) {
	                Node rootNode = null;
	
	                Resource resource = adminResolver.getResource(rootPath);
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
	                        	htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                    "Could not determine council root");
	                        if(councilRoot.indexOf("/") != -1 && !councilRoot.endsWith("/")){
	                        	councilName = councilRoot.substring(councilRoot.lastIndexOf("/") + 1,councilRoot.length());
	                        }
	                	}catch(Exception e){
	                		htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                "Can not Determine Root Node Depth and/or Council Name");
	                        htmlResponse.send(response, true);
	                        e.printStackTrace();
	                        return;
	                	}
	                	if(rootDepth >= 2){
	            			
	            			
	                		
		                    
		                    
		                    CSVReader csvR = new CSVReader(bufferReader);
		                    String[] headerArr = csvR.readNext();
		                    if (headerArr != null) {
		                        
		                    	List<String> headers = new LinkedList<String>(Arrays.asList(headerArr));
		                    	try{
			                    	//GS - We can't make lots of asset packages
			            			if(importType.equals("contacts")){
				                		
				                		htmlResponse = updateContacts(headers, rootPath, councilName, csvR, scriptHelper, adminResolver);	
			            			} else if(importType.equals("events")){
			            				htmlResponse = updateEvents(headers, rootPath, year, csvR, scriptHelper, adminResolver);
			            			} else if(importType.equals("documents")){
			            				htmlResponse = updateFormMetadata(headers, rootPath, csvR, scriptHelper, adminResolver);
			            			} else{
			            				htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
				                                "Unsupported import type.");
			            			}
		            			
		                    	} catch(Exception e){
		                			htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
			                                "General Exception occured while processing content: " + e.getMessage());
		                		}
		                            
		                       
		                    } else {
		                        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
		                                "Empty document");
		                    }
	                	} else {
	                		htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                "Please increase the depth of the root path to at least two");
	                	}
	                } else {
	                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Invalid root path");
	                }
	            } else {
	                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                    "No root path provided");
	            }
            
            } catch (RepositoryException e) {
            	 htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                         "Unable to get admin access for modifying content due to error: " + e.getMessage());
            }
            
        } else {
            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                    "No document provided");
        }
        
        

        htmlResponse.send(response, true);
    }
    
    
    //This method processes the contacts file
    public HtmlResponse updateContacts(List<String> headers, String rootPath, String councilName, CSVReader csvR, SlingScriptHelper scriptHelper, ResourceResolver adminResolver){
    	
    	List<String> replicationList = new ArrayList<String>();
    	Map<String,List<Contact>> teamMap = new HashMap<String,List<Contact>>();
    	HtmlResponse response = null;
    	Node rootNode = null;
    	
    	//Acquire root node. If doesn't exist resturn error
    	Resource resource = adminResolver.getResource(rootPath);
        if(resource!=null) {
            rootNode = resource.adaptTo(Node.class);
        } else{
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Root node does not exist. Process aborted");
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
	    		int indexCount = 0;
	    		for(String value : values){
				
					if(Contact.NAME_PROP.equals(headers.get(indexCount))){ 
						if(value == null || value.trim().isEmpty()){
							response = HtmlStatusResponseHelper.createStatusResponse(true,
				                    "Missing required field jcr:content/jcr:title at line:" + lineCount +". Process aborted");
				            return response;
						}
						contact.setName(value);
					} else if(Contact.JOB_TITLE_PROP.equals(headers.get(indexCount))){
						contact.setJobTitle(value);
					} else if(Contact.EMAIL_PROP.equals(headers.get(indexCount))){
						contact.setEmail(value);
					} else if(Contact.PHONE_PROP.equals(headers.get(indexCount))){
						contact.setPhone(value);
					} else if(Contact.TEAM_PROP.equals(headers.get(indexCount))){
						if(value == null || value.trim().isEmpty()){
							response = HtmlStatusResponseHelper.createStatusResponse(true,
				                    "Missing required field jcr:content/team at line:" + lineCount +". Process aborted");
				            return response;
						}
						contact.setTeam(value);
					} else{
						//Do nothing
					}
				
	    			indexCount++;
	    		}
	    		String jcrName = this.getJcrName(contact.getName().trim());
	    		String jcrTeamName = this.getJcrName(contact.getTeam().trim());
	    		if(jcrName == null || jcrTeamName == null || jcrName.isEmpty() || jcrTeamName.isEmpty()){
	    			response = HtmlStatusResponseHelper.createStatusResponse(true,
		                    "Required fields blank at line:" + lineCount +". Process aborted");
		            return response;
	    		}
	    		
	    		contact.setPath(jcrName);
	    		
	    		List<Contact> contactList = teamMap.get(contact.getTeam());
	    		if(contactList != null){
	    			contactList.add(contact);
	    		} else{
	    			List<Contact> newList = new ArrayList<Contact>();
	    			newList.add(contact);
	    			teamMap.put(contact.getTeam(), newList);
	    		}
	    		
	    		
	    		lineCount++;
	    	}
    	} catch(IOException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Critical Issue Parsing the input file at:" + lineCount +". Process aborted");
            return response;
    	}
    	
    	//Try to create a backup contact package 
    	response = backupContacts(scriptHelper, rootPath, rootNode);
        if(response != null){
        	return response;
        }
    	
    	//Delete all the old contacts
    	try{
    		
            if(rootNode.getProperty("jcr:content/sling:resourceType").getString().equals("girlscouts/components/contact-placeholder-page")){
            	Page rootPage = resource.adaptTo(Page.class);
            	Iterator<Page> oldChildren = rootPage.listChildren();
            	Replicator replicator = scriptHelper.getService(Replicator.class);
            	while(oldChildren.hasNext()){
            		Page child = oldChildren.next();
            		replicator.replicate(rootNode.getSession(), ReplicationActionType.DELETE, child.getPath());
            		Resource childRes = adminResolver.getResource(child.getPath());
            		Node childNode = childRes.adaptTo(Node.class);
            		childNode.remove();
            	}
            }
            //If deleting the contacts does not work create an error and exit method
        }catch(Exception e){
            response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Failed to delete original contact data. Process Aborted");
            return response;
        }
    	
    	
    	//Now that old contacts have been cleared create new contact nodes
    	try {
	    	for(String team : teamMap.keySet()){
				String teamPath = (team == null || team.equals("")) ? "none" : team;
				String teamPathName = this.getJcrName(teamPath);
				Node teamNode = null;
				Node teamContentNode = null;
				try{
					teamNode = rootNode.addNode(teamPathName,CQ_PAGE);
					teamContentNode = teamNode.addNode(JCR_CONTENT,CQ_PAGE_CONTENT);
					teamContentNode.setProperty("jcr:title",team);
					teamContentNode.setProperty("hideInNav", true);
				}catch(ItemExistsException e){
					try{
						teamNode = rootNode.getNode(teamPathName);
					}catch(Exception e1){
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
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Critical Error While Writing Data to Repository. Process Aborted" + e.getMessage());
            return response;
		}
    	
    		
    	//Upon successful writing try to save the rootNode if not successful abort
        try{
        	rootNode.save();
        } catch(Exception e){
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                "Unable to save Contacts. Process Aborted");
            	return response;
        }
        
        //Activate contacts 
        response = staggerActivate(scriptHelper, replicationList, contactDelayInterval, repDelayTimeInMinutes, adminResolver);
    	
        if(response == null){
        	int mins = lineCount / documentDelayInterval;
        	mins++;
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Contacts updated successfully. The data will finish replicating in approximately " + mins + " minutes.");
        }
        
    	return  response;
    }
    
    public HtmlResponse updateEvents(List<String> headers, String rootPath, String year, CSVReader csvR, SlingScriptHelper scriptHelper, ResourceResolver adminResolver) {
    	
    	HtmlResponse response = null;
    	TagManager tagManager = null;
    	Node rootNode = null;
    	String councilName = null;
    	
    	HashMap<String, Tag> existingCategories = new HashMap<String, Tag>();
    	HashMap<String, Tag> existingProgramLevels = new HashMap<String, Tag>();
    	Pattern colorMatch = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    	List<Event> eventList = new ArrayList<Event>();
    	Tag categoriesTag = null;
    	Tag programLevelTag = null;
    	List<String> replicationList = new ArrayList();
    	
    	//Check for year and root node
    	if(!year.equals("")){
	    	Resource rootRes = adminResolver.getResource(rootPath + "/" + year);
	    	if(rootRes == null){
	    		rootRes = adminResolver.getResource(rootPath);
	    		if(rootRes == null){
	    			response = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Root path does not exist. Process aborted");
	                return response;
	    		}else{
	        		Node parentNode = rootRes.adaptTo(Node.class);
	        		Node newYearNode = null;
	        		Node newYearContentNode = null;
	        		try{
	        			newYearNode = parentNode.addNode(year,CQ_PAGE);
	        			newYearContentNode = newYearNode.addNode(JCR_CONTENT,CQ_PAGE_CONTENT);
	        			parentNode.save();
	        		}catch(Exception e){
	        			response = HtmlStatusResponseHelper.createStatusResponse(true,
	                            "Failed to create year node");
	                    return response;
	        		}
	    		}
	    	}
	    	
	    	rootNode = rootRes.adaptTo(Node.class);
        	
        	
        } else{
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Year parameter is mandatory for importing events. Please provide year parameter");
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
			}
			
			if(programLevelTag != null){
				Iterator<Tag> listOfChildren = programLevelTag.listChildren();
				while(listOfChildren.hasNext()){
					Tag tempTag = listOfChildren.next();
					existingProgramLevels.put(tempTag.getTitle(), tempTag);
				}
			} else{
				programLevelTag = tagManager.createTag(programLevelTagId, "Program Level", "");
			}
			
		} catch (RepositoryException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Unable to access the repository. Critical error. Process aborted");
        	return response;
		} catch (AccessControlException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Unable to modify tags. Critical error. Process aborted");
        	return response;
		} catch (InvalidTagFormatException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Ivalid tag format. Critical error. Process aborted");
        	return response;
		}
        
    	//Convert headers to JCR properties
        headers = this.replaceEventHeaders(headers);
    	
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
    			int index = 0;
    			for(String value : values){
    				String header = headers.get(index);
    				
    				if (header.equals(Event.END_DATE)){
    					event.setEndDate(value);
    				} else if (header.equals(Event.END_TIME)){
    					event.setEndTime(value);
    				} else if (header.equals(Event.START_DATE)){
    					if(value == null || value.trim().isEmpty()){
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Missing required field Start Date at line "+lineCount +". Process aborted");
    			        	return response;
    					}
    					event.setStartDate(value);
    				} else if (header.equals(Event.START_TIME)){
    					if(value == null || value.trim().isEmpty()){
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Missing required field Start Time at line "+lineCount +". Process aborted");
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
    				} else if (header.equals(Event.COLOR)){
    					if(colorMatch.matcher(value).matches()){
    						String truncatedHeader = header.replace("jcr:content/data/", "");
    						event.addDataPair(truncatedHeader, value);
    					}else{
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Color field is in the wrong format at line "+lineCount +". Process aborted");
    			        	return response;
    					}
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
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Missing required field Title at line "+lineCount +". Process aborted");
    			        	return response;
    					}
    					event.setTitle(value);
    				} else if (header.equals(Event.LOCATION_NAME)){
    					if(value == null || value.trim().isEmpty()){
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Missing required field Location Name at line "+lineCount +". Process aborted");
    			        	return response;
    					}
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				} else if (header.equals(Event.TEXT)){
    					if(value == null || value.trim().isEmpty()){
    						response = HtmlStatusResponseHelper.createStatusResponse(true,
    			                    "Missing required field Text at line "+lineCount +". Process aborted");
    			        	return response;
    					}
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				} else{
    					
    					if(value.isEmpty()){
        					index++;
        					continue;
        				}
    					
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				}
    				
    				index++;
    			}
    			
    			//Convert the date and time + timezone to single date string fields and add to the values map
    			String message = event.updateDataPairs();
    			if(message != null){
    				response = HtmlStatusResponseHelper.createStatusResponse(true,
    	                    "Content error:  "+message+" at line "+lineCount +". Process aborted");
    	        	return response;
    			}
    			
    			eventList.add(event);
    			lineCount++;
    		}
    	} catch(IOException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Input/Output error at line "+lineCount +". Process aborted");
        	return response;
    	} catch(NullPointerException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "General error at line "+lineCount +". Process aborted");
        	throw e;
    	} catch(Exception e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "General error at line "+lineCount +". Process aborted");
        	return response;
    	}
    	
    	
    	//TODO create 10 new tags one new event    	
    	//Once the file has been parsed successfully add the events to the repository
    	//Add and Activate any new tags
    	try {
	    	Set<String> tagSet = existingCategories.keySet();
	    	for(String tagName : tagSet){
	    		Tag content = existingCategories.get(tagName);
	    		if(content == null){
	    			String tagId = councilName + ":categories/" + getJcrName(tagName);
	    			Tag temp = tagManager.createTag(tagId, tagName, "");
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
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Unable to modify tags. Critical error. Process aborted");
        	return response;
		} catch (InvalidTagFormatException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Invalid tag format. Critical error. Process aborted");
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
	    				response = HtmlStatusResponseHelper.createStatusResponse(true,
	    	                    "Path at line "+lineCounter 
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
				
				
				imageNode.setProperty("path", event.getImagePath());
				
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
	    	
	    	
	    	rootNode.save();
    	} catch (RepositoryException e) {
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Encountered repository error at line "+lineCounter 
                    +". Process aborted." + e.getMessage());
        	return response;
		}
    	
    	
    	response = delayedActivate(rootNode, scriptHelper, replicationList, adminResolver);
    	
    	if(response == null){
        	int mins = lineCounter / documentDelayInterval;
        	mins++;
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Events updated successfully. The data will replicate overnight.");
        }
    	
    	
    	return  response;
    }
    
    
    public HtmlResponse updateFormMetadata(List<String> headers, String rootPath, CSVReader csvR, SlingScriptHelper scriptHelper, ResourceResolver adminResolver){
    	
    	HtmlResponse response = null;
    	List<String> replicationList = new ArrayList<String>();
    	List<Document> documentList = new ArrayList<Document>();
    	Node rootNode = null;
    	
    	
    	//Acquire root node. If doesn't exist return error
    	Resource resource = adminResolver.getResource(rootPath);
        if(resource!=null) {
            rootNode = resource.adaptTo(Node.class);
        } else{
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Root node does not exist. Process aborted");
        	return response;
        }
    	
    	//Replace headers
    	headers = this.replaceFormHeaders(headers);
    	
    	
    	//Parse file
    	
    	String[] nextLine = null;
    	int lineCount = 2;
    	try{
    		while((nextLine = csvR.readNext()) != null){
    			List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
    			clearNullValues(values, headers.size());
    			Document doc = new Document();
    			int indexCounter = 0;
    			for(String value: values){
    				if(headers.get(indexCounter).equals(Document.TITLE_PROP)){
    					value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    					doc.setTitle(value);	
    				} else if(headers.get(indexCounter).equals(Document.PATH_PROP)){
    					doc.setPath(value);
    				} else if(headers.get(indexCounter).equals(Document.TAGS_PROP)){
    					if(!value.trim().isEmpty()){
    						value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    						String[] tagVals = value.split(",");
    						List<String> taglist = new LinkedList<String>(Arrays.asList(tagVals));
    						doc.setTags(taglist);
    					}
    				} else if(headers.get(indexCounter).equals(Document.DESCRIPTION_PROP)){
    					value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    					doc.setDescription(value);
    				} else{
    					
    				}
    				
    				
    				
    				indexCounter++;
    			}
    			
    			documentList.add(doc);
    			lineCount++;
    		}
    	} catch(IOException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Input/Output error at line "+lineCount +". Process aborted");
        	return response;
    	} catch(IllegalArgumentException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Issue with formatting date or time at line "+lineCount +". Process aborted");
        	return response;
    	} catch(Exception e){
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "General error at line "+lineCount +". Process aborted");
        	return response;
    	}
    	
    	int lineCounter = 2;
    	try {
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
	    			response = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Document at line "+lineCounter +" not found. Please check that the path is correct. Process aborted");
	            	return response;
	    		}
	    		
	    		lineCounter++;
	    		
	    	}
	    	
	    	rootNode.save();
    	} catch (RepositoryException e) {
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Document at line "+lineCounter +" encountered issue writing to repository. Process aborted with message: " + e.getMessage());
        	return response;
		}
    	
    	//Activate contacts 
        response = staggerActivate(scriptHelper, replicationList, documentDelayInterval, repDelayTimeInMinutes, adminResolver);
    	
        if(response == null){
        	int mins = lineCounter / documentDelayInterval;
        	mins++;
        	response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Documents updated successfully. The data will finish replicating in approximately " + mins + " minutes.");
        }
    	
    	return response;
    }
    
    
    
    private HtmlResponse delayedActivate(Node rootNode, SlingScriptHelper scriptHelper, List<String> replicationList, ResourceResolver adminResolver){
    	
    	HtmlResponse response = null;
    	
    	String delayNodeName = PageActivationUtil.getDateRes();
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
	    		delayedParent.save();
    		} catch(RepositoryException e){
    			response = HtmlStatusResponseHelper.createStatusResponse(true,
                        "Critical Activation Error. Error while setting up delayed activation");
            	return response;
    		}
    		
    	} else{
    		response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Critical Activation Error. Delayed activation node is not available");
        	return response;
    	}
    	
    	
    	return null;
    }
    
    private HtmlResponse staggerActivate(SlingScriptHelper scriptHelper, List<String> replicationList, int interval, int delayTimeInMinutes, ResourceResolver adminResolver){
    	 
    	HtmlResponse response = null;
    	String wfNodePath = null;
    	try {
    		Node parentNode = null;
	    	Node etcNode = adminResolver.getResource("/etc").adaptTo(Node.class);
	    	String stagNodeName = PageActivationUtil.getDateRes();
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
	    	etcNode.save();
	    	
	    	
	    	//Kick off the workflow to replicate the created nodes
	    	WorkflowService wfService = scriptHelper.getService(WorkflowService.class);
	
	    	WorkflowSession wfSession = wfService.getWorkflowSession(adminResolver.adaptTo(Session.class));
	
	    	WorkflowModel model;
		
			model = wfSession.getModel("/etc/workflow/models/staggered-activate/jcr:content/model");
			wfNodePath = "/etc/" + STAG_ACTIVATION + "/" + stagNodeName;

			WorkflowData data = wfSession.newWorkflowData("JCR_PATH", stagNode.getPath());
			

			wfSession.startWorkflow(model, data);
		} catch (WorkflowException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Critical Activation Error. Failed to initiate workflow. Due to error: " + e.getMessage());
        	return response;
		} catch (RepositoryException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Critical Activation Error. Failed while accessing repository. Due to error: " + e.getMessage());
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
			.replaceAll("Time Zone \\(Only enter if you want timezone to be visible e.g. 10:30 PM EST. See http://joda-time.sourceforge.net/timezones.html for valid IDs\\)","jcr:content/data/timezone")
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
    
    private HtmlResponse backupContacts(SlingScriptHelper scriptHelper, String rootPath, Node rootNode ){
    	
    	HtmlResponse response = null;
    	try{
	        Packaging packaging = scriptHelper.getService(Packaging.class);
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
		}catch(Exception e){
            response = HtmlStatusResponseHelper.createStatusResponse(true,
                    "Failed to create contact backup package");
            
            e.printStackTrace();
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
    	public static final String TIME_ZONE = "jcr:content/data/timezone";
    	public static final String REGISTRATION_OPEN_DATE = "jcr:content/data/regOpen-date";
    	public static final String REGISTARTION_OPEN_TIME = "jcr:content/data/regOpen-time";
    	public static final String REGISTRATION_CLOSE_DATE = "jcr:content/data/regClose-date";
    	public static final String REGISTRATION_CLOSE_TIME = "jcr:content/data/regClose-time";
    	public static final String REGION = "jcr:content/data/region";
    	public static final String LOCATION_NAME = "jcr:content/data/locationLabel";
    	public static final String ADDRESS = "jcr:content/data/address";
    	public static final String TEXT = "jcr:content/data/details";
    	public static final String SEARCH_DESCRIPTION = "jcr:content/data/srchdisp";
    	public static final String COLOR = "jcr:content/data/color";
    	public static final String REGISTRATION = "jcr:content/data/register";
    	public static final String CATEGORIES = "jcr:content/cq:tags-categories";
    	public static final String PROGRAM_LEVELS = "jcr:content/cq:tags-progLevel";
    	public static final String PATH = "jcr:path";
    	public static final String IMAGE = "jcr:content/data/imagePath";
    	public static final String PROGRAM_TYPE = "jcr:content/data/progType";
    	public static final String GRADES = "jcr:content/data/grades";
    	public static final String GIRL_FEE = "jcr:content/data/girlFee";
    	public static final String ADULT_FEE = "jcr:content/data/adultFee";
    	public static final String MINIMUM_ATTENDANCE = "jcr:content/data/minAttend";
    	public static final String MAXIMUM_ATTENDANCE = "jcr:content/data/maxAttend";
    	public static final String PROGRAM_CODE = "jcr:content/data/programCode";
    	
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
	    		return "Issue with formatting of start date or time";
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
	    		return "Issue with formatting of end date or time";
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
	    		return "Issue with formatting of registration open date or time";
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
	    		return "Issue with formatting of registration close date or time";
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
			if(dateSplit.length == 3){
				String endYear = dateSplit[2];
				if(endYear.length() < 4){
					endYear = "20" + endYear;
				}
				formatDate = dateSplit[0] + "/" + dateSplit[1] + "/" + endYear;
				
			}
			
			String startingFormat = formatDate + "T" + time + " -05:00";
			GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'hh:mm a Z");
        	GSDateTime dt = GSDateTime.parse(startingFormat,dtfIn);
        	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        	String result = dtfOut.print(dt);
        	System.out.println("Bulkeditor Converting Starting date format " + startingFormat + " to " + result);
        	if(this.timezone != null){
        		GSDateTimeFormatter dtfOutZone = GSDateTimeFormat.forPattern("ZZ");
        		GSDateTimeZone dtz = GSDateTimeZone.forID(this.timezone);
			    dt = GSDateTime.parse(result, dtfIn);
			    dt = dt.withZone(dtz);
			    String timeZoneOffset = dtfOutZone.print(dt);
			    //result = result.substring(0,result .lastIndexOf("-")) + timeZoneOffset;
        	}
        	
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

		public static final String FILE_NAME_PROP="pdf:Title";
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