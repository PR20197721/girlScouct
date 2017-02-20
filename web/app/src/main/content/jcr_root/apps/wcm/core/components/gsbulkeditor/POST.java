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

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.wcm.api.Page;

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

/**
 * Servers as base for image servlets
 */
public class POST extends SlingAllMethodsServlet {
    public static final String DOCUMENT_PARAM = "document";
    public static final String INSERTEDRESOURCETYPE_PARAM = "insertedResourceType";
    public static final String ROOTPATH_PARAM = "./rootPath";
    public static final String IMPORT_TYPE_PARAM="importType";

    public static final String DEFAULT_SEPARATOR = "\t";

    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response)
            throws ServletException, IOException {

        HtmlResponse htmlResponse = null;

        if (request.getRequestParameter(DOCUMENT_PARAM) != null) {
            InputStream in = request.getRequestParameter(DOCUMENT_PARAM).getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));

            String insertedResourceType = request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM)!=null ?
                    request.getRequestParameter(INSERTEDRESOURCETYPE_PARAM).getString() : null;
            String rootPath = request.getRequestParameter(ROOTPATH_PARAM)!=null ?
                    request.getRequestParameter(ROOTPATH_PARAM).getString() : null;


            String importType = request.getRequestParameter(IMPORT_TYPE_PARAM)!=null ? .getString() : null;
            
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
                	Page rootPage = resource.adaptTo(Page.class);
                	if(rootPage.getDepth() >= 2){
                		try{
                			SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
                	        SlingScriptHelper scriptHelper = bindings.getSling();
                	        Packaging packaging = scriptHelper.getService(Packaging.class);
                			//Start by creating a package under the root node, in case we need to roll back
	                		JcrPackageManager jcrPM = packaging.getPackageManager(rootNode.getSession());
	                		String packageName = rootPath.replaceAll("/","-");
	                		GSDateTime gdt = new GSDateTime();
	                		GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyyMMddHHmm");
	                		String dateString = dtfOut.print(gdt);  
	                		if(jcrPM == null){
	                			System.out.println("Null PackageManager");
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
                	
	                    long counter = System.currentTimeMillis();
	
	                    //manage headers
	                    String lineBuffer = bufferReader.readLine();
	                    if (lineBuffer != null) {
	                        List<String> headers = Arrays.asList(lineBuffer.split(DEFAULT_SEPARATOR));
	                        List<String> headersLowerCase = Arrays.asList(lineBuffer.toLowerCase().split(DEFAULT_SEPARATOR));
	                        if (headers.size() > 0) {
	                        	//GS: The importer expects a jcr:path header, but it can use the rootpath from the querybuilder if no path is present
	                            int pathIndex = headers.indexOf(JcrConstants.JCR_PATH);
	                            //GS: Check if the type is "contacts." If so, deactivate and delete everything under the rootpath
	                            if(type.equals("contacts")){
	                            	
	                            }
	                            int lineRead = 0, lineOK = 0;
	                            while((lineBuffer = bufferReader.readLine())!=null) {
	                                lineRead++;
	                                if(performLine(request,lineBuffer,headers,pathIndex,rootNode,insertedResourceType,counter++)) {
	                                    lineOK++;
	                                }
	                            }
	
	                            if(lineOK>0) {
	                                try {
	                                    rootNode.save();
	                                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
	                                        "Imported " + lineOK + "/" + lineRead + " lines");
	                                } catch (RepositoryException e) {
	                                    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
	                                        "Error while saving modifications " + e.getMessage());
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

    public boolean performLine(SlingHttpServletRequest request, String line, List<String> headers, int pathIndex, Node rootNode, String insertedResourceType, long counter) {
        boolean updated = false;
        try {
            int headerSize = headers.size();
            line = line + "\tFINAL";
            List<String> values = new LinkedList<String>(Arrays.asList(line.split(DEFAULT_SEPARATOR)));
            values.remove(values.size() - 1);
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

            if(resource!=null) {
                node = resource.adaptTo(Node.class);
            } else {
                if(rootNode!=null) {
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
            }

           if(node!=null) {
               ValueFactory valueFactory = node.getSession().getValueFactory();
                for(int i=0;i<headerSize;i++) {
                    if(i!=pathIndex) {
                        String property = headers.get(i);
                        property = property.trim();

                        if(!property.equals(JcrConstants.JCR_PATH)
                                && !property.equals(JcrConstants.JCR_PRIMARYTYPE)) {
                            Value value = valueFactory.createValue(values.get(i));
                            Node updatedNode = node;
                            if( property.indexOf('/') != -1) {
                                String childNodeName = property.substring(0,property.lastIndexOf('/'));
                                updatedNode = node.getNode(childNodeName);
                                property = property.substring(property.lastIndexOf('/') + 1);
                            }
                            if(updatedNode != null &&
                                    updatedNode.getPrimaryNodeType().canSetProperty(property,value)) {
                                updatedNode.setProperty(property,value);
                            }
                        }
                    }
                }
                updated = true;
            }
        } catch (RepositoryException e) {
            return false;
        }
        return updated;
    }
}