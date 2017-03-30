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

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.StringBuilder;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.ServletException;
import javax.jcr.ItemNotFoundException;

import org.apache.jackrabbit.commons.query.GQL;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.scripting.core.ScriptHelper;
import org.apache.sling.api.scripting.SlingBindings;

import com.day.cq.commons.TidyJSONWriter;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.ResourceResolver;

import org.girlscouts.web.exception.GirlScoutsException;
import org.girlscouts.web.encryption.FormEncryption;
import org.girlscouts.web.events.search.GSDateTime;
import org.girlscouts.web.events.search.GSDateTimeFormat;
import org.girlscouts.web.events.search.GSDateTimeFormatter;


/**
 * Servers as base for image servlets
 */
public class json extends SlingAllMethodsServlet {
	/**
	 * Query clause
	 */
	public static final String QUERY_PARAM = "query";
	public static final String DEEP_SEARCH_PARAM = "isDeep";
	public static final String RESOURCE_TYPE_PARAM = "resourceType";
	public static final String PRIMARY_TYPE_PARAM = "primaryType";
	public static final String IMPORT_TYPE_PARAM = "importType";
	public static final String YEAR_PARAM = "year";

	/**
	 * Common path prefix
	 */
	public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

	/**
	 * Common path prefix
	 */
	public static final String PROPERTIES_PARAM = "cols";

	public static final String TIDY_PARAM = "tidy";

