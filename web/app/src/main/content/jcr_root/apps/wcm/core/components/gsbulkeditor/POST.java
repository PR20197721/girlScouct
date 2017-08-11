/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package apps.wcm.core.components.gsbulkeditor;

import org.apache.commons.lang.ArrayUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.Page;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.day.cq.replication.Replicator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.resource.JcrResourceConstants;

import javax.jcr.*;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
            if(!year.equals("")){
            	if(rootPath != null){
                	Resource rootRes = request.getResourceResolver().getResource(rootPath + "/" + year);
                	if(rootRes == null){
                		rootRes = request.getResourceResolver().getResource(rootPath);
                		if(rootRes == null){
                    		htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                                    "Could not resolve root path");
                            htmlResponse.send(response, true);
                            return;
                		}else{
                    		Node parentNode = rootRes.adaptTo(Node.class);
                    		Node newYearNode = null;
                    		Node newYearContentNode = null;
                    		try{
                    			newYearNode = parentNode.addNode(year,"cq:Page");
                    			newYearContentNode = newYearNode.addNode("jcr:content","cq:PageContent");
                    		}catch(Exception e){
                        		htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                                        "Failed to create year node");
                                htmlResponse.send(response, true);
                                return;
                    		}
                		}
                	}
            	}
            	
            	rootPath = rootPath + "/" + year;
            }
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
                            e.printStackTrace();
                            return;
            			}
            			//GS - We can't make lots of asset packages
            			if(importType.equals("contacts")){
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
	                            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	                                    "Failed to create package");
	                            htmlResponse.send(response, true);
	                            e.printStackTrace();
	                            return;
	                		}
            			}
                	
	                    long counter = System.currentTimeMillis();
	
	                    //manage headers
	                    String lineBuffer = bufferReader.readLine();
	                    if(importType.equals("documents")){
	                    	lineBuffer = lineBuffer.replaceAll("Title","jcr:content/metadata/dc:title").replaceAll("Description","jcr:content/metadata/dc:description").replaceAll("Categories","jcr:content/metadata/cq:tags").replaceAll("Path","jcr:path");
	                    }else if(importType.equals("events")){
	                    	lineBuffer = lineBuffer.replaceAll("Title","jcr:content/jcr:title")
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
	                    }
	                    if (lineBuffer != null) {
	                        List<String> headers = Arrays.asList(lineBuffer.split(DEFAULT_SEPARATOR + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
	                        List<String> headersLowerCase = Arrays.asList(lineBuffer.toLowerCase().split(DEFAULT_SEPARATOR + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));
	                        if (headers.size() > 0) {
	                        	//GS: The importer expects a jcr:path header, but it can use the rootpath from the querybuilder if no path is present
	                            int pathIndex = headers.indexOf(JcrConstants.JCR_PATH);
	                            Replicator replicator;
	                            //GS: Check if the type is "contacts." If so, deactivate and delete everything under the rootpath
	                            if(importType.equals("contacts")){
		                            try{
			                            if(rootNode.getProperty("jcr:content/sling:resourceType").getString().equals("girlscouts/components/contact-placeholder-page")){
			                            	Page rootPage = resource.adaptTo(Page.class);
			                            	Iterator<Page> oldChildren = rootPage.listChildren();
			                            	replicator = scriptHelper.getService(Replicator.class);
			                            	while(oldChildren.hasNext()){
			                            		Page child = oldChildren.next();
			                            		replicator.replicate(rootNode.getSession(), ReplicationActionType.DELETE, child.getPath());
			                            		Resource childRes = request.getResourceResolver().getResource(child.getPath());
			                            		Node childNode = childRes.adaptTo(Node.class);
			                            		childNode.remove();
			                            	}
			                            }
		                            }catch(Exception e){
		                                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
		                                        "Failed to delete original contact data. Process Aborted");
		                                htmlResponse.send(response, true);
		                                e.printStackTrace();
		                                return;
		                            }
	                            }
	                            int lineRead = 0, lineOK = 0;
	                            HashMap<String,ArrayList<Contact>> contactsToCreate = new HashMap<String,ArrayList<Contact>>();
	                            ArrayList<String> pathsToReplicate = new ArrayList<String>();
	                            TreeSet<String> allNames = new TreeSet<String>();
	                            CSVReader csvR = new CSVReader(bufferReader);
	                            String[] nextLine;
	                            while((nextLine = csvR.readNext()) != null){
	                                lineRead++;
	                                if(performLine(request,nextLine,headers,pathIndex,rootNode,insertedResourceType,counter++,importType,contactsToCreate,allNames, scriptHelper, pathsToReplicate, councilName)) {
	                                   lineOK++;
	                                }
	                            }
	                            
	                            if(lineOK>0) {
                                	if(importType.equals("contacts")){
    	                                try {
    	                                	replicator = scriptHelper.getService(Replicator.class);
    	                                	createNewContacts(rootNode,contactsToCreate,councilName,rootNode.getSession(),replicator);
    	                                }catch(Exception e){
    	                                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
    		                                        "Error saving new contacts " + e.getMessage());
    	                                    try {
    	                                        rootNode.refresh(false);
    	                                    } catch (InvalidItemStateException e1) {
    	                                    } catch (RepositoryException e1) {
    	                                    }
    	                                	e.printStackTrace();
    	                                	return;
    	                                }
                                	}else if(importType.equals("documents") || importType.equals("events")){
                                		if(pathsToReplicate.size() > 0){
	                                		try{
	                                			replicator = scriptHelper.getService(Replicator.class);
	                                			for(String activatePath : pathsToReplicate){
	                                				replicator.replicate(rootNode.getSession(), ReplicationActionType.ACTIVATE, activatePath);
	                                			}
	                                		}catch(Exception e){
	                                			htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	    		                                        "Error activating documents " + e.getMessage());
	                                			try {
	    	                                        rootNode.refresh(false);
	    	                                    } catch (InvalidItemStateException e1) {
	    	                                    	e1.printStackTrace();
	    	                                    } catch (RepositoryException e1) {
	    	                                    	e1.printStackTrace();
	    	                                    }
	    	                                	e.printStackTrace();
	    	                                	return;
	                                		}
                                		}
                                	}
	                                try {
	                                    rootNode.save();
	                                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                        "Imported " + lineOK + "/" + lineRead + " lines");
	                                } catch (RepositoryException e) {
	                                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	                                        "Error while saving modifications " + e.getMessage());
	                                    e.printStackTrace();
	                                    try {
	                                        rootNode.refresh(false);
	                                    } catch (InvalidItemStateException e1) {
	                                    } catch (RepositoryException e1) {
	                                    }
	                                }
	                            } else {
	                                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                    "Imported " + lineOK + "/" + lineRead + " lines");
	                            }
	                        } else {
	                            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	                                    "Invalid headers");
	                        }
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

    public boolean performLine(SlingHttpServletRequest request, String[] line, List<String> headers, int pathIndex, Node rootNode, String insertedResourceType, long counter, String importType, HashMap<String,ArrayList<Contact>> contactsToCreate, TreeSet<String> allNames, SlingScriptHelper scriptHelper, ArrayList<String> pathsToReplicate, String councilName) {
    	boolean updated = false;
    	for(String s : line){
    		System.out.println(s.replaceAll("[\\u2013\\u2014\\u2015]", "-")
					.replaceAll("[\\u2017]", "_")
					.replaceAll("[\\u2018\\u2019]","'")
					.replaceAll("[\\u201C\\u201D]", "\"")
					.replaceAll("[\\u201D\\u201E]","\"")
					.replaceAll("[\\u2026]","...")
					.replaceAll("[\\u2032]","\'")
					.replaceAll("[\\u2033]","\""));
    	}
        try {
            int headerSize = headers.size();
            List<String> values = new LinkedList<String>(Arrays.asList(line));
            //values.remove(values.size() - 1);
            if(values.size() < headerSize) {
                //completet missing last empty cols
                for(int i = values.size();i<=headerSize;i++) {
                    values.add("");
                }
            }
            Node node = null;

            String path = (pathIndex > -1 ? values.get(pathIndex) : null);

            Resource resource = (path==null || path.length()==0 || path.equals(" ") ?
                    null : request.getResourceResolver().getResource(path));
            
            if(importType.equals("contacts")){
            	node = rootNode;
            }else{
            
	            if(resource!=null) {
	                node = resource.adaptTo(Node.class);
	            } else {
	                if(rootNode!=null && !importType.equals("documents") && !importType.equals("contacts") && !importType.equals("events")) {
	                	if(path==null || path.length()==0 || path.equals(" ")) {
	                        path = "" + counter;
	                    } else {
	                        //check if path is under root node.
	                        //for the moment do nothing if path does not exist
	
	                        if(path.startsWith(rootNode.getPath())) {
	                            path = path.substring(rootNode.getPath().length() + 1,path.length());
	                        } else {
	                            return false;
	                        }
	                    }
	
	                    int index = headers.indexOf(JcrConstants.JCR_PRIMARYTYPE);
	                    if(index!=-1) {
	                        String primaryType = values.get(index);
	                        if(primaryType!=null && primaryType.length()>0) {
	                            node = rootNode.addNode(path,primaryType);
	                        } else {
	                            node = rootNode.addNode(path,"nt:unstructured");
	                        }
	                    } else {
	                        node = rootNode.addNode(path,"nt:unstructured");
	                        if(insertedResourceType!=null) {
	                            ValueFactory valueFactory = node.getSession().getValueFactory();
	                            Value value = valueFactory.createValue(insertedResourceType);
	                            if(node.getPrimaryNodeType().canSetProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,value)) {
	                                node.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,value);
	                            }
	                        }
	                    }
	                }
	                if(rootNode!=null && importType.equals("events")){
	                	if(path==null || path.length()==0 || path.equals(" ")) {
		                    int index = headers.indexOf("jcr:content/jcr:title");
		                    if(index!=-1) {
		                    	if(null == values.get(index) || "".equals(values.get(index))){
		                    		return false;
		                    	}
		                    	String newEventPath = values.get(index).toLowerCase().replaceAll("[^A-Za-z0-9]","-");
		                    	if(request.getResourceResolver().getResource(rootNode.getPath() + "/" + newEventPath) != null){
		                    		int nodeNameCounter = 0;
		                    		while(request.getResourceResolver().getResource(rootNode.getPath() + "/" + newEventPath + nodeNameCounter) != null){
			                    		nodeNameCounter++;
			                    	}
		                    		newEventPath = newEventPath + nodeNameCounter;
		                    	}
		                    	
		                    	node = rootNode.addNode(newEventPath,"cq:Page");
		                    	Node jcrNode = node.addNode("jcr:content","cq:PageContent");
		                    	jcrNode.setProperty("sling:resourceType","girlscouts/components/event-page");
		                    	jcrNode.setProperty("cq:scaffolding","/etc/scaffolding/" + councilName + "/event");
		                    	jcrNode.addNode("data","nt:unstructured");
		                    }else{
		                    	return false;
		                    }
	                	}else{
	                		node = rootNode.addNode(path,"nt:unstructured");
	                		Node jcrNode = node.addNode("jcr:content","cq:PageContent");
	                    	jcrNode.addNode("data","nt:unstructured");
	                	}
	                }
	            }
            }

           if(node!=null) {
               ValueFactory valueFactory = node.getSession().getValueFactory();
       		   Contact contact = new Contact();
                for(int i=0;i<headerSize;i++) {
                    if(i!=pathIndex) {
                        String property = headers.get(i);
                        property = property.trim();
                        String additional = "";
                        if(property.endsWith("cq:tags-categories") || property.endsWith("cq:tags-progLevel") || property.endsWith("start-date") || property.endsWith("start-time") || property.endsWith("end-date") || property.endsWith("end-time") || property.endsWith("regOpen-date") || property.endsWith("regOpen-time") || property.endsWith("regClose-date") || property.endsWith("regClose-time")){
                        	additional = property.substring(property.lastIndexOf("-")+1,property.length());
                        	property = property.substring(0,property.lastIndexOf("-"));
                        }
                        if(!property.equals(JcrConstants.JCR_PATH)
                                && !property.equals(JcrConstants.JCR_PRIMARYTYPE) && !property.isEmpty()) {
                        	Value value = valueFactory.createValue(values.get(i));
	                        //GS: If this is a contact spreadsheet, just get the necessary properties for a contact
	                        if(importType.equals("contacts")){
	                    		if(value != null){
	                    			String val = value.getString();
	                    			if(val != null){
	                    				Boolean startsWithOneQuote = (val.matches("^\"[^\"].*") && val.matches(".*[^\"]\"$") && val.indexOf(",") != -1);
	                    				Boolean startsWithMultipleQuotes = (val.matches("^[\"]{3,}[^\"].*") && val.matches(".*[^\"][\"]{3,}$") && val.indexOf(",") != -1);
	                    				if(startsWithMultipleQuotes){
	                    					val = val.replaceAll("^\"\"\"|\"\"\"$","\"");
	                    				}
	                    				else if(startsWithOneQuote && val.length() >= 3){
	                    					val = val.substring(1,val.length()-1);
	                    				}
	                    				if(property.equals(Contact.NAME_PROP)){
	                    					if(allNames.contains(val)){
	                    						int nameIndex = 0;
		                    					while(allNames.contains(val + nameIndex)){
		                    						nameIndex++;
		                    					}
		                    					val = val + nameIndex;
	                    					}
		                    				contact.setName(val);
		                    				allNames.add(val);
		                    				contact.setPath(val.toLowerCase().replaceAll("[^A-Za-z0-9]","-"));
		                    			}else if(property.equals(Contact.JOB_TITLE_PROP)){
		                    				contact.setJobTitle(val);
		                    			}else if(property.equals(Contact.PHONE_PROP)){
		                    				contact.setPhone(val);
		                    			}else if(property.equals(Contact.EMAIL_PROP)){
		                    				contact.setEmail(val);
		                    			}else if(property.equals(Contact.TEAM_PROP)){
		                    				contact.setTeam(val);
		                    			}
		                    		}
	                    		}
	                    	}else if(importType.equals("documents")){
	                    		if(value != null){
	                    			String val = value.getString();
	                    			if(val != null && !"".equals(val)){
	                    				Node updatedNode = node;
	                    				if(property.indexOf('/') != -1){
	        	                            if( property.indexOf('/') != -1) {
	        	                                String childNodeName = property.substring(0,property.lastIndexOf('/'));
	        	                                updatedNode = node.getNode(childNodeName);
	        	                                property = property.substring(property.lastIndexOf('/') + 1);
	        	                            }
	                    				}
	                    				Boolean startsWithOneQuote = (val.matches("^\"[^\"].*") && val.matches(".*[^\"]\"$") && val.indexOf(",") != -1);
	                    				Boolean startsWithMultipleQuotes = (val.matches("^[\"]{3,}[^\"].*") && val.matches(".*[^\"][\"]{3,}$") && val.indexOf(",") != -1);
	                    				if(startsWithMultipleQuotes){
	                    					val = val.replaceAll("^\"\"\"|\"\"\"$","\"");
	                    				}
	                    				else if(startsWithOneQuote && val.length() >= 3){
	                    					val = val.substring(1,val.length()-1);
	                    				}
	                    				Boolean multiValue = val.matches("^\\[.*\\]$");
	                    				String[] multiVal = null;
	                    				if(multiValue){
	                    					val = val.substring(1,val.length()-1);
	                    					multiVal = val.split(",");
	                    				}
	                    				if(property.endsWith("cq:tags")){
	                    					String[] tags = val.split(";");
	                    					if(multiVal != null){
	                    						tags = multiVal;
	                    					}
	                    					ArrayUtils.removeElement(tags,"");
	                    					ArrayUtils.removeElement(tags,null);
	                    					ArrayList<String> tagList = new ArrayList<String>();
	                    					ArrayList<String> tagPathList = new ArrayList<String>();
	                                    	if(tags.length > 0){
	                                    		for(String tag : tags){
	                                    			String tagTitle = tag.trim();
	                                    			String tagID = createID(tagTitle, rootNode.getPath(),"forms_documents","_");
	                                    			if(!tagTitle.isEmpty() && !tagID.isEmpty()){
		                                    			 try{
		                                    				TagManager tm = request.getResourceResolver().adaptTo(TagManager.class);
		                	                    			if(null == tm.resolve(tagID)){
		                	                    				if(tm.canCreateTag(tagID)){
		                	                    					Tag newTag = tm.createTag(tagID,tagTitle,"",true);
		                	                    					tagList.add(tagID);
		                	                    					tagPathList.add(newTag.getPath());
		                	                    				}
		                	                    			}else{
		                	                    				Tag existingTag = tm.resolve(tagID);
		                	                    				tagList.add(tagID);
		                	                    				tagPathList.add(existingTag.getPath());
		                	                    			}
		                                    			}catch(Exception e){
		                                    				System.err.println("GSBulkEditor - Failed to Create Tag");
		                                    			} 
	                                    			}
	                                    		}
	                                    	}
	        	                            if(updatedNode != null){
		        	                            updatedNode.setProperty(property,tagList.toArray(new String[0]));
		        	                        }
	        	                            if(tagPathList.size() > 0){
	        	                            	try{
	        	                            		Session session = rootNode.getSession();
	        	                            		Replicator replicator = scriptHelper.getService(Replicator.class);
		        	                            	for(String tagPath : tagPathList){
		        	                            		try{
		        	                            			replicator.replicate(session, ReplicationActionType.ACTIVATE, tagPath);
		        	                            		}catch(Exception e1){
		        	                            			e1.printStackTrace();
		        	                            		}
		        	                            	}
	        	                            	}catch(Exception e){
	        	                            		e.printStackTrace();
	        	                            	}
	        	                            }
	                    				}else{
	        	                            if(updatedNode != null){
	        	                            	if(multiVal != null){
	        	                            		updatedNode.setProperty(property,multiVal);
	        	                            	}else{
	        	                            		try{
	        	                            			updatedNode.setProperty(property,val);
	        	                            		}catch(ValueFormatException e){
	        	                            			multiVal = new String[1];
	        	                            			multiVal[0] = val;
	        	                            			updatedNode.setProperty(property,multiVal);
	        	                            		}
	        	                            	}
	        	                            }
	                    				}
	                    			}
	                    		}
	                    	}else if(importType.equals("events")){
	                    		if(value != null){
	                    			String val = value.getString();
	                    			val = val.replaceAll("[\\u2013\\u2014\\u2015]", "-")
	                    					.replaceAll("[\\u2017]", "_")
	                    					.replaceAll("[\\u2018\\u2019]","'")
	                    					.replaceAll("[\\u201C\\u201D]", "\"")
	                    					.replaceAll("[\\u201D\\u201E]","\"")
	                    					.replaceAll("[\\u2026]","...")
	                    					.replaceAll("[\\u2032]","\'")
	                    					.replaceAll("[\\u2033]","\"");
	                    			if(val != null && !"".equals(val)){
	                    				Node updatedNode = node;
	                    				if(property.indexOf('/') != -1){
	        	                            if( property.indexOf('/') != -1) {
	        	                                String childNodeName = property.substring(0,property.lastIndexOf('/'));
	        	                                updatedNode = node.getNode(childNodeName);
	        	                                property = property.substring(property.lastIndexOf('/') + 1);
	        	                            }
	                    				}
	                    				Boolean startsWithOneQuote = (val.matches("^\"[^\"].*") && val.matches(".*[^\"]\"$") && val.indexOf(",") != -1);
	                    				Boolean startsWithMultipleQuotes = (val.matches("^[\"]{3,}[^\"].*") && val.matches(".*[^\"][\"]{3,}$") && val.indexOf(",") != -1);
	                    				if(startsWithMultipleQuotes){
	                    					val = val.replaceAll("^\"\"\"|\"\"\"$","\"");
	                    				}
	                    				else if(startsWithOneQuote && val.length() >= 3){
	                    					val = val.substring(1,val.length()-1);
	                    				}
	                    				Boolean multiValue = val.matches("^\\[.*\\]$");
	                    				String[] multiVal = null;
	                    				if(multiValue){
	                    					val = val.substring(1,val.length()-1);
	                    					multiVal = val.split(",");
	                    				}
	                    				if(property.endsWith("cq:tags") && (additional.equals("categories") || additional.equals("progLevel"))){
	                    					String tagFolder = additional;
	                						if(tagFolder.equals("progLevel")){
	                							tagFolder = "program-level";
	                						}
	                    					String[] tags = val.split(";");
	                    					if(multiVal != null){
	                    						tags = multiVal;
	                    					}
	                    					ArrayUtils.removeElement(tags,"");
	                    					ArrayUtils.removeElement(tags,null);
	                    					ArrayList<String> tagList = new ArrayList<String>();
	                    					ArrayList<String> tagPathList = new ArrayList<String>();
	                    					if(updatedNode.hasProperty(property)){
	                    						Property tagsProp = updatedNode.getProperty(property);
	                							Value[] tagsVals = tagsProp.getValues();
	                							TagManager tm = request.getResourceResolver().adaptTo(TagManager.class);
	                							if(tagsVals.length > 0){
	                								for(Value existing : tagsVals){
	                									String existingStr = existing.getString();
	                									if(tagFolder.equals("program-level")){
	                										if(existingStr.matches("^.*:" + "categories" + "/.*$")){
	                        	                    			if(null != tm.resolve(existingStr)){
	                        	                    				Tag existingTag = tm.resolve(existingStr);
	                        	                    				tagList.add(existingStr);
	                        	                    				tagPathList.add(existingTag.getPath());
	                        	                    			}
	                										}
	                									}else if(tagFolder.equals("categories")){
	                										if(existingStr.matches("^.*:" + "program-level" + "/.*$")){
	                        	                    			if(null != tm.resolve(existingStr)){
	                        	                    				Tag existingTag = tm.resolve(existingStr);
	                        	                    				tagList.add(existingStr);
	                        	                    				tagPathList.add(existingTag.getPath());
	                        	                    			}
	                										}
	                									}
	                								}
	                							}
	                    					}
	                    					
	                                    	if(tags.length > 0){
	                                    		for(String tag : tags){
	                                    			String tagTitle = tag.trim();
	                                    			String tagID = "";
	                                    			if(tagFolder.equals("categories")){
	                                    				tagID = createID(tagTitle, rootNode.getPath(), tagFolder,"");
	                                    			}else if(tagFolder.equalsIgnoreCase("program-level")){
	                                    				tagID = createID(tagTitle, rootNode.getPath(), tagFolder,"-");
	                                    			}else{
	                                    				tagID = createID(tagTitle, rootNode.getPath(), tagFolder,"_");
	                                    			}
	                                    			if(!tagTitle.isEmpty() && !tagID.isEmpty()){
		                                    			 try{
		                                    				TagManager tm = request.getResourceResolver().adaptTo(TagManager.class);
		                	                    			if(null == tm.resolve(tagID)){
		                	                    				if(tm.canCreateTag(tagID)){
		                	                    					String rand = "" + System.currentTimeMillis();
		                	                    					Tag newTag = tm.createTag(tagID + rand,tagTitle,"",true);
		                	                    					Resource newTagRes = request.getResourceResolver().getResource(newTag.getPath());
		                	                    					Node newTagNode = newTagRes.adaptTo(Node.class);
		                	                    					Session newTagSession = newTagNode.getSession();
		                	                    					newTagSession.save();
		                	                    					newTagSession.move(newTagNode.getPath(), newTagNode.getPath().substring(0,newTagNode.getPath().indexOf(rand)));
		                	                    					newTagSession.save();
		                	                    					tagList.add(tagID);
		                	                    					tagPathList.add(newTag.getPath());
		                	                    				}
		                	                    			}else{
		                	                    				Tag existingTag = tm.resolve(tagID);
		                	                    				tagList.add(tagID);
		                	                    				tagPathList.add(existingTag.getPath());
		                	                    			}
		                                    			}catch(Exception e){
		                                    				System.err.println("GSBulkEditor - Failed to Create Tag");
		                                    				e.printStackTrace();
		                                    			} 
	                                    			}
	                                    		}
	                                    	}
	        	                            if(updatedNode != null){
		        	                            updatedNode.setProperty(property,tagList.toArray(new String[0]));
		        	                        }
	        	                            if(tagPathList.size() > 0){
	        	                            	try{
	        	                            		Session session = rootNode.getSession();
	        	                            		Replicator replicator = scriptHelper.getService(Replicator.class);
		        	                            	for(String tagPath : tagPathList){
		        	                            		try{
		        	                            			Resource tagRes = request.getResourceResolver().resolve(tagPath);
		        	                            			Node tagNode = tagRes.adaptTo(Node.class);
		        	                            			if(tagNode.hasProperty("cq:lastReplicationAction")){
		        	                            				if(tagNode.getProperty("cq:lastReplicationAction").getValue().getString().equals("Activate")){
		        	                            					replicator.replicate(session, ReplicationActionType.ACTIVATE, tagPath);
		        	                            				}
		        	                            			}else{
		        	                            				replicator.replicate(session, ReplicationActionType.ACTIVATE, tagPath);
		        	                            			}
		        	                            		}catch(Exception e1){
		        	                            			e1.printStackTrace();
		        	                            		}
		        	                            	}
	        	                            	}catch(Exception e){
	        	                            		e.printStackTrace();
	        	                            	}
	        	                            }
	                    				}else if((property.equals("start") || property.equals("end") || property.equals("regOpen") || property.equals("regClose")) && additional.equals("date")){
	                    					if(updatedNode.hasProperty(property)){
		                                    	try{
			                    					Property dateProp = updatedNode.getProperty(property);
			                                    	String dateString = val;
			                                    	String timeString = dateProp.getString().substring(dateProp.getString().indexOf("T"));
			                                    	String dateTimeString = dateString + timeString;
			                                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'HH:mm:ss.SSSZ");
			                                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
			                                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
			                                    	val = dtfOut.print(dt);
	    	                            			updatedNode.setProperty(property,val);
	    	                            		}catch(Exception e){
	    	                            			multiVal = new String[1];
	    	                            			multiVal[0] = val;
	    	                            			updatedNode.setProperty(property,multiVal);
	    	                            		}
	                    					}else{
	                    						String dateString = val;
	                    						String timeString = "T00:00:00.000";
		                                    	String dateTimeString = dateString + timeString;
		                                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yyyy'T'HH:mm:ss.SSS");
		                                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
		                                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		                                    	val = dtfOut.print(dt);
		                                    	try{
		                                    		updatedNode.setProperty(property,val);
		                                    	}catch(Exception e){
		                                    		e.printStackTrace();
		                                    	}
	                    					}
	                    				}else if((property.equals("start") || property.equals("end") || property.equals("regOpen") || property.equals("regClose")) && additional.equals("time")){
	                    					if(updatedNode.hasProperty(property)){
		                    					Property dateProp = updatedNode.getProperty(property);
		                                    	String datetimeString = dateProp.getString();
		                                    	String timeString = val;
		                                    	String dateString = dateProp.getString().substring(0, dateProp.getString().indexOf("T"));
		                                    	String dateTimeString = dateString + "T" + timeString;
		                                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm a");
		                                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
		                                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		                                    	val = dtfOut.print(dt);
		                                    	try{
	    	                            			updatedNode.setProperty(property,val);
	    	                            		}catch(Exception e){
	    	                            			multiVal = new String[1];
	    	                            			multiVal[0] = val;
	    	                            			updatedNode.setProperty(property,multiVal);
	    	                            		}
	                    					}else{
	                    						String timeString = val.toUpperCase();
		                                    	String dateString = "2017-01-01";
		                                    	String dateTimeString = dateString + "T" + timeString + " -05:00";
		                                    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm a Z");
		                                    	GSDateTime dt = GSDateTime.parse(dateTimeString,dtfIn);
		                                    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		                                    	val = dtfOut.print(dt);
		                                    	try{
		                                    		updatedNode.setProperty(property,val);
		                                    	}catch(Exception e){
		                                    		e.printStackTrace();
		                                    	}
	                    					}
	                    				}else if(property.equals("timezone")){
	                    					updatedNode.setProperty(property,"()(" + val + ")");
	                    					GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	                    					GSDateTimeFormatter dtfOutZone = GSDateTimeFormat.forPattern("ZZ");
	                    					if(updatedNode.hasProperty("start")){
	                    						try{
	                    							String startDate = updatedNode.getProperty("start").getString();
	                    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
	                    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
	                    						    startDateTime = startDateTime.withZone(dtz);
	                    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
	                    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
	                    						    updatedNode.setProperty("start",startDate);
	                    						}catch(Exception e){
	                    							e.printStackTrace();
	                    						}
	                    					}
	                    					if(updatedNode.hasProperty("end")){
	                    						try{
	                    							String startDate = updatedNode.getProperty("end").getString();
	                    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
	                    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
	                    						    startDateTime = startDateTime.withZone(dtz);
	                    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
	                    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
	                    						    updatedNode.setProperty("end",startDate);
	                    						}catch(Exception e){
	                    							e.printStackTrace();
	                    						}
	                    					}
	                    					if(updatedNode.hasProperty("regOpen")){
	                    						try{
	                    							String startDate = updatedNode.getProperty("regOpen").getString();
	                    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
	                    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
	                    						    startDateTime = startDateTime.withZone(dtz);
	                    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
	                    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
	                    						    updatedNode.setProperty("regOpen",startDate);
	                    						}catch(Exception e){
	                    							e.printStackTrace();
	                    						}
	                    					}
	                    					if(updatedNode.hasProperty("regClose")){
	                    						try{
	                    							String startDate = updatedNode.getProperty("regClose").getString();
	                    						    GSDateTimeZone dtz = GSDateTimeZone.forID(val);
	                    						    GSDateTime startDateTime = GSDateTime.parse(startDate, dtfIn);
	                    						    startDateTime = startDateTime.withZone(dtz);
	                    						    String timeZoneOffset = dtfOutZone.print(startDateTime);
	                    						    startDate = startDate.substring(0,startDate.lastIndexOf("-")) + timeZoneOffset;
	                    						    updatedNode.setProperty("regClose",startDate);
	                    						}catch(Exception e){
	                    							e.printStackTrace();
	                    						}
	                    					}
	                    				}else{
	        	                            if(updatedNode != null){
	        	                            	if(multiVal != null){
	        	                            		updatedNode.setProperty(property,multiVal);
	        	                            	}else{
	        	                            		try{
	        	                            			updatedNode.setProperty(property,val);
	        	                            		}catch(ValueFormatException e){
	        	                            			multiVal = new String[1];
	        	                            			multiVal[0] = val;
	        	                            			updatedNode.setProperty(property,multiVal);
	        	                            		}
	        	                            	}
	        	                            }
	                    				}
	                    			}
	                    		}
	                        }
                    	}
                    }
                }
                if(importType.equals("contacts")){
	                ArrayList<Contact> contactsForTeam;
	                if(contact.getTeam() != null && contactsToCreate.get(contact.getTeam()) != null){
	                	contactsForTeam = contactsToCreate.get(contact.getTeam());
	                	contactsForTeam.add(contact);
	                }else{
	                	contactsForTeam = new ArrayList<Contact>();
	                	contactsForTeam.add(contact);
	                }
	                contactsToCreate.put(contact.getTeam(),contactsForTeam);
                }else if(importType.equals("events")){
                	//Disabled for the time being
                	/*if(node.hasProperty("jcr:content/cq:lastReplicationAction")){
                		if(node.getProperty("jcr:content/cq:lastReplicationAction").getString().equals("Activate")){
                			pathsToReplicate.add(node.getPath());
                		}
                	} */
                }
                else if(importType.equals("documents")){
                	if(node.hasProperty("jcr:content/cq:lastReplicationAction")){
                		if(node.getProperty("jcr:content/cq:lastReplicationAction").getString().equals("Activate")){

                			String metaPath = path + META_DATA_LOCATION;
                			Resource metaResource = request.getResourceResolver().getResource(metaPath);
                			Node metaNode = null;
                            if(metaResource!=null) {
                            	metaNode = metaResource.adaptTo(Node.class);
                            }
                            if(metaNode != null)
                            	pathsToReplicate.add(metaNode.getPath());
                		}
                	}
                }
                updated = true;
            }
        } catch (RepositoryException e) {
        	e.printStackTrace();
            return false;
        }
        return updated;
    }
    
    public String createID(String tag, String path, String folder, String divider){
    	HashMap<String,String> specialCouncils = new HashMap<String,String>();
    	specialCouncils.put("southern-appalachian","girlscoutcsa");
    	specialCouncils.put("NE_Texas","NE_Texas");
    	specialCouncils.put("nc-coastal-pines-images-","girlscoutsnccp");
    	specialCouncils.put("wcf-images","gswcf");
    	specialCouncils.put("oregon-sw-washington-","girlscoutsosw");
    	specialCouncils.put("dxp","girlscouts-dxp");
    	
    	String councilRoot = "";
    	String tagName = tag.toLowerCase().replaceAll(" ",divider).replaceAll("[^a-z0-9_]",divider).trim();
    	Pattern pDam = Pattern.compile("^/content/dam/([^/]{1,})/*.*$");
    	Matcher mDam = pDam.matcher(path);
    	Pattern pContent = Pattern.compile("^/content/([^/]{1,})/*.*$");
    	Matcher mContent = pContent.matcher(path);
    	if(mDam.matches()){
    		councilRoot = mDam.group(1);
        	if(specialCouncils.containsKey(councilRoot)){
        		councilRoot = specialCouncils.get(councilRoot);
        	}
        	if(councilRoot.startsWith("girlscouts-")){
        		councilRoot = councilRoot.replace("girlscouts-","");
        	}
    		return councilRoot + ":" + folder + "/" + tagName;
    	}else if(mContent.matches()){
    		councilRoot = mContent.group(1);
        	if(specialCouncils.containsKey(councilRoot)){
        		councilRoot = specialCouncils.get(councilRoot);
        	}
    		return councilRoot + ":" + folder + "/" + tagName;
    	}else{
    		return null;
    	}
    }
    
    public void createNewContacts(Node rootNode, HashMap<String,ArrayList<Contact>> contactsToCreate, String councilName, Session session, Replicator replicator) throws Exception{
    	if(contactsToCreate.size() > 0){
    		for(String team : contactsToCreate.keySet()){
    			String teamPath = (team == null || team.equals("")) ? "none" : team;
    			String teamPathName = teamPath.toLowerCase().replaceAll("[^A-Za-z0-9]","-");
    			Node teamNode = null;
    			Node teamContentNode = null;
    			try{
    				teamNode = rootNode.addNode(teamPathName,"cq:Page");
    				teamContentNode = teamNode.addNode("jcr:content","cq:PageContent");
    				teamContentNode.setProperty("jcr:title",team);
    			}catch(ItemExistsException e){
    				try{
    					teamNode = rootNode.getNode(teamPathName);
    					teamContentNode = teamNode.getNode("jcr:content");
    				}catch(Exception e1){
    					e1.printStackTrace();
    					break;
    				}
    			}
    			ArrayList<Contact> contacts = contactsToCreate.get(team);
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
    					replicator.replicate(session, ReplicationActionType.ACTIVATE, contactNode.getPath());
    				}
    			}
    			replicator.replicate(session, ReplicationActionType.ACTIVATE, teamNode.getPath());
    		}
    		replicator.replicate(session, ReplicationActionType.ACTIVATE, rootNode.getPath());
    	}else{
    		throw new Exception("Contact Spreadsheet import - Contact Map is empty");
    	}
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
    
    private class Document{
    	public static final String FILE_NAME_PROP="pdf:Title";
    	public static final String TITLE_PROP="dc:title";
    	public static final String DESCRIPTION_PROP="dc:description";
    	public static final String TAGS_PROP="cq:tags";
    }
}