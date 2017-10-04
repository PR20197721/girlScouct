package apps.wcm.core.components.gsbulkeditor;

import org.apache.commons.lang.ArrayUtils;

import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.Page;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.day.cq.replication.Replicator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.jcr.*;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.girlscouts.web.events.search.GSDateTime;
import org.girlscouts.web.events.search.GSDateTimeFormatter;
import org.girlscouts.web.events.search.GSDateTimeFormat;

import com.day.jcr.vault.packaging.JcrPackageManager;
import com.day.jcr.vault.packaging.JcrPackage;
import com.day.jcr.vault.packaging.JcrPackageDefinition;
import com.day.jcr.vault.fs.config.DefaultWorkspaceFilter;
import com.day.jcr.vault.fs.api.PathFilterSet;
import com.day.jcr.vault.util.DefaultProgressListener;
import com.day.jcr.vault.packaging.Packaging;

import java.io.PrintWriter;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.girlscouts.web.events.search.*;

import com.opencsv.CSVReader;

import java.nio.charset.StandardCharsets;

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
    int repDelayInterval = 10;
    int repDelayTimeInSeconds = 60;

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
            
            if(rootPath!=null) {
                Node rootNode = null;

                Resource resource = request.getResourceResolver().getResource(rootPath);
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
            			SlingBindings bindings = null;
            			SlingScriptHelper scriptHelper = null;
            			try{
            				bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
            				scriptHelper = bindings.getSling();
            			}catch(Exception e){
                            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                                    "Could not resolve sling helper");
                            htmlResponse.send(response, true);
                            return;
            			}
            			
                		
	                    //manage headers
	                    String lineBuffer = bufferReader.readLine();
	                    
	                    if (lineBuffer != null) {
	                        
	                        	
	                    	CSVReader csvR = new CSVReader(bufferReader);
	                    	List<String> headers = new LinkedList<String>(Arrays.asList(csvR.readNext()));
	                    	try{
		                    	//GS - We can't make lots of asset packages
		            			if(importType.equals("contacts")){
			                		
			                		htmlResponse = updateContacts(headers, rootPath, councilName, csvR, scriptHelper, request);	
			                		
		            			} else if(importType.equals("events")){
		            				htmlResponse = updateEvents(headers, rootPath, year, csvR, scriptHelper, request);
		            			} else if(importType.equals("documents")){
		            				htmlResponse = updateFormMetadata(headers, rootPath, csvR, scriptHelper, request);
		            			} else{
		            				htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
			                                "Unsupported import type.");
		            			}
	            			
	                    	} catch(Exception e){
	                			htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
		                                "General Exception occured while processing content: " + e.getMessage());
	                		}
	                            
	                            
                                	
                               
                            //SEND SUCCESS RESPONSE HERE
                            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                                "Content Imported Successfully.");
                                
	                            
	                       
	                    } else {
	                        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	                                "Empty document");
	                    }
                	} else {
                		htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                                "Please increase the depth of the root path to at least two");
                	}
                } else {
                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                        "Invalid root path");
                }
            } else {
                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                    "No root path provided");
            }
        } else {
            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                    "No document provided");
        }

        htmlResponse.send(response, true);
    }
    
    
    //This method processes the contacts file
    public HtmlResponse updateContacts(List<String> headers, String rootPath, String councilName, CSVReader csvR, SlingScriptHelper scriptHelper, SlingHttpServletRequest request){
    	
    	List<String> replicationList = new ArrayList<String>();
    	Map<String,List<Contact>> teamMap = new HashMap<String,List<Contact>>();
    	HtmlResponse response = null;
    	Node rootNode = null;
    	int nameIndex = headers.indexOf(Contact.NAME_PROP);
    	int jobTitleIndex = headers.indexOf(Contact.JOB_TITLE_PROP);
    	int emailIndex = headers.indexOf(Contact.EMAIL_PROP);
    	int phoneIndex = headers.indexOf(Contact.PHONE_PROP);
    	int teamIndex = headers.indexOf(Contact.TEAM_PROP);
    	
    	int sumOfIndexes = nameIndex + jobTitleIndex + emailIndex + phoneIndex + teamIndex;
    	if(sumOfIndexes != 10){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Missing required headers. Process aborted");
    		return response;
    	}
    	
    	
    	//Acquire root node. If doesn't exist resturn error
    	Resource resource = request.getResourceResolver().getResource(rootPath);
        if(resource!=null) {
            rootNode = resource.adaptTo(Node.class);
        } else{
        	response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Root node does not exist. Process aborted");
        	return response;
        }
        
        //Try to create a backup contact package
        response = backupContacts(scriptHelper, rootPath, rootNode);
        if(response != null){
        	return response;
        }
        
        
    	
    	//Parse CSV file line by line
    	String[] nextLine = null;
    	int lineCount = 1;
    	try{
	    	while((nextLine = csvR.readNext()) != null){
	    		//Convert array to list
	    		List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
	    		clearNullValues(values, headers.size());
	    		Contact contact = new Contact();
	    		int indexCount = 0;
	    		for(String value : values){
				
					if(indexCount == nameIndex){ 
						contact.setName(value);
					} else if(indexCount == jobTitleIndex){
						contact.setJobTitle(value);
					} else if(indexCount == emailIndex){
						contact.setEmail(value);
					} else if(indexCount == phoneIndex){
						contact.setPhone(value);
					} else if(indexCount == teamIndex){
						contact.setTeam(value);
					} else{
						response = HtmlStatusResponseHelper.createStatusResponse(false,
			                    "Unexpected additional column. Process aborted");
			            return response;
					}
				
	    			indexCount++;
	    		}
	    		String jcrName = this.getJcrName(contact.getName().trim());
	    		String jcrTeamName = this.getJcrName(contact.getTeam().trim());
	    		if(jcrName == null || jcrTeamName == null || jcrName.isEmpty() || jcrTeamName.isEmpty()){
	    			response = HtmlStatusResponseHelper.createStatusResponse(false,
		                    "Required fields blank at line:" + lineCount +". Process aborted");
		            return response;
	    		}
	    		
	    		contact.setPath(rootPath + "/" + jcrTeamName + "/" + jcrName);
	    		
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
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Critical Issue Parsing the input file at:" + lineCount +". Process aborted");
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
            		Resource childRes = request.getResourceResolver().getResource(child.getPath());
            		Node childNode = childRes.adaptTo(Node.class);
            		childNode.remove();
            	}
            }
            //If deleting the contacts does not work create an error and exit method
        }catch(Exception e){
            response = HtmlStatusResponseHelper.createStatusResponse(false,
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
					teamNode = rootNode.addNode(teamPathName,"cq:Page");
					teamContentNode = teamNode.addNode("jcr:content","cq:PageContent");
					teamContentNode.setProperty("jcr:title",team);
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
						Node contactNode = teamNode.addNode(contact.getPath(),"cq:Page");
						Node contactContentNode = contactNode.addNode("jcr:content","cq:PageContent");
						
							contactContentNode.setProperty("cq:scaffolding","/etc/scaffolding/" + councilName + "/contact");
						
						contactContentNode.setProperty("sling:resourceType","girlscouts/components/contact-page");
						contactContentNode.setProperty("jcr:title",contact.getName());
						contactContentNode.setProperty("title",contact.getJobTitle());
						contactContentNode.setProperty("phone",contact.getPhone());
						contactContentNode.setProperty("email",contact.getEmail());
						contactContentNode.setProperty("team",contact.getTeam());
						replicationList.add(contactNode.getPath());
					}
				}
			}
    	} catch (RepositoryException e) {
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Critical Error While Writing Data to Repository. Process Aborted");
            return response;
		}
    	
    		
    	//Upon successful writing try to save the rootNode if not successful abort
        try{
        	rootNode.save();
        } catch(Exception e){
        	response = HtmlStatusResponseHelper.createStatusResponse(false,
                "Unable to save Contacts. Process Aborted");
            	return response;
        }
        
        
        //Replicate the created contacts
        try{
    		Session session = rootNode.getSession();
    		Replicator replicator = scriptHelper.getService(Replicator.class);

    		for(String repPath : replicationList){
	        	try{
	    			replicator.replicate(session, ReplicationActionType.ACTIVATE, repPath);
	    		}catch(Exception e1){
	    			response = HtmlStatusResponseHelper.createStatusResponse(false,
	    	                "Unable to replicate. Contacts have been created but replication will need to be done manually.");
	    	            	return response;
	    		}
	        }
        }catch(Exception e){
        	response = HtmlStatusResponseHelper.createStatusResponse(false,
	                "Unable to replicate. Contacts have been created but replication will need to be done manually.");
	            	return response;
		}
    	
    	return  response;
    }
    
    public HtmlResponse updateEvents(List<String> headers, String rootPath, String year, CSVReader csvR, SlingScriptHelper scriptHelper, SlingHttpServletRequest request){
    	
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
    	
    	//Check for year and root node
    	if(!year.equals("")){
	    	Resource rootRes = request.getResourceResolver().getResource(rootPath + "/" + year);
	    	if(rootRes == null){
	    		rootRes = request.getResourceResolver().getResource(rootPath);
	    		if(rootRes == null){
	    			response = HtmlStatusResponseHelper.createStatusResponse(true,
	                        "Root path does not exist. Process aborted");
	                return response;
	    		}else{
	        		Node parentNode = rootRes.adaptTo(Node.class);
	        		Node newYearNode = null;
	        		Node newYearContentNode = null;
	        		try{
	        			newYearNode = parentNode.addNode(year,"cq:Page");
	        			newYearContentNode = newYearNode.addNode("jcr:content","cq:PageContent");
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
        	tagManager = request.getResourceResolver().adaptTo(TagManager.class);
			councilName = rootNode.getAncestor(2).getName();
			String categoriesTagId = councilName + ":categories";
			String programLevelTagId = councilName + ":program-level";
			categoriesTag = tagManager.resolve(categoriesTagId);
			programLevelTag = tagManager.resolve(programLevelTagId);
			
			if(categoriesTag != null){
				Iterator<Tag> listOfChildren = categoriesTag.listChildren();
				Tag tempTag = null;
				while((tempTag = listOfChildren.next()) != null){
					existingCategories.put(tempTag.getTitle(), tempTag);
				}
				
			} else{
				categoriesTag = tagManager.createTag(categoriesTagId, "Categories", "");
			}
			
			if(programLevelTag != null){
				Iterator<Tag> listOfChildren = programLevelTag.listChildren();
				Tag tempTag = null;
				while((tempTag = listOfChildren.next()) != null){
					existingProgramLevels.put(tempTag.getTitle(), tempTag);
				}
			} else{
				programLevelTag = tagManager.createTag(programLevelTagId, "Categories", "");
			}
			
		} catch (RepositoryException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Unable to access the repository. Critical error. Process aborted");
        	return response;
		} catch (AccessControlException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Unable to modify tags. Critical error. Process aborted");
        	return response;
		} catch (InvalidTagFormatException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(false,
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
    	int lineCount = 1;
    	try{
    		while((nextLine = csvR.readNext()) != null){
    			List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
    			clearNullValues(values, headers.size());
    			Event event = new Event();
    			int index = 0;
    			for(String value : values){
    				String header = headers.get(index);
    				if(value.isEmpty()){
    					continue;
    				}
    				
    				if (header.equals(Event.END_DATE)){
    					event.setEndDate(value);
    				} else if (header.equals(Event.END_TIME)){
    					event.setEndTime(value);
    				} else if (header.equals(Event.START_DATE)){
    					event.setStartDate(value);
    				} else if (header.equals(Event.START_TIME)){
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
    						event.addDataPair(header, value);
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
    					event.setCategories(tagTitleList);
    				} else if (header.equals(Event.TITLE)){
    					event.setTitle(value);
    				} else{
    					
    					event.addDataPair(header.replace("jcr:content/data/", ""), value);
    				}
    				
    				index++;
    			}
    			
    			//Convert the date and time + timezone to single date string fields and add to the values map
    			event.updateDataPairs();
    			
    			eventList.add(event);
    			lineCount++;
    		}
    	} catch(IOException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Input/Output error at line "+lineCount +". Process aborted");
        	return response;
    	} catch(IllegalArgumentException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Issue with formatting date or time at line "+lineCount +". Process aborted");
        	return response;
    	} catch(Exception e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "General error at line "+lineCount +". Process aborted");
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
	    			existingCategories.put(tagName, temp);
	    		}
	    	}
	    	
	    	tagSet = existingProgramLevels.keySet();
	    	for(String tagName : tagSet){
	    		Tag content = existingCategories.get(tagName);
	    		if(content == null){
	    			String tagId = councilName + ":program-levels/" + getJcrName(tagName);
	    			Tag temp = tagManager.createTag(tagId, tagName, "");
	    			existingProgramLevels.put(tagName, temp);
	    		}
	    	}
    	} catch (AccessControlException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Unable to modify tags. Critical error. Process aborted");
        	return response;
		} catch (InvalidTagFormatException e) {
			response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Ivalid tag format. Critical error. Process aborted");
        	return response;
		}
    	
    	
    	//Add any events that have a blank path
    	//Throw error if path of an event is not found
    	try {
	    	int lineCounter = 1;
	    	for(Event event : eventList){
	    		Node eventNode = null;
	    		if(event.getPath().isEmpty()){
	    			//Create new event node
	    			String id = event.getStartDate();
	    			id = id.replaceAll("[^0-9]", "-");
	    			String eventName = getJcrName(event.title);
	    			eventName = eventName + "_" + id;
	    			while(rootNode.hasNode(eventName)){
	    				eventName = eventName + "1";
	    			}
	    			
	    			eventNode = rootNode.addNode(eventName);
	    			
	    			 
	    		} else{
	    			//Check if path is correct
	    			Resource eventResource = request.getResourceResolver().getResource(event.getPath());
	    			if(eventResource != null){
	    				eventNode = eventResource.adaptTo(Node.class);
	    					
	    				
	    			} else{
	    				response = HtmlStatusResponseHelper.createStatusResponse(false,
	    	                    "Path at line "+lineCounter 
	    	                    +" points to an event that doesn't exist. Fix path or remove altogether if creating new event. Process aborted");
	    	        	return response;
	    			}
	    			
	    		}
	    		
	    		eventNode.setProperty("jcr:title", event.getTitle());
				
				//Set the data values
				Node dataNode = eventNode.getNode("data");
				
				if(dataNode == null){
					dataNode = eventNode.addNode("data");
				}
				
				Map<String, String> valueMap = event.getDataPairs();
				Set<String> valueKeys = valueMap.keySet();
				for(String key : valueKeys){
					dataNode.setProperty(key, valueMap.get(key));
				}
				Node imageNode = null;
				if(dataNode.hasNode("image")){
					imageNode = dataNode.getNode("image");
				} else{
					imageNode = dataNode.addNode("image");
				}
				
				imageNode.setProperty("path", event.getImagePath());
				
				//Set Tags
				String[] categories = tagListToIdArray(event.getCategories(), existingCategories);
				String[] programLevels = tagListToIdArray(event.getCategories(), existingProgramLevels);
				String[] allTags = (String[])ArrayUtils.addAll(categories, programLevels);
				
				eventNode.setProperty("cq:tags", allTags);
	    		
	    		lineCounter ++;
	    	}
	    	
	    	rootNode.save();
    	} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    	
    	return  null;
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
    
    
    public HtmlResponse updateFormMetadata(List<String> headers, String rootPath, CSVReader csvR, SlingScriptHelper scriptHelper, SlingHttpServletRequest request){
    	
    	HtmlResponse response = null;
    	List<Document> documentList = new ArrayList<Document>();
    	Node rootNode = null;
    	
    	//Acquire root node. If doesn't exist return error
    	Resource resource = request.getResourceResolver().getResource(rootPath);
        if(resource!=null) {
            rootNode = resource.adaptTo(Node.class);
        } else{
        	response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Root node does not exist. Process aborted");
        	return response;
        }
    	
    	//Replace headers
    	headers = this.replaceFormHeaders(headers);
    	
    	
    	//Parse file
    	
    	String[] nextLine = null;
    	int lineCount = 1;
    	try{
    		while((nextLine = csvR.readNext()) != null){
    			List<String> values = new LinkedList<String>(Arrays.asList(nextLine));
    			clearNullValues(values, headers.size());
    			
    			Document doc = new Document();
    			int indexCounter = 0;
    			for(String value: values){
    				if(headers.get(indexCounter).equals(Document.TITLE_PROP)){
    					doc.setTitle(value);	
    				} else if(headers.get(indexCounter).equals(Document.FILE_NAME_PROP)){
    					doc.setFileName(value);
    				} else if(headers.get(indexCounter).equals(Document.TAGS_PROP)){
    					if(!value.trim().isEmpty()){
    						value = value.replaceAll("\\[", "").replaceAll("\\]", "");
    						String[] tagVals = value.split(",");
    						List<String> taglist = new LinkedList<String>(Arrays.asList(tagVals));
    						doc.setTags(taglist);
    					}
    				} else if(headers.get(indexCounter).equals(Document.DESCRIPTION_PROP)){
    					doc.setDescription(value);
    				} else{
    					
    				}
    				
    				
    				
    				indexCounter++;
    			}
    			
    			
    			lineCount++;
    		}
    	} catch(IOException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Input/Output error at line "+lineCount +". Process aborted");
        	return response;
    	} catch(IllegalArgumentException e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Issue with formatting date or time at line "+lineCount +". Process aborted");
        	return response;
    	} catch(Exception e){
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "General error at line "+lineCount +". Process aborted");
        	return response;
    	}
    	
    	try {
	    	//Once file parsed successfully commit changes to memory
	    	int lineCounter = 0;
	    	for(Document doc : documentList){
	    		//Check that document exists
	    		Resource docResource = request.getResourceResolver().getResource(doc.getPath());
	    		if(docResource != null){
	    			Node docNode = docResource.adaptTo(Node.class);
	    			Node contentNode = null;
	    			Node metaDataNode = null;
	    			
					if(docNode.hasNode("jcr:content")){
						contentNode = docNode.getNode("jcr:content");
					} else{
						contentNode = docNode.addNode("jcr:content");
					}
					
					if(contentNode.hasNode("metadata")){
						metaDataNode = contentNode.getNode("metadata");
					} else{
						metaDataNode = contentNode.addNode("metadata");
					}
	    			
					metaDataNode.setProperty(Document.TITLE_PROP, doc.getTitle());
					metaDataNode.setProperty(Document.FILE_NAME_PROP, doc.getFileName());
					metaDataNode.setProperty(Document.DESCRIPTION_PROP, doc.getDescription());
					metaDataNode.setProperty(Document.TAGS_PROP, (String[])doc.getTags().toArray());
	    			
	    		} else{
	    			response = HtmlStatusResponseHelper.createStatusResponse(false,
	                        "Document at line "+lineCount +" not found. Please check that the path is correct. Process aborted");
	            	return response;
	    		}
	    		
	    		lineCounter++;
	    		
	    	}
	    	
	    	rootNode.save();
    	} catch (RepositoryException e) {
    		response = HtmlStatusResponseHelper.createStatusResponse(false,
                    "Document at line "+lineCount +" encountered issue writing to repository. Process aborted");
        	return response;
		}
    	
    	
    	
    	
    	return null;
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
    		
    		return new LinkedList<String>(Arrays.asList(allHeaders.split("|")));	
    		
    	}
    	
    	return headers;
    		
    	
    }
    
    private List<String> replaceFormHeaders(List<String> headers){
    	if(!headers.isEmpty()){
    		String allHeaders = headers.remove(0);
    		for(String header : headers){
    			allHeaders = allHeaders + "|" + header;
    		}
    		
    		allHeaders = allHeaders.replaceAll("Title","jcr:content/metadata/dc:title")
    				.replaceAll("Description","jcr:content/metadata/dc:description")
    				.replaceAll("Categories","jcr:content/metadata/cq:tags")
    				.replaceAll("Path","jcr:path");
    		
    		return new LinkedList<String>(Arrays.asList(allHeaders.split("|")));	
    		
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
    
    private void clearNullValues(List<String> list, int size){
    	int i = 0;
    	for(i = 0; i < list.size(); i++){
    		if(list.get(i) == null){
    			list.set(i, "");
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
            response = HtmlStatusResponseHelper.createStatusResponse(false,
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
    	
    	public Event(){
    		this.dataPairs = new HashMap<String,String>();
    		
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

		public void setDataPairs(Map<String, String> dataPairs) {
			this.dataPairs = dataPairs;
		}

		public void addDataPair(String key, String value){
    		this.dataPairs.put(key, value);
    	}
		
		public void updateDataPairs(){
			String start = convertDateAndTime(this.startDate, this.startTime);
			addDataPair("start", start);
			
			String end = convertDateAndTime(this.endDate, this.endTime);
			addDataPair("end", start);
			
			if(regOpenDate != null && regOpenTime != null){
				String regOpen = convertDateAndTime(this.regOpenDate, this.regOpenTime);
				addDataPair("regOpen", regOpen);
			}
			if(regCloseDate != null && regCloseTime != null){
				String regClose = convertDateAndTime(this.regCloseDate, this.regCloseTime);
				addDataPair("regClose", regClose);
			}
		}
		
		private String convertDateAndTime(String date, String time){
			String startingFormat = date + "T" + time + " -05:00";
			GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'hh:mm a Z");
        	GSDateTime dt = GSDateTime.parse(startingFormat,dtfIn);
        	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        	String result = dtfOut.print(dt);
        	if(this.timezone != null){
        		GSDateTimeFormatter dtfOutZone = GSDateTimeFormat.forPattern("ZZ");
        		GSDateTimeZone dtz = GSDateTimeZone.forID(this.timezone);
			    GSDateTime baseDateTime = GSDateTime.parse(result, dtfIn);
			    baseDateTime = baseDateTime.withZone(dtz);
			    String timeZoneOffset = dtfOutZone.print(baseDateTime);
			    result = result.substring(0,result .lastIndexOf("-")) + timeZoneOffset;
        	}
        	return result;
        	
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