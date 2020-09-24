package org.girlscouts.web.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.girlscouts.common.osgi.component.WebToCase;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.web.util.WebToCaseUtils;
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
import java.util.*;
import java.util.zip.GZIPInputStream;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Web to Case Servlet", "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.extensions=html", "sling.servlet.selectors=webtocase", "sling.servlet.resourceTypes=foundation/components/form/start"})
public class WebToCaseServlet extends SlingAllMethodsServlet implements OptingServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String oid;
    private String apiURL;
    private Map<String,String> recaptchaMap;
    private Set<String> expectedParams;
    @Reference
    private WebToCase webToCase;
    @Reference
    private GSEmailService gsEmailService;
    @Reference
    private CouncilCodeToPathMapper councilCodeToPathMapper;

    @Activate
    private void activate() {
        this.oid = webToCase.getOID();
        this.apiURL = webToCase.getApiURL();
        this.recaptchaMap = webToCase.getRecaptchaMap();
        this.expectedParams = webToCase.getExpectedParams();
    }

    @Override
    public boolean accepts(SlingHttpServletRequest request) {
        return true;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        return;
    }

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        boolean debug = (request.getParameter("debug") != null && "true".equals(request.getParameter("debug")));
        if(debug){
            logger.debug("Submitting Web-To-Case data in debug mode.");
        }else{
            logger.debug("Submitting Web-To-Case data.");
        }
        List<String> errors = WebToCaseUtils.validateForm(request);
        if (errors != null && errors.size() > 0) {
            WebToCaseResponse respObj = new WebToCaseResponse("error", errors);
            respond(respObj, response);
            return;
        } else {
            if (ResourceUtil.isNonExistingResource(request.getResource())) {
                errors.add("Received invalid request!");
                logger.error("Received invalid request!");
                respond(new WebToCaseResponse("error", errors), response);
                return;
            }
            List<NameValuePair> data = new LinkedList<NameValuePair>();
            data.addAll(getSubmittedParams(request));
            data.addAll(getAdditionalParams(request));
            postToSF(data, errors, debug);
            if (errors.size() > 0) {
                respond(new WebToCaseResponse("error", errors), response);
                return;
            } else {
                final ValueMap props = ResourceUtil.getValueMap(request.getResource());
                boolean disableConfirmations = props.get("disableConfirmationEmails", false);
                //Ensure that override is off
                if (!disableConfirmations) {
                    sendConfirmationEmail(request, props);
                }
                respond(new WebToCaseResponse("success", null), response);
            }
        }
    }

    private List<NameValuePair> getAdditionalParams(SlingHttpServletRequest request) {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new NameValuePair("status", "New"));
        data.add(new NameValuePair("orgid", this.oid));
        data.add(new NameValuePair("00N22000000ltnH", "Web"));
        data.add(new NameValuePair("00N22000000ltnp", "Tier 1"));
        data.add(new NameValuePair("recordType", "012220000000Tvw"));
        Resource resource = request.getResource();
        PageManager pm = resource.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pm.getContainingPage(resource);
        Page homepage = currentPage.getAbsoluteParent(2);
        Page site = homepage.getParent();
        data.add(new NameValuePair("retURL", resource.getResourceResolver().map(currentPage.getPath()) + ".html"));
        String councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
        if (!StringUtils.isBlank(councilCode)) {
            logger.debug("found council code :"+councilCode);
            String origin = councilCode +"cw";
            data.add(new NameValuePair("origin", origin));
        }
        logger.debug("returning additional params: "+data);
        return data;
    }

    private List<NameValuePair> getSubmittedParams(SlingHttpServletRequest request) {
        List<NameValuePair> data = new ArrayList<>();
        String description ="";
        String extraParameters ="";
        for (Iterator<String> itr = FormsHelper.getContentRequestParameterNames(request); itr.hasNext(); ) {
            final String paraName = itr.next();
            logger.debug("reading value for "+paraName);
            RequestParameter[] paras = request.getRequestParameters(paraName);
            for (RequestParameter paraValue : paras) {
                if (paraValue.isFormField()) {//do not support file upload
                    if(expectedParams.contains(paraName)){
                        if("description".equals(paraName.toLowerCase())) {
                            if(!StringUtils.isBlank(paraValue.getString())) {
                                description += paraValue.getString();
                            }
                        }else{
                            logger.debug("Adding:  "+paraName+" = " +paraValue.getString());
                            if(!StringUtils.isBlank(paraValue.getString())) {
                                data.add(new NameValuePair(paraName, paraValue.getString()));
                            }
                        }
                    }else{
                        //additional fields that are not expected by salesforce will be appended to description
                        if(!"g-recaptcha-response".equals(paraName) && !paraName.contains("debug")) {
                            if(!StringUtils.isBlank(paraValue.getString())) {
                                logger.debug("Adding: " + paraName + " = " + paraValue.getString() + " to description");
                                extraParameters += "\r\n" + paraName + ": " + paraValue.getString();
                            }
                        }
                    }
                }
            }
        }
        description = description + extraParameters;
        logger.debug("Adding:  description = " +description);
        data.add(new NameValuePair("description",description));
        logger.debug("returning submitted params: "+data);
        return data;
    }

    private void sendConfirmationEmail(SlingHttpServletRequest request, ValueMap props) {
        try {
            final ResourceBundle resBundle = request.getResourceBundle(null);
            String formStart = request.getParameter(":formstart");
            ResourceResolver rr = request.getResourceResolver();
            logger.debug("reading form configuration from {}",formStart);
            ValueMap formProps = rr.resolve(formStart).adaptTo(ValueMap.class);
            final String debugEmail = formProps.get("debugEmail","");
            final boolean isDebug = !StringUtils.isBlank(debugEmail) ? true : false;
            if(isDebug){
               logger.debug("sending debug email to {}", debugEmail);
            }
            final String confFromAddress = formProps.get("confirmationFrom","");
            logger.debug("confFromAddress = {}", confFromAddress);
            final String[] confMailTo = formProps.get("confirmationmailto", String[].class);
            logger.debug("confMailTo = {}", confMailTo);
            final String confSubject = formProps.get("confirmationSubject", resBundle.getString("Form Submission Received"));
            logger.debug("confSubject = {}", confSubject);
            String templatePath = formProps.get("templatePath", "/content/girlscouts-template/en/email-templates/default_template");
            if(StringUtils.isBlank(templatePath)){
                templatePath = "<p>Thank you for contacting Girl Scouts. We have received your message and will respond shortly.</p>";
            }
            logger.debug("templatePath = {}", templatePath);
            String confBody = getTemplate(request, templatePath);
            if (!StringUtils.isBlank(confBody)) {
                List<String> confirmationEmailAddresses = new ArrayList<String>();
                if(!isDebug) {
                    if (confMailTo != null) {
                        for (final String rec : confMailTo) {
                            confirmationEmailAddresses.add(rec);
                        }
                    }
                }else{
                    confirmationEmailAddresses.add(debugEmail);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.", new Object[]{confFromAddress, confirmationEmailAddresses, confSubject, confBody});
                }
                gsEmailService.sendEmail(confSubject, confirmationEmailAddresses, confBody, confFromAddress);
            } else {
                logger.debug("Email body null for " + request.getResource().getPath());
            }
        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage(), e);
        }
    }

    private Map<String, List<String>> getFieldListMap(SlingHttpServletRequest request) {
        final List<String> contentNamesList = new ArrayList<String>();
        final Iterator<String> names = FormsHelper.getContentRequestParameterNames(request);
        while (names.hasNext()) {
            final String name = names.next();
            contentNamesList.add(name);
        }
        Collections.sort(contentNamesList);
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
            }
        }
        namesList.addAll(contentNamesList);
        Map<String, List<String>> formFields = new HashMap<String, List<String>>();
        for (final String name : namesList) {
            final RequestParameter rp = request.getRequestParameter(name);
            if (rp == null) {
                //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                logger.debug("skipping form element {} from mail content because it's not in the request", name);
            } else if (rp.isFormField()) {
                final String[] pValues = request.getParameterValues(name);
                for (final String v : pValues) {
                    if (null == formFields.get(name)) {
                        List<String> formField = new ArrayList<String>();
                        formField.add(v);
                        formFields.put(name, formField);
                    } else {
                        formFields.get(name).add(v);
                    }
                }
            }
        }
        return formFields;
    }

    public String getTemplate(SlingHttpServletRequest request, String templatePath) {
        try {
            ResourceResolver rr = request.getResourceResolver();
            Map<String, List<String>> formFields = getFieldListMap(request);
            logger.debug("Collected "+formFields.size()+ " fields");
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource templateResource = resourceResolver.resolve(templatePath);
            Resource dataResource = templateResource.getChild("jcr:content/data");
            logger.debug("Loaded email template from "+templateResource.getPath());
            ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
            String parsed = parseHtml(templateProps.get("content", ""), formFields, rr);
            String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>Girl Scouts</title></head>";
            String html = head + "<body>" + parsed + "</body></html>";
            return html;
        } catch (Exception e) {
            logger.error("No valid template found for " + request.getResource().getPath(), e);
            return "";
        }
    }

    public String parseHtml(String html, Map<String, List<String>> fields, ResourceResolver rr) {
        logger.debug("Parsing html "+html);
        Set<String> fieldNames = fields.keySet();
        for(String fieldName:fieldNames){
            String placeholder = "%%("+fieldName+")%%";
            logger.debug("Checking for %%("+fieldName+")%% in email template");
            if(html.contains(placeholder)){
                logger.debug("Found %%("+fieldName+")%% in email template");
                List<String> values = fields.get(fieldName);
                if(values != null &&  values.size() > 0){
                    logger.debug("Replacing "+placeholder+" with "+values.get(0));
                    html = html.replaceAll(placeholder,values.get(0));
                }
            }
        }
        logger.debug("Result html "+html);
        return html;
    }

    private void postToSF(List<NameValuePair> data, List<String> errors, boolean debug) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.apiURL);
        NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
        method.setRequestBody(dataArray);
        if (debug) {
            logger.debug("data:"+data);
        }
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            // Execute the method.
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
            content = StringUtils.isBlank(content) ? method.getResponseBodyAsString() :content;
            logger.debug("http client postMethod StatusLine: " + method.getStatusLine());
            logger.debug("http client postMethod Response: " + content);
            if (statusCode != HttpStatus.SC_OK) {
                errors.add("Sorry, system error occurred while submitting your form. Please try again later.");
                logger.error("Method failed: " + method.getStatusLine());
            }else{
                if(!content.contains("Your request has been queued.")) {
                    if (content.contains("ERROR") || content.contains("ALERT" )) {
                        String[] lines = content.split(System.getProperty("line.separator"));
                        if(lines != null){
                            boolean errorMessage = false;
                            for(String line:lines){
                                if(errorMessage){
                                    errors.add(line);
                                    logger.error("SF responded with error: " + line);
                                }
                                if(line.contains("ALERT")){
                                    errorMessage = true;
                                }else{
                                    errorMessage = false;
                                }
                            }
                        }
                    } else {
                        errors.add("Sorry, system error occurred while submitting your form. Please try again later.");
                    }
                }
            }

        } catch (Exception e) {
            String errormsg = "Sorry, system error occurred while submitting your form. Please try again later.";
            errors.add(errormsg);
            logger.error(errormsg, e);
        } finally {
            method.releaseConnection();
        }
    }

    private void respond(WebToCaseResponse respObj, SlingHttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.print(new Gson().toJson(respObj));
            out.flush();
        } catch (Exception e) {
            logger.error("Encountered error:", e);
        }
    }

    private class WebToCaseResponse {
        private String status;
        private List<String> errors;

        WebToCaseResponse(String status, List<String> errors) {
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