	/**
	 * Property name replacements
	 */
	private static Map<String, String> PROPERTY_NAME_REPLACEMENTS = new HashMap<String, String>();
	static {
		PROPERTY_NAME_REPLACEMENTS.put("\\.", "_DOT_");
	}
	final static char NAME_VALUE_SEPARATOR=30;
	final static char VALUE_SEPARATOR=31;
	final static char PARA_SEPARATOR=29;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		if ("json".equals(request.getRequestPathInfo().getExtension())) {
			StringWriter buf = new StringWriter();

			String queryString = request.getParameter(QUERY_PARAM);
			String isDeepString = request.getParameter(DEEP_SEARCH_PARAM);
			String importType = request.getParameter(IMPORT_TYPE_PARAM);
			//Girl Scouts Custom Options
			Boolean isDeep = "true".equals(isDeepString);
			String resourceType = request.getParameter(RESOURCE_TYPE_PARAM);
			String primaryType = request.getParameter(PRIMARY_TYPE_PARAM);
			
			String commonPathPrefix = request.getParameter(COMMON_PATH_PREFIX_PARAM);

			Session session = request.getResourceResolver().adaptTo(
					Session.class);
			
			try {
				RowIterator hits;

                // Girl Scouts customization
                // Discarding the original implementation of using a query
                //if (commonPathPrefix != null && queryString != null) {
                    //hits = GQL.execute(queryString, session, commonPathPrefix);
                //} else if (queryString != null) {
                    //hits = GQL.execute(queryString, session);
                //} else {
                    //return;
                //}

                String path = queryString.split(":")[1];

                long nbrOfResults = 0;//hits.getSize();

				TidyJSONWriter writer = new TidyJSONWriter(buf);
				writer.setTidy("true".equals(request.getParameter(TIDY_PARAM)));
				writer.object();
				writer.key("hits");
				writer.array();

				String tmp = request.getParameter(PROPERTIES_PARAM);
				String[] properties = (tmp != null) ? tmp.split(",") : null;
				
				String year = request.getParameter(YEAR_PARAM);
				if(null != importType){
					if(importType.equals("events")){
						if(null != year){
							path = path + "/" + year;
						}
					}
				}
                iterateNodes(path, nbrOfResults, writer, properties, session, request, isDeep, resourceType, primaryType, importType);

				writer.endArray();
				writer.key("results").value(nbrOfResults);
				writer.endObject();
			} catch (Exception e) {
				throw new ServletException(e);
			}

			// send string buffer
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(buf.getBuffer().toString());
		}
	}

	public static String encodeString(String s) {
		for (String key : PROPERTY_NAME_REPLACEMENTS.keySet()) {
			s = s.replaceAll(key, PROPERTY_NAME_REPLACEMENTS.get(key));
		}
		return s;
	}
	//return decrypted (name,value) pair of a node 
	public static Map<String, String[]> getNodeSecret(Node node,SlingHttpServletRequest request) throws ItemNotFoundException{
		try{
			String secret = node.getProperty("secret").getString();

            SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
            ScriptHelper scriptHelper = (ScriptHelper)bindings.getSling();
            FormEncryption fEn = scriptHelper.getService(FormEncryption.class);

            String decrypted = fEn.decrypt(secret);
			String[] propStrings = decrypted.split(String.valueOf(PARA_SEPARATOR));
			Map<String, String[]> propsMap = new HashMap<String, String[]>();
			for(String propString:propStrings){
				String[] tmpStrings=propString.split(String.valueOf(NAME_VALUE_SEPARATOR));
				String propName = tmpStrings[0];
				String[] values = tmpStrings[1].split(String.valueOf(VALUE_SEPARATOR));
				propsMap.put(propName, values);
			}
			return propsMap;

		}catch(Exception e){
			throw new ItemNotFoundException("The node is encrypted, but no secret found");
		}

	}

	public void writeProperty(TidyJSONWriter writer, String name, String[] values)throws Exception{
		writer.key(encodeString(name));
		if(values.length==0){
			writer.value("");
		}
		else if (values.length==1) {
			writer.value(values[0]);

		} else {
			writer.array();
			for (String v : values) {
				writer.value(v);
			}
			writer.endArray();
		}

	}
	public void writeProperty(TidyJSONWriter writer, String name, Property prop, ResourceResolver rr)throws Exception{
		writer.key(encodeString(name));
		if(name.endsWith("cq:tags")){
			TagManager tm = rr.adaptTo(TagManager.class);
			if (prop.getDefinition().isMultiple()) {
				StringBuilder tagStrBuilder = new StringBuilder();
				if(prop.getValues().length > 0){
					for (int i = 0; i < prop.getValues().length; i++){
						Value v = prop.getValues()[i];
						Tag t = tm.resolve(v.getString());
						if(t != null){
							tagStrBuilder.append(t.getTitle());
						}else{
							tagStrBuilder.append(v.getString());
						}
						if(i < prop.getValues().length - 1){
							tagStrBuilder.append(";");
						}
					}
					writer.value(tagStrBuilder.toString());
				}else{
					writer.value("");
				}
			} else {
				Tag t = tm.resolve(prop.getString());
				if(t != null){
					writer.value(t.getTitle());
				}else{
					writer.value(prop.getString());
				}
			}
		}else{
			if (prop.getType() != PropertyType.BINARY) {
				if (prop.getDefinition().isMultiple()) {
					writer.array();
					for (Value v : prop.getValues()) {
						writer.value(v.getString());
					}
					writer.endArray();
				} else {
					writer.value(prop.getString());
				}
			} else {
				writer.value("BINARY");
			}
		}
	}


	public void iterateNodes(String path, long nbrOfResults, TidyJSONWriter writer, String[] properties, Session session, SlingHttpServletRequest request, Boolean isDeep, String resourceType, String primaryType, String importType)
	throws Exception{
		NodeIterator iter = session.getNode(path).getNodes();
		while (iter.hasNext()) {
		    // Girl Scouts customization
		    // Use NodeIterator instead. Only handles direct children.
		    // This reduces deeper query and gets rid of the memory issue of queries.
		    //while (hits.hasNext()) {
	        //Row hit = hits.nextRow();
	        //Node node = (Node) session.getItem(hit.getValue(JcrConstants.JCR_PATH).getString());
	        nbrOfResults++;
	        Node node = iter.nextNode();
			if (node != null && (!isDeep || (isDeep && !node.getPath().endsWith("jcr:content")))) {
				Boolean canIterate = false;
	            if(null != resourceType && null != primaryType){
	            	if(node.hasProperty("sling:resourceType") && node.hasProperty("jcr:primaryType")){ 
	            		if(!node.getProperty("sling:resourceType").getString().equals(resourceType) && !node.getProperty("jcr:primaryType").getString().equals(primaryType)) {
	            			canIterate = true;
		            	}
	            	}else if(node.hasProperty("jcr:content/sling:resourceType") && node.hasProperty("jcr:content/jcr:primaryType")){
		            	if(!node.getProperty("jcr:content/sling:resourceType").getString().equals(resourceType) && !node.getProperty("jcr:content/jcr:primaryType").getString().equals(primaryType)) {
		            		canIterate = true;
		            	}
	            	}else{
	    				canIterate = true;
	            	}
	            }else if(null != resourceType){
	            	if(node.hasProperty("sling:resourceType")){
	            		if(!node.getProperty("sling:resourceType").getString().equals(resourceType)){
	            			canIterate = true;
	            		}
	            	}else if(node.hasProperty("jcr:content/sling:resourceType")){
	            		if(!node.getProperty("jcr:content/sling:resourceType").getString().equals(resourceType)){
	            			canIterate = true;
	            		}
	            	}else{
	            		canIterate = true;
	            	}
	            }else if(null != primaryType){
	            	if(node.hasProperty("jcr:primaryType")){
	            		if(!node.getProperty("jcr:primaryType").getString().equals(primaryType)){
	            			canIterate = true;
	            		}
	            	}else if(node.hasProperty("jcr:content/jcr:primaryType")){
	            		if(!node.getProperty("jcr:content/jcr:primaryType").getString().equals(primaryType)){
	            			canIterate = true;
	            		}
	            	}else{
	            		canIterate = true;
	            	}
	            }   
	            if(canIterate){
    				if(node.hasNodes() && isDeep){
    					iterateNodes(node.getPath(), nbrOfResults, writer, properties, session, request, isDeep, resourceType, primaryType, importType);
    				}
    				continue;
	            }
	            
	            if(null != importType){
		            if(importType.equals("documents")){
		            	if(!node.hasProperty("jcr:content/metadata/dc:format")){
		            		continue;
		            	}else{
		            		String format = node.getProperty("jcr:content/metadata/dc:format").getString();
		            		if(!format.equals("application/pdf") 
		            				&& !format.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
		            				&& !format.equals("application/zip")
		            				&& !format.equals("application/vnd.ms-powerpoint")
		            				&& !format.equals("application/vnd.ms-excel")
		            				&& !format.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
		            				&& !format.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
		            				&& !format.equals("application/msword")
		            				&& !format.equals("application/vnd.ms-word.document.macroenabled.12")
		            				&& !format.equals("text/csv")
		            				&& !format.equals("application/vnd.ms-excel.sheet.macroenabled.12")
		            				&& !format.equals("text/plain")){
		            			continue;
		            		}
		            	}
		            }
	            }
				writer.object();
	            //writer.key(JcrConstants.JCR_PATH).value(hit.getValue(JcrConstants.JCR_PATH).getString());
	            writer.key(JcrConstants.JCR_PATH).value(node.getPath());
				if(node.hasProperty("isEncrypted") && node.getProperty("isEncrypted").getString().equals("true")){
					Map<String, String[]> decryptedMap=getNodeSecret(node,request);
					if (properties != null) {
						for (String property : properties) {
							if(decryptedMap.containsKey(property)){
								writeProperty(writer,property,decryptedMap.get(property));
							}else if (node.hasProperty(property)) {
								writeProperty(writer,property,node.getProperty(property),request.getResourceResolver());
							}
						}
					}
				}else{//open form
					if (properties != null) {
						for (String property : properties) {
							if(null != importType){
								if(importType.equals("events")){
									if(property.equals("jcr:content/data/start-date") || property.equals("jcr:content/data/start-time") || property.equals("jcr:content/data/end-date") || property.equals("jcr:content/data/end-time") || property.equals("jcr:content/data/regOpen-date") || property.equals("jcr:content/data/regOpen-time") || property.equals("jcr:content/data/regClose-date") || property.equals("jcr:content/data/regClose-time")){
										if (node.hasProperty(property.substring(0,property.lastIndexOf("-")))){
											String datetimeString = node.getProperty(property.substring(0,property.lastIndexOf("-"))).getString();
											GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
											GSDateTime dt = GSDateTime.parse(datetimeString,dtfIn);
											GSDateTimeFormatter dtfOutDate = GSDateTimeFormat.forPattern("MM/dd/yyyy");
											GSDateTimeFormatter dtfOutTime = GSDateTimeFormat.forPattern("hh:mm:ss a");
											String [] dateTime = new String[1];
											if(property.equals("jcr:content/data/start-date") || property.equals("jcr:content/data/end-date") || property.equals("jcr:content/data/regOpen-date") || property.equals("jcr:content/data/regClose-date")){
												dateTime[0] = dtfOutDate.print(dt);
												writeProperty(writer,property,dateTime);
											}else if(property.equals("jcr:content/data/start-time") || property.equals("jcr:content/data/end-time") || property.equals("jcr:content/data/regOpen-time") || property.equals("jcr:content/data/regClose-time")){
												dateTime[0] = dtfOutTime.print(dt);
												writeProperty(writer,property,dateTime);
											}
										}
									}else if(property.equals("jcr:content/cq:tags-progLevel") || property.equals("jcr:content/cq:tags-categories")){
										if (node.hasProperty(property.substring(0,property.lastIndexOf("-")))){
											String tagFolder = property.substring(property.lastIndexOf("-")+1,property.length());
											if(tagFolder.equals("progLevel")){
												tagFolder = "program-level";
											}
											String[] tagsToWrite = new String[1];
											String tagString = "";
											try{
												Property prop = node.getProperty(property.substring(0,property.lastIndexOf("-")));
                                				TagManager tm = request.getResourceResolver().adaptTo(TagManager.class);
                                				if (prop.getDefinition().isMultiple()) {
                                					Value[] values = prop.getValues();
                                					for(Value value : values){
                                						Tag t = tm.resolve(value.toString());
                                						if(null != t){
                                							if(t.getTagID().matches("^.*:" + tagFolder + "/.*$")){
                                        						if(tagString.length() > 0){
                                        							tagString = tagString + ";";
                                        						}
                                								tagString = tagString + t.getTitle();
                                							}
                                						}
                                					}
                                					tagsToWrite[0] = tagString;
                                				}else{
                                					Value value = prop.getValue();
                            						Tag t = tm.resolve(value.toString());
                            						if(null != t){
                            							if(t.getTagID().matches("^.*:" + tagFolder + "/.*$")){
                            								tagsToWrite[0] = t.getTitle();
                            							}
                            						}
                                				}
                                				writeProperty(writer,property,tagsToWrite);
											}catch(Exception e){
												e.printStackTrace();
											}
										}
									}else if (node.hasProperty(property)) {
										writeProperty(writer,property,node.getProperty(property),request.getResourceResolver());
									}
								}else if (node.hasProperty(property)) {
									writeProperty(writer,property,node.getProperty(property),request.getResourceResolver());
								}
							}
							else if (node.hasProperty(property)) {
								writeProperty(writer,property,node.getProperty(property),request.getResourceResolver());
							}
						}
					}
				}
				//Allows optional deep searches
				writer.endObject();
				if(node.hasNodes() && isDeep){
					iterateNodes(node.getPath(), nbrOfResults, writer, properties, session, request, isDeep, resourceType, primaryType, importType);
				}
			}
		}
	}
}
