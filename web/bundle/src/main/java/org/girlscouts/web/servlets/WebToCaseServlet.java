package org.girlscouts.web.servlets;

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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.HtmlEmail;
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
import org.girlscouts.common.osgi.component.WebToCase;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.web.util.WebToCaseUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.Servlet;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Web to Case Servlet", "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.extensions=html", "sling.servlet.selectors=webtocase", "sling.servlet.resourceTypes=foundation/components/form/start"})
public class WebToCaseServlet extends SlingAllMethodsServlet implements OptingServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String oid;
    private String apiURL;
    @Reference
    private WebToCase webToCase;
    @Reference
    private GSEmailService gsEmailService;

    private final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
    private final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
    private final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";
    private final String TEMPLATE_PATH_PROPERTY = "templatePath";

    private final Set<String> expectedParams = new HashSet<>();

    @Activate
    private void activate() {
        this.oid = webToCase.getOID();
        this.apiURL = webToCase.getApiURL();
        this.expectedParams.add("orgid");
        this.expectedParams.add("00N22000000ltnH");
        this.expectedParams.add("00N22000000ltnp");
        this.expectedParams.add("CouncilCode");
        this.expectedParams.add("origin");
        this.expectedParams.add("status");
        this.expectedParams.add("name");
        this.expectedParams.add("email");
        this.expectedParams.add("phone");
        this.expectedParams.add("type");
        this.expectedParams.add("subject");
        this.expectedParams.add("description");
        this.expectedParams.add("g-recaptcha-response");
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
            data.add(new NameValuePair("orgid", this.oid));
            data.add(new NameValuePair("00N22000000ltnH", "Web"));
            data.add(new NameValuePair("00N22000000ltnp", "Tier 1"));
            if(request.getParameter("CouncilCode") != null){
                String origin = request.getParameter("CouncilCode") +"cw";
                data.add(new NameValuePair("origin", origin));
            }
            //TODO: remove next line for prod
            data.add(new NameValuePair("origin", "Cultural Resources Support"));
            data.add(new NameValuePair("status", "New"));
            final ValueMap props = ResourceUtil.getValueMap(request.getResource());
            boolean debug = (request.getRequestParameter("debug") != null && "true".equals(request.getRequestParameter("debug")));
            for (Iterator<String> itr = FormsHelper.getContentRequestParameterNames(request); itr.hasNext(); ) {
                final String paraName = itr.next();
                RequestParameter[] paras = request.getRequestParameters(paraName);
                String description ="";
                for (RequestParameter paraValue : paras) {
                    if (paraValue.isFormField()) {//do not support file upload
                        if(expectedParams.contains(paraName)){
                            if("description".equals(paraName.toLowerCase())) {
                                description = paraValue.getString();
                            }else{
                                data.add(new NameValuePair(paraName, paraValue.getString()));
                            }
                        }else{
                            //additional fields that are not expected by salesforce will be appended to description
                            description+="\r\n"+paraName+": "+paraValue.getString();
                        }
                    }
                }
                data.add(new NameValuePair("description",description));
            }
            callHttpClient(data, errors, debug);
            if (errors.size() > 0) {
                respond(new WebToCaseResponse("error", errors), response);
                return;
            } else {
                boolean disableConfirmations = props.get("disableConfirmationEmails", false);
                //Ensure that override is off
                if (!disableConfirmations) {
                    sendConfirmationEmail(request, props);
                }
                respond(new WebToCaseResponse("success", null), response);
            }
        }
    }

    private void sendConfirmationEmail(SlingHttpServletRequest request, ValueMap props) {
        try {
            final ResourceBundle resBundle = request.getResourceBundle(null);
            final StringBuilder builder = new StringBuilder();
            builder.append(request.getScheme());
            builder.append("://");
            builder.append(request.getServerName());
            if ((request.getScheme().equals("https") && request.getServerPort() != 443) || (request.getScheme().equals("http") && request.getServerPort() != 80)) {
                builder.append(':');
                builder.append(request.getServerPort());
            }
            builder.append(request.getRequestURI());
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
                    if (childProperties.get("confirmationemail", false)) {
                        final String[] pValues = request.getParameterValues(desc.getName());
                        for (final String v : pValues) {
                            confirmationEmailAddresses.add(v);
                        }
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
            final HtmlEmail confEmail;
            confEmail = new HtmlEmail();
            confEmail.setCharset("utf-8");
            String confBody = getTemplate(request, props, formFields, confEmail, request.getResourceResolver());
            if (!("").equals(confBody)) {
                confEmail.setHtmlMsg(confBody);
                // mailto
                for (String confEmailAddress : confirmationEmailAddresses) {
                    confEmail.addTo(confEmailAddress);
                }
                final String[] confMailTo = props.get(CONFIRM_MAILTO_PROPERTY, String[].class);
                List<String> bccAddresses = new ArrayList<>();
                if (confMailTo != null) {
                    for (final String rec : confMailTo) {
                        confEmail.addBcc(rec);
                        bccAddresses.add(rec);
                    }
                }
                // subject and from address
                final String confSubject = props.get(CONFIRMATION_SUBJECT_PROPERTY, resBundle.getString("Form Submission Received"));
                confEmail.setSubject(confSubject);
                final String confFromAddress = props.get(CONFIRMATION_FROM_PROPERTY, "");
                if (confFromAddress.length() > 0) {
                    confEmail.addReplyTo(confFromAddress);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.", new Object[]{confFromAddress, confirmationEmailAddresses, confSubject, confBody});
                }
                gsEmailService.sendEmail(confSubject, confirmationEmailAddresses, bccAddresses, confBody, confFromAddress);
            } else {
                logger.debug("Email body null for " + request.getResource().getPath());
            }
        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage(), e);
        }
    }

    public String getTemplate(SlingHttpServletRequest request, ValueMap values, Map<String, List<String>> formFields, HtmlEmail confEmail, ResourceResolver rr) {
        try {
            String templatePath = values.get(TEMPLATE_PATH_PROPERTY, "/content/girlscouts-template/en/email-templates/default-template");
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource templateResource = resourceResolver.resolve(templatePath);
            Resource dataResource = templateResource.getChild("jcr:content/data");
            ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
            String parsed = parseHtml(templateProps.get("content", ""), formFields, confEmail, rr);
            String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>Girl Scouts</title></head>";
            String html = head + "<body>" + parsed + "</body></html>";
            return html;
        } catch (Exception e) {
            logger.error("No valid template found for " + request.getResource().getPath());
            e.printStackTrace();
            return "";
        }
    }

    public String parseHtml(String html, Map<String, List<String>> fields, HtmlEmail confEmail, ResourceResolver rr) {
        //Part 1: Insert field variables whenever %%{field_id}%% is found
        final Pattern pattern = Pattern.compile("%%(.*?)%%");
        final Matcher matcher = pattern.matcher(html);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            List<String> matched = fields.get(matcher.group(1));
            if (matched != null) {
                if (matched.size() > 1) {
                    matcher.appendReplacement(sb, matched.toString());
                } else if (matched.toString().length() >= 1) {
                    matcher.appendReplacement(sb, matched.toString().substring(1, matched.toString().length() - 1));
                }
            }
        }
        matcher.appendTail(sb);
        html = sb.toString();
        //Part 2: Find images and replace them with embeds, embed the image file in the email
        final Pattern imgPattern = Pattern.compile("<img src=\"(.*?)\"");
        final Matcher imgMatcher = imgPattern.matcher(html);
        final StringBuffer imgSb = new StringBuffer();
        while (imgMatcher.find()) {
            byte[] result = null;
            try {
                String renditionPath = getRenditionPath(imgMatcher.group(1));
                Resource imgRes = rr.resolve(renditionPath);
                if (ResourceUtil.isNonExistingResource(imgRes)) {
                    imgRes = rr.resolve(renditionPath.replaceAll("%20", " "));
                    if (ResourceUtil.isNonExistingResource(imgRes)) {
                        throw (new Exception("Cannot find resource: " + renditionPath));
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
                e.printStackTrace();
            }
            try {
                String fileName = imgMatcher.group(1).substring(imgMatcher.group(1).lastIndexOf('/') + 1);
                File imgFile = new File(fileName);
                FileUtils.writeByteArrayToFile(imgFile, result);
                imgMatcher.appendReplacement(imgSb, "<img src=cid:" + (confEmail.embed(imgFile, fileName)));
                imgMatcher.appendTail(imgSb);
                html = imgSb.toString();
            } catch (Exception e) {
                logger.error("Failed to embed image");
                e.printStackTrace();
            }
        }
        return html;
    }

    public String getRenditionPath(String imgPath) {
        final Pattern pattern = Pattern.compile("/jcr:content/renditions/");
        final Matcher matcher = pattern.matcher(imgPath);
        if (matcher.find()) {
            return imgPath;
        } else {
            return imgPath + "/jcr:content/renditions/original";
        }
    }

    private void callHttpClient(List<NameValuePair> data, List<String> errors, boolean debug) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.apiURL);
        NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
        method.setRequestBody(dataArray);
        if (debug) {
            logger.info("http client request para: ");
            for (NameValuePair para : method.getParameters()) {
                logger.info(para.getName() + " :: " + para.getValue());
            }
        }
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                errors.add("Sorry, system error occurred while submitting your form. Please try again later.");
                logger.error("Method failed: " + method.getStatusLine());
            }
            if (debug) {
                logger.info("http client postMethod StatusLine: " + method.getStatusLine());
                logger.info("http client postMethod Response: " + method.getResponseBodyAsString());
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
