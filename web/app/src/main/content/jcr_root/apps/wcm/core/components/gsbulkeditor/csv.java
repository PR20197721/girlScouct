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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.ServletException;

import org.apache.jackrabbit.commons.query.GQL;
import org.apache.jackrabbit.value.ValueHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.commons.lang.StringUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.text.Text;
import com.day.text.XMLChar;

import apps.wcm.core.components.gsbulkeditor.json;

import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Servers as base for image servlets
 */
public class csv extends SlingAllMethodsServlet {
    /**
     * Query clause
     */
    public static final String QUERY_PARAM = "query";
    public static final String DEEP_SEARCH_PARAM = "isDeep";
    public static final String RESOURCE_TYPE_PARAM = "resourceType";
    public static final String PRIMARY_TYPE_PARAM = "primaryType";
    public static final String IMPORT_TYPE_PARAM = "importType";

    public static final String SEPARATOR_PARAM = "separator";

    /**
     * Common path prefix
     */
    public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

    /**
     * Common path prefix
     */
    public static final String PROPERTIES_PARAM = "cols";

    public static final String DEFAULT_SEPARATOR = ",";

    public static final String VALUE_DELIMITER = "\"";

    public static final String CHARACTER_ENCODING = "windows-1252";

    /**
     * Encoding param
     */
    public static final String ENCODING_PARAM = "charset";

    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws ServletException, IOException {

        StringWriter buf = new StringWriter();
        BufferedWriter bw = new BufferedWriter(buf);

        String queryString = request.getParameter(QUERY_PARAM);
        
		String isDeepString = request.getParameter(DEEP_SEARCH_PARAM);
		String resourceTypeString = request.getParameter(RESOURCE_TYPE_PARAM);
		String primaryTypeString = request.getParameter(PRIMARY_TYPE_PARAM);
		String importType = request.getParameter(IMPORT_TYPE_PARAM);
		
		Boolean isDeep = "true".equals(isDeepString);
		
        String commonPathPrefix = request.getParameter(COMMON_PATH_PREFIX_PARAM);

        ResourceResolver rr = request.getResourceResolver();
        Session session = rr.adaptTo(
                Session.class);
        try {
            // Girl Scouts customization
            // Discarding the original implementation of using a query
            //RowIterator hits;
            //if (commonPathPrefix != null && queryString != null) {
            //    hits = GQL.execute(queryString, session, commonPathPrefix);
            //} else if (queryString != null) {
            //    hits = GQL.execute(queryString, session);
            //} else {
            //    return;
            //}

            String tmp = request.getParameter(PROPERTIES_PARAM);
            String[] properties = (tmp != null) ? tmp.split(",") : null;

            //final String separator = (request.getParameter(SEPARATOR_PARAM)!=null ? request.getParameter(SEPARATOR_PARAM) : DEFAULT_SEPARATOR);
            final String separator = DEFAULT_SEPARATOR;

            Boolean includePath = true;
            if(importType != null){
            	if(!importType.equals("contacts")){
            		bw.write(csv.valueParser(JcrConstants.JCR_PATH, separator) + separator);
            	}else{
            		includePath = false;
            	}
            }else{
            	bw.write(csv.valueParser(JcrConstants.JCR_PATH, separator) + separator);
            }
            if (properties != null) {
                for (String property : properties) {
                    property = property.trim();
                    bw.write(csv.valueParser(property, separator) + separator);
                }
            }

            bw.newLine();

            String path = queryString.split(":")[1];
            
            iterateNodes(path, separator, bw, properties, session, request, isDeep, resourceTypeString, primaryTypeString, includePath, importType, rr);

        } catch (Exception e) {
            throw new ServletException(e);
        }

        // send string buffer
        bw.flush();
        response.setContentType("text/csv");
        String encoding = request.getParameter(ENCODING_PARAM);
        response.setCharacterEncoding(StringUtils.isNotBlank(encoding) ? encoding : CHARACTER_ENCODING);
        response.getWriter().print(buf.getBuffer().toString());
    }

    public static String valueParser(String value, String separator) {
        if (value != null) {
            if (value.indexOf(separator) != -1 || value.indexOf('\n') != -1) {
                value = VALUE_DELIMITER + value + VALUE_DELIMITER;
            }
        }
        return value;
    }

