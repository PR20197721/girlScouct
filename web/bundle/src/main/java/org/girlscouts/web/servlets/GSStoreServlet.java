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

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.mailer.MailService;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.web.service.recaptcha.RecaptchaService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This mail servlet accepts POSTs to a form begin paragraph
 * but only if the selector "mail" and the extension "html" is used.
 */
@Component(metatype = true, label = "Adobe CQ Form Mail Servlet",
        description = "Accepts posting to a form start component and performs validations")
@Service(Servlet.class)
@Properties({
        @Property(name = "sling.servlet.resourceTypes", value = "foundation/components/form/start"),
        @Property(name = "sling.servlet.methods", value = "POST"),
        @Property(name = "sling.servlet.selectors", value = "gsstore"),
        @Property(name = "service.description", value = "Girl Scouts Form Store Servlet")
})
public class GSStoreServlet
        extends SlingAllMethodsServlet
        implements OptingServlet {

    protected static final String EXTENSION = "html";
    protected static final String RESPONSE_VAL = "g-recaptcha-response";


    protected static final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
    protected static final String DISABLE_CONFIRMATION_PROPERTY = "disableConfirmation";
    protected static final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
    protected static final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";
    protected static final String TEMPLATE_PATH_PROPERTY = "templatePath";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    protected volatile MailService mailService;

    @Reference
	private ResourceResolverFactory resolverFactory;
    
    @Reference(policy=ReferencePolicy.STATIC)
    private SlingSettingsService slingSettings;
    
    @Reference
    private RecaptchaService recaptchaService;
    
    @Property(value = {
            "/content",
            "/home"
    },
            label = "Resource Whitelist",
            description = "List of paths under which servlet will only accept requests.")
    private static final String PROPERTY_RESOURCE_WHITELIST = "resource.whitelist";
    private String[] resourceWhitelist;
    
    private static Logger log = LoggerFactory.getLogger(GSStoreServlet.class);

    @Property(value = {
            "/content/usergenerated"
    },
            label = "Resource Blacklist",
            description = "List of paths under which servlet will reject requests.")
    private static final String PROPERTY_RESOURCE_BLACKLIST = "resource.blacklist";
    private String[] resourceBlacklist;
    private Map<String, Object> serviceParams;

    protected void activate(ComponentContext componentContext) {
        Dictionary<String, Object> properties = componentContext.getProperties();
        resourceWhitelist = OsgiUtil.toStringArray(properties.get(PROPERTY_RESOURCE_WHITELIST));
        resourceBlacklist = OsgiUtil.toStringArray(properties.get(PROPERTY_RESOURCE_BLACKLIST));
    }

    /**
     * The mail servlet will attempt to check the resource's path against a blacklist,
     * then check to see if the resource's path exists in the white list.  If blacklisted return false,
     * if whitelisted return true, otherwise the path could not be resolved therefore return false.
     *
     * @return true if the request can be processed and false if the request is should be rejected.
     */
    public boolean accepts(SlingHttpServletRequest request) {
        boolean acceptable = EXTENSION.equals(request.getRequestPathInfo().getExtension());

        if (!acceptable) {
            return acceptable;
        }

        final Resource resource = request.getResource();
        logger.debug("checking for acceptance of resource {} ", resource.getPath());

        // Ensure the path is not on the blacklist to be able to continue
        for(String path : resourceBlacklist) {
            if (resource.getPath().startsWith(path)) {
                return false;
            }
        }

        // now check to see if the path might be on the whitelist
        for(String path : resourceWhitelist) {
            if (resource.getPath().startsWith(path)) {
                return true;
            }
        }

        // if not on the whitelist then we reject
        return false;
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

        final MailService localService = this.mailService;

        //Double check that the request should be accepted since it is possible to
        //bypass the OptingServlet interface through a properly constructed resource type.
        //eg. sling:resourceType=foundation/components/form/start/mail.POST.servlet
        if (!accepts(request)) {
            logger.debug("Resource not accepted.");
            response.setStatus(500);
            return;
        }
        if (ResourceUtil.isNonExistingResource(request.getResource())) {
            logger.debug("Received fake request!");
            response.setStatus(500);
            return;
        }
        
        String responseVal = request.getParameter(RESPONSE_VAL);
        if (null != responseVal) {
	        boolean success = recaptchaService.captchaSuccess(responseVal);
	        if (!success) {
	        	logger.debug("Recaptcha validation failed");
	        	response.setStatus(500);
	        	return;
	        }
        } else {
        	logger.debug("Recaptcha response invalid");
            response.setStatus(500);
            return;
        }

        final ResourceBundle resBundle = request.getResourceBundle(null);
        final ValueMap values = ResourceUtil.getValueMap(request.getResource());
        int status = 200;
        try {
            final StringBuilder builder = new StringBuilder();
            builder.append(request.getScheme());
            builder.append("://");
            builder.append(request.getServerName());
            if ((request.getScheme().equals("https") && request.getServerPort() != 443)
                    || (request.getScheme().equals("http") && request.getServerPort() != 80)) {
                builder.append(':');
                builder.append(request.getServerPort());
            }
            builder.append(request.getRequestURI());
            
            serviceParams = new HashMap<String, Object>();
			serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
			log.error("####################Before aqcuiring resource resolver");
			ResourceResolver rr = resolverFactory.getServiceResourceResolver(serviceParams);
			
            
            String contentPath = (String)request.getAttribute("contentPath");
            log.error("####################Content path is: " + contentPath);
            if(contentPath != null && !contentPath.isEmpty()) {
            		Node contentBaseNode = null;
            		Resource contentResource = rr.getResource(contentPath);
            		if(contentResource == null) {
            			contentBaseNode = getFormStorageNode(rr.getResource("/content/usergenerated").adaptTo(Node.class), contentPath);
            		} else {
            			contentBaseNode = contentResource.adaptTo(Node.class);
            		}
            		Date now = new Date();
            		Random rand = new Random();
            		String nodeId = now.getTime() + "_" + rand.nextInt(50);
            		Node submissionNode = contentBaseNode.addNode(nodeId, "sling:Folder");
            		
            		Enumeration<String> names = request.getParameterNames();
            		Map<String, List<String>> paramMap = new HashMap<>();
            		while(names.hasMoreElements()) {
            			String n = names.nextElement();
            			log.error("################PARAM NAME IS: " + n);
            			
            			if(!n.contains(":") && !n.equals("_charset_")) {
							for(RequestParameter param : Arrays.asList(request.getRequestParameters(n))) {
								if (param.getContentType() == null) {
									List<String> valLst = new ArrayList<>(1);
									valLst.add(param.getString());
									paramMap.merge(n, valLst, (l1, l2) -> {
										l1.addAll(l2);
										return l1;
									});
								} else {
									if (param.getSize() > 0) {
										String val = param.getFileName();
										submissionNode.setProperty(n, val);
									}
								}
							}
            			}
            			
            		}

    				for(Entry<String, List<String>> entry : paramMap.entrySet()) {
    					if(entry.getValue().size() > 1) {
							Object[] objVals = entry.getValue().toArray();
							String[] strVals = new String[objVals.length];
							for(int i = 0; i < objVals.length; i++){
								strVals[i] = Optional.ofNullable(objVals[i]).map(Object::toString).orElse(null);
							}
							submissionNode.setProperty(entry.getKey(), strVals);
    					}else {
    						submissionNode.setProperty(entry.getKey(), entry.getValue().get(0));
    					}
    				}
    				
            		
            		log.error("Submission Node path is: " + submissionNode.getPath());
            		submissionNode.save();
            		Set<String> runmodes = slingSettings.getRunModes();
            		boolean isPublish = false;
            		for(String mode : runmodes) {
            			if(mode.equalsIgnoreCase("publish")) {
            				isPublish = true;
            			}
            		}
            		if(isPublish) {
                        try {
                            WorkflowSession wfSession = rr.adaptTo(WorkflowSession.class);
                            WorkflowModel wfModel = wfSession.getModel("/etc/workflow/models/reverse_replication/jcr:content/model");
                            WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", submissionNode.getPath());
                            wfSession.startWorkflow(wfModel, wfData);
                        } catch (Exception e){
                            logger.error("Failed to activate Reverse Replication Workflow", e);
                        }
            		}
            }
            	
           

            // let's get all parameters first and sort them alphabetically!
            final List<String> contentNamesList = new ArrayList<String>();
            final Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                if(!name.equals("_charset_") && !name.contains(":")) {
                		contentNamesList.add(name);
                }
                
            }
            Collections.sort(contentNamesList);
            
            List<String> confirmationEmailAddresses = new ArrayList<String>();
            final List<String> namesList = new ArrayList<String>();
            
            namesList.addAll(contentNamesList);
            
            
           final Iterator<Resource> fields = FormsHelper.getFormElements(request.getResource());
           while (fields.hasNext()) {
                final Resource field = fields.next();
                final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, field);
                for (final FieldDescription desc : descs) {
                    ValueMap childProperties = ResourceUtil.getValueMap(field);
	                	if(childProperties.get("confirmationemail",false)){
	                		final String[] pValues = request.getParameterValues(desc.getName());
	                        for (final String v : pValues) {
	                        	confirmationEmailAddresses.add(v);
	                        }
	            		}
                }
            }
  
                // now add form fields to message
            // and uploads as attachments
            Map<String, List<String>> formFields = new HashMap<String,List<String>>();
            final List<RequestParameter> attachments = new ArrayList<RequestParameter>();
            for (final String name : namesList) {
                final RequestParameter rp = request.getRequestParameter(name);
                if (rp == null) {
                    //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                    logger.debug("skipping form element {} from mail content because it's not in the request", name);
                } else if (rp.isFormField()) {
                		
                    final String[] pValues = request.getParameterValues(name);
                    for (final String v : pValues) {
                    	if(null == formFields.get(name)){
                    		List<String> formField = new ArrayList<String>();
                    		formField.add(v);
                    		formFields.put(name, formField);
                    	}else{
                    		formFields.get(name).add(v);
                    	}
                    }
                } else if(rp.getSize() > 0) {
                		log.error("Attachments: " + rp.getFileName());
                		attachments.add(rp);
                } else {
                    //ignore
                }
            }
            
            //confirmation emails
            boolean disableConfirmations = values.get("disableConfirmationEmails", false);
            //Ensure that override is off
            if(!disableConfirmations){
            	final HtmlEmail confEmail;
            	confEmail = new HtmlEmail();
                confEmail.setCharset("utf-8");
                String confBody = getTemplate(request, values, formFields, confEmail, rr, attachments);
                log.error("###########CONF BODY" + confBody);
                if(!("").equals(confBody)){
                    confEmail.setHtmlMsg(confBody);
                    // mailto
                    for(String confEmailAddress : confirmationEmailAddresses){
                        confEmail.addTo(confEmailAddress);
                    }
                    final String[] confMailTo = values.get(CONFIRM_MAILTO_PROPERTY, String[].class);
                    if(confMailTo != null) {
                        for (final String rec : confMailTo) {
                            confEmail.addBcc(rec);
                        }
                    }

                    // subject and from address
                    final String confSubject = values.get(CONFIRMATION_SUBJECT_PROPERTY, resBundle.getString("Form Submission Received"));
                    confEmail.setSubject(confSubject);
                    final String confFromAddress = values.get(CONFIRMATION_FROM_PROPERTY, "");
                    if (confFromAddress.length() > 0) {
                        //confEmail.setFrom(confFromAddress);
                        confEmail.addReplyTo(confFromAddress);
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
                                new Object[]{confFromAddress, confirmationEmailAddresses, confSubject, confBody});
                    }
                    if(!attachments.isEmpty()) {
                    		for(RequestParameter rp : attachments) {
                    			final ByteArrayDataSource ea = new ByteArrayDataSource(rp.getInputStream(), rp.getContentType());
                    			confEmail.attach(ea, rp.getFileName(), rp.getFileName());
                    		}
                    }
                    localService.sendEmail(confEmail);
                }else{
                	logger.debug("Email body null for " + request.getResource().getPath());
                }
            }

        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage(), e);
            status = 500;
        }
        // check for redirect
        String redirectTo = request.getParameter(":gsredirect");
        if (redirectTo != null) {
            if (AuthUtil.isRedirectValid(request, redirectTo) || redirectTo.equals(FormsHelper.getReferrer(request))) {
                int pos = redirectTo.indexOf('?');
                redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + status;
                response.sendRedirect(redirectTo);
            } else {
                logger.error("Invalid redirect specified: {}", new Object[]{redirectTo});
                response.sendError(403);
            }
            return;
        }
        if (FormsHelper.isRedirectToReferrer(request)) {
            FormsHelper.redirectToReferrer(request, response,
                    Collections.singletonMap("stats", new String[]{String.valueOf(status)}));
            return;
        }
        
        response.setStatus(status);
    }

    public String getTemplate(SlingHttpServletRequest request, ValueMap values, Map<String,List<String>> formFields, HtmlEmail confEmail, ResourceResolver rr, List<RequestParameter> attachments){
    	try{
    		String templatePath = values.get(TEMPLATE_PATH_PROPERTY, "/content/girlscouts-template/en/email-templates/default_template");
    		if(templatePath.trim().isEmpty()) {
    			templatePath = "/content/girlscouts-template/en/email-templates/default_template";
    		}   		
    		String parsed = "";
    		Resource templateResource = rr.getResource(templatePath);
    		if(templateResource != null) {
	    		Resource dataResource = templateResource.getChild("jcr:content/data");
	    		ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
	    		parsed = parseHtml(templateProps.get("content",""), formFields);
    		}
    		if(parsed.isEmpty()) {
	    		parsed =  "The following fields and values were submitted for form: " + request.getParameter(":formid") +  "<br/> \n";
	    		for(String key: formFields.keySet()) {
	    			List<String> lvalues = formFields.get(key);
	    			String valstring = "";
	    			for(String lval : lvalues) {
	    				valstring = valstring.concat(lval + " ");
	    			}
	    			parsed = parsed.concat(" Name: " + key + " Value: " + valstring + "<br/> \n");
	    			
	    			
	    		}
    		}
    		if(!attachments.isEmpty()) {
    			parsed = parsed.concat("<br/> \n Attachments: <br/>\n");
				for(RequestParameter rp : attachments) {
					parsed = parsed.concat(rp.getFileName() + " <br/> \n");
				}
    		}
    		String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + 
    				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + 
    				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
    				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
    				"<title>Girl Scouts</title></head>";
    		String html = head + "<body>" + parsed + "</body></html>";
    		return html;
    	}catch(Exception e){
    		logger.error("No valid template found for " + request.getResource().getPath());
    		e.printStackTrace();
    		return "";
    	}
    }
    
    public String parseHtml(String html, Map<String,List<String>> fields){
    	//Part 1: Insert field variables whenever %%{field_id}%% is found
    	final Pattern pattern = Pattern.compile("%%(.*?)%%");
    	final Matcher matcher = pattern.matcher(html);
    	final StringBuffer sb = new StringBuffer();
    	while(matcher.find()){
    		List<String> matched = fields.get(matcher.group(1));
    		if(matched != null){
    			if(matched.size() > 1) {
    				matcher.appendReplacement(sb, matched.toString());
    			} else if(matched.toString().length() >= 1){
    				matcher.appendReplacement(sb, matched.toString().substring(1, matched.toString().length()-1));
    			}
    		}
    	}
    	matcher.appendTail(sb);
    	html = sb.toString();
    	
   
    	
    	return html;
    }
    
    private Node getFormStorageNode(Node node, String path) throws RepositoryException {
    		Node rootNode = node;
    		String relativePath = path.replaceAll("/content/usergenerated/", "");
    		String[] subNames = relativePath.split("/");
    		for(int i = 0; i < subNames.length; i++) {
    			String temp = subNames[i];
    			if(rootNode.hasNode(temp)){
    				rootNode = rootNode.getNode(temp);
    			} else {
    				Node tempNode = rootNode.addNode(temp, "sling:Folder");
    				rootNode = tempNode;
    				
    			}
    		}
    		return rootNode;
    }
    
    public String getRenditionPath(String imgPath){
    	final Pattern pattern = Pattern.compile("/jcr:content/renditions/");
    	final Matcher matcher = pattern.matcher(imgPath);
    	if(matcher.find()){
    		return imgPath;
    	}else{
    		return imgPath + "/jcr:content/renditions/original";
    	}
    }

}
