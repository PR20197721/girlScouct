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

    protected static final String URL_PROPERTY = "formActionURL";
//    protected static final String CW_RW_PROPERTY = "cwrw";
    
    protected static final String ORIGIN = "origin";

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
    /**
     * @see org.apache.sling.api.servlets.OptingServlet#accepts(org.apache.sling.api.SlingHttpServletRequest)
     */
    public boolean accepts(SlingHttpServletRequest request) {
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
    	String url = props.get(URL_PROPERTY, "https://www.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8");
        String id = props.get(FormsConstants.START_PROPERTY_FORMID, "");
        
        int status = 200;

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
        
        
        for(Iterator<String> itr=FormsHelper.getContentRequestParameterNames(request); itr.hasNext();){
    		final String paraName=itr.next();
            RequestParameter[] paras = request.getRequestParameters(paraName);
            for(RequestParameter paraValue : paras){
            	if(paraValue.isFormField()){//do not support file upload
            		 data.add(new NameValuePair(paraName,paraValue.getString()));//add to encription
            	}
            							
            }
        }
    	PostMethod method = callHttpClient(url);
    	status = method.getStatusCode();
        response.setStatus(status);
        for(Header header:method.getResponseHeaders()){
            response.setHeader(header.getName(), header.getValue());
        }
        response.getWriter().write(method.getResponseBodyAsString());
        response.getWriter().flush();

             
        // check for redirect
//        String redirectTo = request.getParameter(":redirect");
//        if (redirectTo != null) {
//            if (AuthUtil.isRedirectValid(request, redirectTo) || redirectTo.equals(FormsHelper.getReferrer(request))) {
//                int pos = redirectTo.indexOf('?');
//                redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + status;
//                response.sendRedirect(redirectTo);
//            } else {
//                logger.error("Invalid redirect specified: {}", new Object[]{redirectTo});
//                response.sendError(403);
//            }
//            return;
//        }
        
        String redirectLocation;
        Header locationHeader = method.getResponseHeader("location");
        if (locationHeader != null) {
            redirectLocation = locationHeader.getValue();
        } else {
            // The response is invalid and did not provide the new location for
            // the resource.  Report an error or possibly handle the response
            // like a 404 Not Found error.
        }
//        if (FormsHelper.isRedirectToReferrer(request)) {
//            FormsHelper.redirectToReferrer(request, response,
//                    Collections.singletonMap("stats", new String[]{String.valueOf(status)}));
//            return;
//        }
        
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

          // Read the response body.
          String responseBody = method.getResponseBodyAsString();
          // Deal with the response.
          // Use caution: ensure correct character encoding and is not binary data
          System.out.println(responseBody);

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


}
