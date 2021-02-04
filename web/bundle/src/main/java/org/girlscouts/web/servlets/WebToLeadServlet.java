package org.girlscouts.web.servlets;

import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.google.gson.Gson;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.osgi.component.WebToLead;
import org.girlscouts.web.service.recaptcha.RecaptchaService;
import org.girlscouts.web.util.WebToLeadUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts Web to Lead Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.extensions=html",
        "sling.servlet.selectors=webtolead",
        "sling.servlet.resourceTypes=foundation/components/form/start"})
public class WebToLeadServlet extends SlingAllMethodsServlet implements OptingServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String oid;
    private String apiURL;
    
    protected static final String SECRET = "secret";
    protected static final String RESPONSE_VAL = "g-recaptcha-response";
    protected static final String CAPTCHA_RESPONSE = ":cq:captcha";

    @Reference
    private WebToLead webToLead;
    
    @Reference
    private RecaptchaService recaptchaService;

    @Activate
    private void activate() {
        this.oid = webToLead.getOID();
        this.apiURL = webToLead.getApiURL();
        logger.debug("Activated");
    }

    @Override
    public boolean accepts(SlingHttpServletRequest request) {
        return true;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        return;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        logger.debug("Processing Post");
        
        //Recaptcha Server Validations
        String responseVal = request.getParameter(RESPONSE_VAL);
        String captcha = request.getParameter(CAPTCHA_RESPONSE);
        String secret = request.getParameter(SECRET);
        List<String> errors = WebToLeadUtils.validateForm(request);
        if (null == captcha){
        	if (null != responseVal) {
		        boolean success = recaptchaService.captchaSuccess(secret, responseVal);
		        if (!success) {
		        	logger.debug("Recaptcha validation failed");
		        	errors.add("Validation failed for : g-recaptcha-response. Please try again.");
		        	return;
		        }
	        } 
        }
        
        if(errors!= null && !errors.isEmpty()){
            WebToLeadResponse respObj = new WebToLeadResponse("error", errors);
            respond(respObj, response);
            return;
        } else {
            List<NameValuePair> data = new LinkedList<NameValuePair>();
            data.add(new NameValuePair("oid", webToLead.getOID()));
            if (ResourceUtil.isNonExistingResource(request.getResource())) {
                errors.add("Received invalid request!");
                logger.error("Received invalid request!");
                respond(new WebToLeadResponse("error", errors),response);
                return;
            }
            for (Iterator<String> itr = FormsHelper.getContentRequestParameterNames(request); itr.hasNext(); ) {
                final String paraName = itr.next();
                RequestParameter[] paras = request.getRequestParameters(paraName);
                for (RequestParameter paraValue : paras) {
                    if (paraValue.isFormField()) {//do not support file upload
                        data.add(new NameValuePair(paraName, paraValue.getString()));//add to encription
                    }
                }
            }
            callHttpClient(data, errors);
            if(errors.size() > 0){
                respond(new WebToLeadResponse("error", errors),response);
                return;
            } else {
                respond(new WebToLeadResponse("success", null),response);
            }
        }
    }

    private void respond(WebToLeadResponse respObj, SlingHttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.print(new Gson().toJson(respObj));
            out.flush();
        }catch(Exception e){
            logger.error("Encountered error:", e);
        }
    }

    private void callHttpClient(List<NameValuePair> data, List<String> errors) {
        String errormsg = "";
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(webToLead.getApiURL());
        NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
        method.setRequestBody(dataArray);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        method.addRequestHeader("Accept-Encoding", "gzip,deflate");
        method.addRequestHeader("Accept", "text/html;charset=utf-8");
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            logger.debug("Calling "+this.apiURL+ " with "+data);
            int statusCode = client.executeMethod(method);
            InputStream in = null;
            ByteArrayOutputStream outStream = null;
            String content = "";
            try {
                in = new GZIPInputStream(method.getResponseBodyAsStream());
                outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
                content = new String(outStream.toByteArray(), "UTF-8");
            }catch(Exception e){

            } finally {
                if(outStream != null) {
                    outStream.close();
                }
                if(in != null) {
                    in.close();
                }
            }
            logger.debug("SFMC RESPONSE: "+content);
            if (statusCode != HttpStatus.SC_OK) {
                errors.add("Sorry, system error occurred while submitting your form. Please try again later.");
                logger.error("Method failed: " + method.getStatusLine());
            }else{
                if(!content.contains("SUCCESS")) {
                    if (content.contains("ERROR")) {
                        String[] lines = content.split(System.getProperty("line.separator"));
                        if(lines != null){
                            for(String line:lines){
                                if(line.contains("ERROR")){
                                    line = line.trim();
                                    String[] errorDetails = line.split("\\|");
                                    if (errorDetails.length >= 2) {
                                        errors.add("Sorry, an error occurred: " + errorDetails[1]);
                                        logger.error("SFMC responded with error: " + errorDetails[1]);
                                    }
                                }
                            }
                        }
                    } else {
                        errors.add("Sorry, system error occurred while submitting your form. Please try again later.");
                    }
                }
            }
        } catch (Exception e) {
            errormsg = "Sorry, system error occurred while submitting your form. Please try again later.";
            errors.add(errormsg);
            logger.error(errormsg, e);
        }  finally {
            method.releaseConnection();
        }
    }

    private class WebToLeadResponse{

        private String status;
        private List<String> errors;

        WebToLeadResponse(String status, List<String> errors){
            this.status = status;
            this.errors = errors;

        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}
