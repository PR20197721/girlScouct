/*
 * Copyright 1997-2009 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package org.girlscouts.web.servlets;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.google.common.collect.Lists;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.girlscouts.common.events.search.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.activation.FileDataSource;

@Component(metatype = true, label = "Girl Scouts Temporary User Creator",
        description = "Creates a temporary user based on scaffolding input")
@Service(Servlet.class)
@Properties({
        @Property(name = "sling.servlet.paths", value = "/bin/tempUserGen.html"),
        @Property(name = "sling.servlet.methods", value = "POST"),
        @Property(name = "service.description", value = "Girl Scouts Temporary User Account Creation Servlet")
})
public class GSTemporaryUserCreator
        extends SlingAllMethodsServlet
        implements OptingServlet {
	
    @Reference
    private Replicator replicator;

    protected static final String EXTENSION = "html";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void activate(ComponentContext componentContext) {
        Dictionary<String, Object> properties = componentContext.getProperties();
    }

    public boolean accepts(SlingHttpServletRequest request) {
    	if(request.getRequestPathInfo().getResourcePath().equals("/bin/tempUserGen.html")){
    		return true;
    	}else{
    		return false;
    	}
    }

    /**
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response)
            throws ServletException, IOException {

    	String message = "";
    	
        //Double check that the request should be accepted since it is possible to
        //bypass the OptingServlet interface through a properly constructed resource type.
        //eg. sling:resourceType=foundation/components/form/start/mail.POST.servlet
        if (!accepts(request)) {
            logger.debug("Resource not accepted.");
            response.setStatus(500);
	        message = "Error: Resource not accepted. Could not create user";
	        output(message, response);
            return;
        }
        if (ResourceUtil.isNonExistingResource(request.getResource())) {
            logger.debug("Received fake request!");
            response.setStatus(500);
	        message = "Error: Request believed to be fake. Could not create user";
	        output(message, response);
            return;
        }
        
        ResourceResolver resourceResolver = request.getResourceResolver();
        
        String username = "";
        String password = "";
        String expirationDate = "";
        String group = "";
        for(Iterator<String> itr=FormsHelper.getContentRequestParameterNames(request); itr.hasNext();){
    		final String paraName=itr.next();
            RequestParameter[] paras = request.getRequestParameters(paraName);
            for(RequestParameter paraValue : paras){
            	if(paraValue.isFormField()){
            		if(paraName.equals("./username")){
            			username = paraValue.getString();
            		}
            		else if(paraName.equals("./password")){
            			password = paraValue.getString();
            		}
            		else if(paraName.equals("./expiration")){
            			expirationDate = paraValue.getString() + "T23:59";
            		}else if(paraName.equals("./group")){
            			group = paraValue.getString();
            		}
            	}
            }
        }
        if(username.equals("") || password.equals("") || expirationDate.equals("") || group.equals("")){
	        message = "Error: Not all values are filled in.";
	        output(message, response);
        	return;
        }
        
        try{
	        Session session = resourceResolver.adaptTo(Session.class);
	        
	        UserManager userManager = ((JackrabbitSession) session).getUserManager();
	        User tempUser = userManager.createUser(username, password);
	        Group userGroup = (Group)userManager.getAuthorizable(group);
	        userGroup.addMember(tempUser);
	        
	        //If necessary, create the folder where the temp user nodes will be stored
	        Resource etcRes = resourceResolver.resolve("/etc");
	        Node etcNode = etcRes.adaptTo(Node.class);
	        Resource gsUsersRes = resourceResolver.resolve("/etc/gs-temp-users");
	        Node gsUsersNode = null;
	        if(gsUsersRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
				gsUsersNode = etcNode.addNode("gs-temp-users");
	        }else{
		        gsUsersNode = gsUsersRes.adaptTo(Node.class);
	        }
	        
	        replicator.replicate(session, ReplicationActionType.ACTIVATE, tempUser.getPath());
	        replicator.replicate(session, ReplicationActionType.ACTIVATE, userGroup.getPath());
	        
	        //Format date to be more node-friendly
	    	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("MM/dd/yy'T'HH:mm");
	    	GSDateTimeFormatter dtfOut = GSDateTimeFormat.forPattern("yyyy-MM-dd");
	    	GSDateTime expirationDateTime = GSDateTime.parse(expirationDate, dtfIn);
	    	String dateNodeString = dtfOut.print(expirationDateTime);
	    	
	    	GSDateTime today = new GSDateTime();
	    	if(today.isAfter(expirationDateTime)){
		        message = "Error: Expiration Date must be today or after today";
		        output(message, response);
	    		return;
	    	}
	    	
	    	//If necessary, create a node for the expiration date
	    	Resource dateRes = resourceResolver.resolve("/etc/gs-temp-users/" + dateNodeString);
	    	Node dateNode = null;
	    	if(dateRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
	    		dateNode = gsUsersNode.addNode(dateNodeString,"nt:unstructured");
	    	}else{
	    		dateNode = dateRes.adaptTo(Node.class);
	    	}
	    	String [] userProperty;
	    	
	    	if(!dateNode.hasProperty("users")){
	    		userProperty = new String[]{username};
	    	}else{
	    		Value[] propValues = dateNode.getProperty("users").getValues();
	    		userProperty = new String[propValues.length+1];
	    		for(int i=0; i<propValues.length; i++){
	    			userProperty[i] = propValues[i].getString();
	    		}
	    		userProperty[propValues.length] = username;
	    	}
	    	dateNode.setProperty("users", userProperty);
	    	
			session.save();
	        message = "Successfully created temporary user " + username;
	        output(message, response);
			return;
		} catch (AuthorizableExistsException e){
	        message = "Error: User Already Exists";
	        output(message, response);
			e.printStackTrace();
		}catch (Exception e) {
			String errorCode = System.currentTimeMillis() + "";
			System.err.println("Temporary User Creator Error " + errorCode);
	        message = "Error: Something went wrong. Error code: " + errorCode;
	        output(message, response);
			e.printStackTrace();
		}
    }
    
    public void output(String message, SlingHttpServletResponse response){
    	try{
    		PrintWriter pw = response.getWriter();
    		pw.write("<html><head>    <title>User Creation Response</title></head>    <body>    <h1></h1>    <table>        <tbody>            <tr>                <td>Status</td>                <td><div id=\"Status\">200</div></td>            </tr>            <tr>                <td>Message</td>                <td><div id=\"Message\">" + message + "</div></td>            </tr>            <tr>                <td>Location</td>                <td><a href=\"/\" id=\"Location\">/</a></td>            </tr>            <tr>                <td>Parent Location</td>                <td><a href=\"/\" id=\"ParentLocation\"></a></td>            </tr>            <tr>                            </tr>            <tr>                <td>Referer</td>                <td><a href=\"/\" id=\"Referer\"></a></td>            </tr>            <tr>                <td>ChangeLog</td>                <td><div id=\"ChangeLog\"></div></td>            </tr>        </tbody>    </table>    <p><a href=\"/\">Go Back</a></p>    <p><a href=\"/\">Modified Resource</a></p>    <p><a href=\"/\">Parent of Modified Resource</a></p>    </body></html>");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

}
