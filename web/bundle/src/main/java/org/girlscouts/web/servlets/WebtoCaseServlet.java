package org.girlscouts.web.servlets;

import com.day.cq.mailer.MailService;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.day.cq.wcm.foundation.forms.FormsConstants;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

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

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private List<NameValuePair> data = new LinkedList<NameValuePair>();
	private PostMethod method = null;
	private boolean debug = false;
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
		
		final ValueMap props = ResourceUtil.getValueMap(request.getResource());
		String url = props.get(URL_PROPERTY, String.class);
		String id = props.get(FormsConstants.START_PROPERTY_FORMID, String.class);
		int status = 200;
		debug = (request.getRequestParameter(FORM_DEBUG)!=null)||DEBUG;
		String errormsg = "";
		if(url==null || url.isEmpty()){
			errormsg = "The POST request URL is missing the form begin at "+request.getResource().getPath();
			logger.error(errormsg);
			status = 500;

		}else if(request.getRequestParameter(ORIGIN)==null) {
			errormsg = "The 'origin' value is missing the form, please check if the council is in Salesforce Volenteer System";
			logger.error(errormsg);
			status = 500;
		}else if(request.getRequestParameter(ORGID)==null){
			errormsg = "The 'orgid' value is missing the form, please check if the council is in Salesforce Volenteer System";
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
			status = method.getStatusCode();
			for(Header header:method.getResponseHeaders()){
				response.setHeader(header.getName(), header.getValue());
				System.out.println("http client response header: "+header.getName()+" :: "+header.getValue());
			}

		}

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

		if(debug){//check debug mode
			response.setStatus(status);
			if(method!=null){
				response.getWriter().write(method.getStatusLine().toString());
				if(method.getResponseBodyAsString()!=null){
					response.getWriter().write(method.getResponseBodyAsString());
				}
				response.getWriter().flush();
				return;
			}
				response.getWriter().write(errormsg);
			return;

		}

		//redirect to referrer
		FormsHelper.redirectToReferrer(request, response,
				Collections.singletonMap("stats", new String[]{String.valueOf(status)}));
		return;



	}

	private PostMethod callHttpClient(String url) {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		PostMethod method = new PostMethod(url);
		NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
		method.setRequestBody(dataArray);
		if(debug){
			for(NameValuePair para:method.getParameters()){
				System.out.println("http client request para: "+para.getName()+" :: "+para.getValue());
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
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return method;

	}
	private void reset() {
		data.clear();
		method=null;
		debug=false;
	}


}
