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

import com.day.cq.mailer.MailService;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
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
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import org.apache.sling.api.resource.ResourceResolver;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import javax.jcr.Node;
import javax.activation.FileDataSource;

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
        @Property(name = "sling.servlet.selectors", value = "gsmail"),
        @Property(name = "service.description", value = "Girl Scouts Form Mail Servlet")
})
public class GSMailServlet
        extends SlingAllMethodsServlet
        implements OptingServlet {

    protected static final String EXTENSION = "html";

    protected static final String MAILTO_PROPERTY = "mailto";
    protected static final String CC_PROPERTY = "cc";
    protected static final String BCC_PROPERTY = "bcc";
    protected static final String SUBJECT_PROPERTY = "subject";
    protected static final String FROM_PROPERTY = "from";
    protected static final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
    protected static final String DISABLE_CONFIRMATION_PROPERTY = "disableConfirmation";
    protected static final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
    protected static final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";
    protected static final String TEMPLATE_PATH_PROPERTY = "templatePath";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    protected volatile MailService mailService;

    @Property(value = {
            "/content",
            "/home"
    },
            label = "Resource Whitelist",
            description = "List of paths under which servlet will only accept requests.")
    private static final String PROPERTY_RESOURCE_WHITELIST = "resource.whitelist";
    private String[] resourceWhitelist;

    @Property(value = {
            "/content/usergenerated"
    },
            label = "Resource Blacklist",
            description = "List of paths under which servlet will reject requests.")
    private static final String PROPERTY_RESOURCE_BLACKLIST = "resource.blacklist";
    private String[] resourceBlacklist;

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

        final ResourceBundle resBundle = request.getResourceBundle(null);

        final ValueMap values = ResourceUtil.getValueMap(request.getResource());
        final String[] mailTo = values.get(MAILTO_PROPERTY, String[].class);
        int status = 200;
        if (mailTo == null || mailTo.length == 0 || mailTo[0].length() == 0) {
            // this is a sanity check
            logger.error("The mailto configuration is missing in the form begin at " + request.getResource().getPath());

            status = 500;
        } else if (localService == null) {
            logger.error("The mail service is currently not available! Unable to send form mail.");

            status = 500;
        } else {
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

                // construct msg
                final StringBuilder buffer = new StringBuilder();
                String text = resBundle.getString("You've received a new form based mail from {0}.");
                text = text.replace("{0}", builder.toString());
                buffer.append(text);
                buffer.append("\n\n");
                buffer.append(resBundle.getString("Values"));
                buffer.append(":\n\n");
                // we sort the names first - we use the order of the form field and
                // append all others at the end (for compatibility)

                // let's get all parameters first and sort them alphabetically!
                final List<String> contentNamesList = new ArrayList<String>();
                final Iterator<String> names = FormsHelper.getContentRequestParameterNames(request);
                while (names.hasNext()) {
                    final String name = names.next();
                    contentNamesList.add(name);
                }
                Collections.sort(contentNamesList);
                
                List<String> confirmationEmailAddresses = new ArrayList<String>();
                final List<String> namesList = new ArrayList<String>();
                final Iterator<Resource> fields = FormsHelper.getFormElements(request.getResource());
                while (fields.hasNext()) {
                    final Resource field = fields.next();
                    final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, field);
                    for (final FieldDescription desc : descs) {
                        // remove from content names list
                        contentNamesList.remove(desc.getName());
                        if (!desc.isPrivate()) {
                            namesList.add(desc.getName());
                        }
                        ValueMap childProperties = ResourceUtil.getValueMap(field);
                    	if(childProperties.get("confirmationemail",false)){
                    		final String[] pValues = request.getParameterValues(desc.getName());
                            for (final String v : pValues) {
                            	confirmationEmailAddresses.add(v);
                            }
                		}
                    }
                }
                namesList.addAll(contentNamesList);
  
                // now add form fields to message
                // and uploads as attachments
                final List<RequestParameter> attachments = new ArrayList<RequestParameter>();
                Map<String, List<String>> formFields = new HashMap<String,List<String>>();
                for (final String name : namesList) {
                    final RequestParameter rp = request.getRequestParameter(name);
                    if (rp == null) {
                        //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                        logger.debug("skipping form element {} from mail content because it's not in the request", name);
                    } else if (rp.isFormField()) {
                        buffer.append(name);
                        buffer.append(" : \n");
                        final String[] pValues = request.getParameterValues(name);
                        for (final String v : pValues) {
                        	if(null == formFields.get(name)){
                        		List<String> formField = new ArrayList<String>();
                        		formField.add(v);
                        		formFields.put(name, formField);
                        	}else{
                        		formFields.get(name).add(v);
                        	}
                            buffer.append(v);
                            buffer.append("\n");
                        }
                        buffer.append("\n");
                    } else if (rp.getSize() > 0) {
                        attachments.add(rp);

                    } else {
                        //ignore
                    }
                }
                // if we have attachments we send a multi part, otherwise a simple email
                final Email email;
                if (attachments.size() > 0) {
                    buffer.append("\n");
                    buffer.append(resBundle.getString("Attachments"));
                    buffer.append(":\n");
                    final MultiPartEmail mpEmail = new MultiPartEmail();
                    email = mpEmail;
                    for (final RequestParameter rp : attachments) {
                        final ByteArrayDataSource ea = new ByteArrayDataSource(rp.getInputStream(), rp.getContentType());
                        mpEmail.attach(ea, rp.getFileName(), rp.getFileName());

                        buffer.append("- ");
                        buffer.append(rp.getFileName());
                        buffer.append("\n");
                    }
                } else {
                    email = new SimpleEmail();
                }
                email.setCharset("utf-8");
                email.setMsg(buffer.toString());
                // mailto
                for (final String rec : mailTo) {
                    email.addTo(rec);
                }
                // cc
                final String[] ccRecs = values.get(CC_PROPERTY, String[].class);
                if (ccRecs != null) {
                    for (final String rec : ccRecs) {
                        email.addCc(rec);
                    }
                }
                // bcc
                final String[] bccRecs = values.get(BCC_PROPERTY, String[].class);
                if (bccRecs != null) {
                    for (final String rec : bccRecs) {
                        email.addBcc(rec);
                    }
                }

                // subject and from address
                final String subject = values.get(SUBJECT_PROPERTY, resBundle.getString("Form Mail"));
                email.setSubject(subject);
                final String fromAddress = values.get(FROM_PROPERTY, "");
                if (fromAddress.length() > 0) {
                    email.setFrom(fromAddress);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
                            new Object[]{fromAddress, mailTo, subject, buffer});
                }
                localService.sendEmail(email);
                
                //confirmation emails
                boolean disableConfirmations = values.get("disableConfirmationEmails", false);
                //Ensure that override is off
                if(!disableConfirmations){
                	final HtmlEmail confEmail;
                	confEmail = new HtmlEmail();
                    confEmail.setCharset("utf-8");
                    String confBody = getTemplate(request, values, formFields, confEmail, request.getResourceResolver());
                    if(!("").equals(confBody)){
	                    confEmail.setHtmlMsg(confBody);
	                    // mailto
	                    for(String confEmailAddress : confirmationEmailAddresses){
	                        confEmail.addTo(confEmailAddress);
	                    }
	                    final String[] confMailTo = values.get(CONFIRM_MAILTO_PROPERTY, String[].class);
	                    if(confMailTo != null) {
	                        for (final String rec : confMailTo) {
	                            email.addBcc(rec);
	                        }
	                    }
	
	                    // subject and from address
	                    final String confSubject = values.get(CONFIRMATION_SUBJECT_PROPERTY, resBundle.getString("Form Submission Received"));
	                    confEmail.setSubject(confSubject);
	                    final String confFromAddress = values.get(CONFIRMATION_FROM_PROPERTY, values.get(FROM_PROPERTY, ""));
	                    if (confFromAddress.length() > 0) {
	                        confEmail.setFrom(confFromAddress);
	                    }
	                    if (this.logger.isDebugEnabled()) {
	                        this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
	                                new Object[]{confFromAddress, confirmationEmailAddresses, confSubject, confBody});
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
        }
        // check for redirect
        String redirectTo = request.getParameter(":redirect");
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

    public String getTemplate(SlingHttpServletRequest request, ValueMap values, Map<String,List<String>> formFields, HtmlEmail confEmail, ResourceResolver rr){
    	try{
    		String templatePath = values.get(TEMPLATE_PATH_PROPERTY, "/content/girlscouts-template/en/email-templates/default-template");
    		ResourceResolver resourceResolver = request.getResourceResolver();
    		Resource templateResource = resourceResolver.resolve(templatePath);
    		Resource dataResource = templateResource.getChild("jcr:content/data");
    		ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
    		String parsed = parseHtml(templateProps.get("content",""), formFields, confEmail, rr);
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
    
    public String parseHtml(String html, Map<String,List<String>> fields, HtmlEmail confEmail, ResourceResolver rr){
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
    	
    	//Part 2: Find images and replace them with embeds, embed the image file in the email
    	final Pattern imgPattern = Pattern.compile("<img src=\"(.*?)\"");
    	final Matcher imgMatcher = imgPattern.matcher(html);
    	final StringBuffer imgSb = new StringBuffer();
    	while(imgMatcher.find()){
    		byte[] result = null;
    		try {
    			String renditionPath = getRenditionPath(imgMatcher.group(1));
        		Resource imgRes = rr.resolve(renditionPath);
        		if(ResourceUtil.isNonExistingResource(imgRes)) {
        			imgRes = rr.resolve(renditionPath.replaceAll("%20"," "));
        			if(ResourceUtil.isNonExistingResource(imgRes)){
        				throw(new Exception("Cannot find resource: " + renditionPath));
        			}
        		}
        		Node ntFileNode = imgRes.adaptTo(Node.class);
        		Node ntResourceNode = ntFileNode.getNode("jcr:content");
        		InputStream is = ntResourceNode.getProperty("jcr:data").getBinary().getStream();
        		BufferedInputStream bin = new BufferedInputStream(is);
        		result = IOUtils.toByteArray(bin);
        		bin.close();
        		is.close();
			} catch (Exception e) {
				logger.error("Input Stream Failed");
				System.out.println("Input Stream Failed");
				e.printStackTrace();
			}
    		try {
    			String fileName = imgMatcher.group(1).substring(imgMatcher.group(1).lastIndexOf('/') + 1);
    			File imgFile = new File(fileName);
    			FileUtils.writeByteArrayToFile(imgFile,result);
				imgMatcher.appendReplacement(imgSb, "<img src=cid:" + (confEmail.embed(imgFile,fileName)));
		    	imgMatcher.appendTail(imgSb);
		    	html = imgSb.toString();
			} catch (Exception e) {
				logger.error("Failed to embed image");
				e.printStackTrace();
			}
    	}
    	
    	return html;
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