    /**
     * Formats the given jcr property to the enhanced docview syntax.
     *
     * @param prop the jcr property
     * @return the formatted string
     * @throws RepositoryException if a repository error occurs
     */
    public static String format(Property prop, ResourceResolver rr) throws RepositoryException {
        StringBuffer attrValue = new StringBuffer();
        int type = prop.getType();
        if (type == PropertyType.BINARY || isAmbiguous(prop)) {
            attrValue.append("{");
            attrValue.append(PropertyType.nameFromValue(prop.getType()));
            attrValue.append("}");
        }
        // only write values for non binaries
        if (prop.getType() != PropertyType.BINARY) {
        	if(prop.getName().endsWith("cq:tags")){
                if (prop.getDefinition().isMultiple()) {
                    Value[] values = prop.getValues();
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) {
                            attrValue.append(';');
                        }
                        TagManager tm = rr.adaptTo(TagManager.class);
                        Tag t = tm.resolve(values[i].getString());
                        String strValue = ValueHelper.serialize(values[i], false);
                        if(t != null){
                        	strValue = t.getTitle();
                        }
                        switch (prop.getType()) {
                            case PropertyType.STRING:
                            case PropertyType.NAME:
                            case PropertyType.PATH:
                                escape(attrValue, strValue, true);
                                break;
                            default:
                                attrValue.append(strValue);
                        }
                    }
                } else {
                    String strValue = ValueHelper.serialize(prop.getValue(), false);
                    TagManager tm = rr.adaptTo(TagManager.class);
                    Tag t = tm.resolve(prop.getValue().getString());
                    if(t != null){
                    	strValue = t.getTitle();
                    }
                    escape(attrValue, strValue, false);
                }
        	}
        	else if (prop.getDefinition().isMultiple()) {
                attrValue.append('[');
                Value[] values = prop.getValues();
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        attrValue.append(',');
                    }
                    String strValue = ValueHelper.serialize(values[i], false);
                    switch (prop.getType()) {
                        case PropertyType.STRING:
                        case PropertyType.NAME:
                        case PropertyType.PATH:
                            escape(attrValue, strValue, true);
                            break;
                        default:
                            attrValue.append(strValue);
                    }
                }
                attrValue.append(']');
            } else {
                String strValue = ValueHelper.serialize(prop.getValue(), false);
                escape(attrValue, strValue, false);
            }
            String strValue = attrValue.toString();
            if (strValue.contains(",")) {
                strValue = strValue.replace("\"", "\"\"");
                strValue = "\"" + strValue + "\"";
            }
            if (strValue.isEmpty()) {
                strValue = "\"\"";
            }
            return strValue;
        }
        return attrValue.toString();
    }

	public static String format(String[] values) throws RepositoryException {
		StringBuffer attrValue = new StringBuffer();
		if (values.length>1) {
			attrValue.append('[');
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					attrValue.append(',');
				}
				attrValue.append(values[i]);
			}
			attrValue.append(']');
		} else {
			String strValue = values.length==0?"":values[0];
			escape(attrValue, strValue, false);
		}
		String strValue = attrValue.toString();
		if (strValue.contains(",")) {
			strValue = strValue.replace("\"", "\"\"");
			strValue = "\"" + strValue + "\"";
		}
		if (strValue.isEmpty()) {
			strValue = "\"\"";
		}
		return strValue;

	}
    /**
     * Escapes the value
     *
     * @param buf     buffer to append to
     * @param value   value to escape
     * @param isMulti indicates multi value property
     */
    protected static void escape(StringBuffer buf, String value, boolean isMulti) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                buf.append("\\\\");
            } else if (c == ',' && isMulti) {
                buf.append("\\,");
            } else if (i == 0 && !isMulti && (c == '[' || c == '{')) {
                buf.append('\\').append(c);
            } else if (XMLChar.isInvalid(c)) {
                buf.append("\\u");
                buf.append(Text.hexTable[(c >> 12) & 15]);
                buf.append(Text.hexTable[(c >> 8) & 15]);
                buf.append(Text.hexTable[(c >> 4) & 15]);
                buf.append(Text.hexTable[c & 15]);
            } else {
                buf.append(c);
            }
        }
    }

    /**
     * Checks if the type of the given property is ambiguous in respect to it's
     * property definition. the current implementation just checks some well
     * known properties.
     *
     * @param prop the property
     * @return type
     * @throws RepositoryException if a repository error occurs
     */
    public static boolean isAmbiguous(Property prop) throws RepositoryException {
        return prop.getType() != PropertyType.STRING && !UNAMBIGOUS.contains(prop.getName());
    }

    /**
     * set of unambigous property names
     */
    private static final Set<String> UNAMBIGOUS = new HashSet<String>();

    static {
        UNAMBIGOUS.add("jcr:primaryType");
        UNAMBIGOUS.add("jcr:mixinTypes");
        UNAMBIGOUS.add("jcr:lastModified");
        UNAMBIGOUS.add("jcr:created");
    }
    
	public void iterateNodes(String path, String separator, BufferedWriter bw, String[] properties, Session session, SlingHttpServletRequest request, Boolean isDeep, String resourceType, String primaryType, Boolean includePath, String importType, ResourceResolver rr)
	throws Exception{
		NodeIterator iter = session.getNode(path).getNodes();
        //while (hits.hasNext()) {
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            //Row hit = hits.nextRow();
            //Node node = (Node) session.getItem(hit.getValue(JcrConstants.JCR_PATH).getString());
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
	            	}
	            }   
	            if(canIterate){
    				if(node.hasNodes() && isDeep){
    					iterateNodes(node.getPath(), separator, bw, properties, session, request, isDeep, resourceType, primaryType, includePath, importType, rr);
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
	            
	            if(includePath){
	            	bw.write(csv.valueParser(node.getPath(), separator) + separator);
	            }
				if(node.hasProperty("isEncrypted") && node.getProperty("isEncrypted").getString().equals("true")){
					Map<String, String[]> decryptedMap=json.getNodeSecret(node,request);
					if (properties != null) {
						for (String property : properties) {
							property = property.trim();
							if(decryptedMap.containsKey(property)){
								String[] values = decryptedMap.get(property);
								bw.write(csv.format(values));
							}else if (node.hasProperty(property)) {
								Property prop = node.getProperty(property);
								bw.write(csv.format(prop, rr));
							}
							bw.write(separator);
						}
					}
				}else{
					if (properties != null) {
						for (String property : properties) {
							property = property.trim();
							if (node.hasProperty(property)) {
								Property prop = node.getProperty(property);
								bw.write(csv.format(prop, rr));
							}
							bw.write(separator);
						}
					}
				}
                bw.newLine();
				if(node.hasNodes() && isDeep){
					iterateNodes(node.getPath(), separator, bw, properties, session, request, isDeep, resourceType, primaryType, includePath, importType, rr);
				}
            }
        }
	}


}