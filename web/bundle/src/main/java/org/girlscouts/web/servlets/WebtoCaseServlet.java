package org.girlscouts.web.servlets;

import com.day.cq.mailer.MailService;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.day.cq.wcm.foundation.forms.FormsConstants;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@Component(metatype = false)
@Service(Servlet.class)
@Properties({
	@Property(name = "sling.servlet.resourceTypes", value = "foundation/components/form/start"),
	@Property(name = "sling.servlet.methods", value = "POST"),
	@Property(name = "service.description", value = "Web to Case Servlet"),
	@Property(name = "sling.servlet.selectors", value = "webtocase")
})
public class WebtoCaseServlet extends SlingAllMethodsServlet
implements OptingServlet {
	protected static final String EXTENSION = "html";
	protected static final boolean DEBUG = false;
	protected static final String SALESFORCE_URL="https://www.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";
	//protected static final String SALESFORCE_URL="https://test.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";

	protected static final String URL_PROPERTY = "requestURL";
	protected static final String CW_RW_PROPERTY = "cwrw";
	protected static final String FORM_DEBUG = "debug";
	protected static final String ORIGIN = "origin";
	protected static final String ORGID = "orgid";
	protected static final String NAME = "name";
	protected static final String EMAIL = "email";
	protected static final String PHONE = "phone";
	protected static final String PREFERRED_METHOD_OF_CONTACT = "00NG000000DdNmM";
	protected static final String BEST_TIME_TO_CALL = "00NG000000DdNmL";
	protected static final String ZIP_CODE = "00NG000000DdNmN";
	protected static final String SUBJECT = "subject";
	protected static final String DESCRIPTION = "description";
    protected static final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
    protected static final String DISABLE_CONFIRMATION_PROPERTY = "disableConfirmation";
    protected static final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
    protected static final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";
    protected static final String TEMPLATE_PATH_PROPERTY = "templatePath";

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    protected volatile MailService mailService;
    
	private List<NameValuePair> data = new LinkedList<NameValuePair>();
	private PostMethod method = null;
	private boolean debug = false;
	private String errormsg = "";
	/**
	 * @see org.apache.sling.api.servlets.OptingServlet#accepts(org.apache.sling.api.SlingHttpServletRequest)
	 */
	public boolean accepts(SlingHttpServletRequest request) {
		reset();
		return EXTENSION.equals(request.getRequestPathInfo().getExtension());
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

		if (ResourceUtil.isNonExistingResource(request.getResource())) {
			logger.debug("Received fake request!");
			response.setStatus(500);
			return;
		}
		final ResourceBundle resBundle = request.getResourceBundle(null);
		
		final MailService localService = this.mailService;

		final ValueMap props = ResourceUtil.getValueMap(request.getResource());
		String url = props.get(URL_PROPERTY, String.class);
		String id = props.get(FormsConstants.START_PROPERTY_FORMID, String.class);
		String cwrw = props.get(CW_RW_PROPERTY,String.class);
		int status = 200;
		debug = (request.getRequestParameter(FORM_DEBUG)!=null)||DEBUG;
		if(url==null || url.isEmpty()){
			errormsg = "The POST request URL is missing the form start at "+request.getResource().getPath()+".\n\nPlease go to action configuration.";
			logger.error(errormsg);
			status = 500;

		}else if(request.getRequestParameter(ORIGIN)==null) {
			if(cwrw==null || CW_RW_PROPERTY.indexOf(cwrw)<0 ){
				errormsg= "The cwrw property is missing the form start at "+request.getResource().getPath()+".\n\nPlease go to action configuration.";
			}else{
				errormsg = "The 'origin' value is missing the form. The council code can not be found. \n\nPlease check if the council is in Salesforce Volenteer System.";
			}
			logger.error(errormsg);
			status = 500;
		}else if(request.getRequestParameter(ORGID)==null){
			errormsg = "The 'orgid' value is missing the form. \n\nPlease check if the council is in Salesforce Volenteer System";
			logger.error(errormsg);
			status = 500;
		}else{
			//				ValueMap formValues = FormsHelper.getGlobalFormValues(request); 
			for(Iterator<String> itr=FormsHelper.getContentRequestParameterNames(request); itr.hasNext();){
				final String paraName=itr.next();
				RequestParameter[] paras = request.getRequestParameters(paraName);
				for(RequestParameter paraValue : paras){
					if(paraValue.isFormField()){//do not support file upload
						data.add(new NameValuePair(paraName,paraValue.getString()));//add to encription
					}
				}
			}
			method = callHttpClient(url);
			if(method.hasBeenUsed() && method.isRequestSent()){
				status = method.getStatusCode();
				System.out.println("http client response header: ");
				for(Header header:method.getResponseHeaders()){
					response.setHeader(header.getName(), header.getValue());
					System.out.println(header.getName()+" :: "+header.getValue());
				}
			}else{
				status = 500;
				errormsg = "Http client POST has not been executed, exceptions thrown: \n\n" + errormsg;
				errormsg += "\n\nPlease check the log to see details.";
			}

		}
		
		//Confirmation Email
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
            
            Map<String, List<String>> formFields = new HashMap<String,List<String>>();
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
                } else {
                    //ignore
                }
            }
            
            boolean disableConfirmations = props.get("disableConfirmationEmails", false);
            //Ensure that override is off
            if(!disableConfirmations){
            	final HtmlEmail confEmail;
            	confEmail = new HtmlEmail();
                confEmail.setCharset("utf-8");
                String confBody = getTemplate(request, props, formFields, confEmail, request.getResourceResolver());
                if(!("").equals(confBody)){
                    confEmail.setHtmlMsg(confBody);
                    // mailto
                    for(String confEmailAddress : confirmationEmailAddresses){
                        confEmail.addTo(confEmailAddress);
                    }
                    final String[] confMailTo = props.get(CONFIRM_MAILTO_PROPERTY, String[].class);
                    if(confMailTo != null) {
                        for (final String rec : confMailTo) {
                            confEmail.addBcc(rec);
                        }
                    }

                    // subject and from address
                    final String confSubject = props.get(CONFIRMATION_SUBJECT_PROPERTY, resBundle.getString("Form Submission Received"));
                    confEmail.setSubject(confSubject);
                    final String confFromAddress = props.get(CONFIRMATION_FROM_PROPERTY, "");
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

		//check for redirect
		String redirectTo = request.getParameter(":gsredirect");
		if (redirectTo != null && !redirectTo.trim().isEmpty()) {
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

		if(debug){//check debug mode
			response.setStatus(status);
			if(method!=null){
				if(method.getStatusLine()!=null){
					response.getWriter().write(method.getStatusLine().toString());
				}
				if(method.getResponseBodyAsString()!=null){
					response.getWriter().write(method.getResponseBodyAsString());
				}	
			}
			response.getWriter().write(errormsg);
			response.getWriter().flush();
			return;
		}

		//redirect to referrer
		FormsHelper.redirectToReferrer(request, response,
				Collections.singletonMap("stats", new String[]{String.valueOf(status)}));
		return;



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

	private PostMethod callHttpClient(String url) {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		PostMethod method = new PostMethod(url);
		NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
		method.setRequestBody(dataArray);
		if(debug){
			System.out.println("http client request para: ");
			for(NameValuePair para:method.getParameters()){
				System.out.println(para.getName()+" :: "+para.getValue());
			}
		}
		method.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(3, false));
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + method.getStatusLine());
			}
			if (debug) {
				System.out.println("http client postMethod StatusLine: " + method.getStatusLine());
				System.out.println("http client postMethod Response: " + method.getResponseBodyAsString());
			}


		} catch (HttpException e) {
			errormsg = "Fatal protocol violation: " + e.getMessage();
			logger.error(errormsg);
			e.printStackTrace();
		} catch (IOException e) {
			errormsg = "Fatal transport error: " + e.getMessage();
			logger.error(errormsg);
			e.printStackTrace();
		} catch (Exception e){
			errormsg="Fatal error: " + e.getMessage();
			logger.error(errormsg);
			e.printStackTrace();
		}finally {
			// Release the connection.
			method.releaseConnection();
		}
		return method;

	}
	private void reset() {
		data.clear();
		method=null;
		debug=false;
		errormsg="";
	}


}
