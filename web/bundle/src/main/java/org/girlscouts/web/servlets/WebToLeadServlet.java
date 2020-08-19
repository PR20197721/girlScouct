package org.girlscouts.web.servlets;

import com.day.cq.wcm.foundation.forms.FormsHelper;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.girlscouts.common.osgi.configuration.WebToLeadConfig;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    @Reference
    private WebToLeadConfig webToLeadConfig;

    @Activate
    private void activate(WebToLeadConfig config) {
        this.oid = config.oid();
        this.apiURL = config.apiURL();
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
        List<NameValuePair> data = new LinkedList<NameValuePair>();
        data.add(new NameValuePair("oid", this.oid));
        if (ResourceUtil.isNonExistingResource(request.getResource())) {
            logger.error("Received invalid request!");
            response.setStatus(500);
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
        callHttpClient(data);
        String redirectTo = request.getParameter(":gsredirect");
        if (redirectTo != null && !redirectTo.trim().isEmpty()) {
            if (AuthUtil.isRedirectValid(request, redirectTo) || redirectTo.equals(FormsHelper.getReferrer(request))) {
                int pos = redirectTo.indexOf('?');
                redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + "200";
                response.sendRedirect(redirectTo);
            } else {
                logger.error("Invalid redirect specified: {}", new Object[]{redirectTo});
                response.sendError(403);
            }
            return;
        }

    }

    private void callHttpClient(List<NameValuePair> data) {
        String errormsg = "";
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.apiURL);
        NameValuePair[] dataArray = data.toArray(new NameValuePair[data.size()]);
        method.setRequestBody(dataArray);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        //TODO: Add validation on response from submission

        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Method failed: " + method.getStatusLine());
            }
        } catch (HttpException e) {
            errormsg = "Fatal protocol violation: " + e.getMessage();
            logger.error(errormsg);
        } catch (IOException e) {
            errormsg = "Fatal transport error: " + e.getMessage();
            logger.error(errormsg);
        } catch (Exception e) {
            errormsg = "Fatal error: " + e.getMessage();
            logger.error(errormsg);
        } finally {
            method.releaseConnection();
        }
    }

}
