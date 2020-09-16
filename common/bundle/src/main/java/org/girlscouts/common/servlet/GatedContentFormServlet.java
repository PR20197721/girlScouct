package org.girlscouts.common.servlet;

import com.day.cq.wcm.foundation.forms.FormsHelper;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.osgi.component.WebToLead;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Gated Content Form Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.extensions=html",
        "sling.servlet.selectors=gatedcontentform",
        "sling.servlet.resourceTypes=girlscouts/components/gated-content-form",
        "sling.servlet.resourceTypes=gsusa/components/gated-content-form"})
public class GatedContentFormServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6610151934476719719L;
	private static final Logger logger = LoggerFactory.getLogger(GatedContentFormServlet.class);
	private List<NameValuePair> requestParams = new LinkedList<>();
	private int statusCode;

    @Reference
    private WebToLead webToLead;

    @Activate
    private void activate() {
        logger.debug("Activated");
    }

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        List<NameValuePair> requestParams = new LinkedList<>();
		logger.info("End Point configured :" + webToLead.getApiURL());
		requestParams.add(new NameValuePair("oid", webToLead.getOID()));
        requestParams.add(new NameValuePair("LeadType", "General"));
		logger.info("OrgId :" + webToLead.getOID());
		for (Iterator<String> itr = FormsHelper.getContentRequestParameterNames(request); itr.hasNext();) {
			final String paraName = itr.next();
			RequestParameter[] paras = request.getRequestParameters(paraName);
			for (RequestParameter paraValue : paras) {
				if (paraValue.isFormField()) {
					requestParams.add(new NameValuePair(paraName, paraValue.getString()));
				}
			}
		}
		logger.info("Request Parameters :" + requestParams);
		response.setContentType("text/html");
		statusCode = getResponse(webToLead.getApiURL());
		if (statusCode == HttpStatus.SC_OK) {
			response.getWriter().write("success");
			logger.info("Gated content form response code :" + HttpStatus.SC_OK);
		} else {
			response.sendError(statusCode, "Sorry, we are having a difficulty processing your request.\nPlease try again later.");
		}
	}

	private int getResponse(String endPoint) {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(endPoint);
		NameValuePair[] dataArray = requestParams.toArray(new NameValuePair[requestParams.size()]);
		postMethod.setRequestBody(dataArray);
		postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			statusCode = client.executeMethod(postMethod);
            byte[] bytes = postMethod.getResponseBody();
            String s = Base64.getEncoder().encodeToString(bytes);
            logger.debug("response:"+s);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + postMethod.getStatusLine());
			}
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Fatal error:: " + e.getMessage());
		} finally {
			postMethod.releaseConnection();
			requestParams.clear();
		}
		return statusCode;
	}
}