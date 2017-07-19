package org.girlscouts.web.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.day.cq.wcm.foundation.forms.FormsHelper;

@Component(metatype = false)
@Service(Servlet.class)
@Properties({
	@Property(name = "sling.servlet.resourceTypes", value = "foundation/components/form/start"),
	@Property(name = "sling.servlet.methods", value = "POST"),
	@Property(name = "service.description", value = "Web to Lead Servlet"),
	@Property(name = "sling.servlet.selectors", value = "webtolead")
})
public class WebToLeadServlet extends SlingAllMethodsServlet implements OptingServlet {

	protected static final String EXTENSION = "html";
	protected static final String URL_PROPERTY = "requestURL";
	//Prod settings
	protected static final String SALESFORCE_URL="https://www.salesforce.com/servlet/servlet.WebToLead?encoding=UTF-8";
	protected static final String OID = "oid";
	protected static final String CAMPAIGN_ID = "00NZ0000001vvQA";
	protected static final String GIRL_AGE = "00NZ0000001vvKq";
	protected static final String GIRL_GRADE = "00NZ0000001vvKl";
	protected static final String GIRL_FIRST_NAME = "00NZ0000001vvKg";
	protected static final String GIRL_LAST_NAME = "00NZ0000001vvKb";
	
	//UAT settings
	/*
	protected static final String SALESFORCE_URL="https://test.salesforce.com/servlet/servlet.WebToLead?encoding=UTF-8";
	protected static final String OID = "oid";
	protected static final String CAMPAIGN_ID = "00NZ0000001vvQA";
	protected static final String GIRL_AGE = "00NZ0000001vvKq";
	protected static final String GIRL_GRADE = "00NZ0000001vvKl";
	protected static final String GIRL_FIRST_NAME = "00NZ0000001vvKg";
	protected static final String GIRL_LAST_NAME = "00NZ0000001vvKb";*/
	
	//General Settings
	protected static final String FIRST_NAME = "first_name";
	protected static final String LAST_NAME = "last_name";
	protected static final String ZIP = "zip";
	protected static final String EMAIL = "email";
	
	private List<NameValuePair> data = new LinkedList<NameValuePair>();
	private PostMethod method = null;
	private String errormsg = "";
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
    
	public boolean accepts(SlingHttpServletRequest request) {
		reset();
		return EXTENSION.equals(request.getRequestPathInfo().getExtension());
	}
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response)
					throws ServletException, IOException {
		this.doPost(request, response);

	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response)
					throws ServletException, IOException {
		if (ResourceUtil.isNonExistingResource(request.getResource())) {
			logger.error("Received fake request!");
			response.setStatus(500);
			return;
		}
		
		final ValueMap props = ResourceUtil.getValueMap(request.getResource());
		String url = props.get(URL_PROPERTY, String.class);
		if(url.isEmpty()){
			url = SALESFORCE_URL;
		}
		int status = 200;
		
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
		
		//check for redirect
		String redirectTo = request.getParameter(FormsConstants.REQUEST_PROPERTY_REDIRECT);
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

	}
	
	private PostMethod callHttpClient(String url) {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		PostMethod method = new PostMethod(url);
		NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
		method.setRequestBody(dataArray);
		
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
		errormsg="";
	}
	

}
