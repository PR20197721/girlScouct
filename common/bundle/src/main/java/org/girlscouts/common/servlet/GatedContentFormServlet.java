package org.girlscouts.common.servlet;

import java.io.IOException;
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
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.webtolead.config.WebToLeadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.foundation.forms.FormsHelper;

@Component(metatype = false)
@Service(Servlet.class)
@Properties({
		@Property(name = "sling.servlet.resourceTypes", value = { "girlscouts/components/gated-content-form",
				"gsusa/components/gated-content-form" }),
		@Property(name = "sling.servlet.methods", value = "POST"),
		@Property(name = "sling.servlet.selectors", value = "gatedcontentform"),
		@Property(name = "sling.servlet.extensions", value = "html"),
		@Property(name = "service.description", value = "Gated Content Form Servlet"), })
public class GatedContentFormServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6610151934476719719L;

	private static final Logger logger = LoggerFactory.getLogger(GatedContentFormServlet.class);

	protected static final String URL_PROPERTY = "apiUrl";

	private List<NameValuePair> requestParams = new LinkedList<>();

	private int statusCode;

	protected static final String EXTENSION = "html";

	@Reference
	private WebToLeadConfig webToLeadConfig;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		String endPoint = webToLeadConfig.getAPIURL();
		logger.info("End Point configured :" + endPoint);

		requestParams.add(new NameValuePair("oid", webToLeadConfig.getOID()));
		logger.info("OrgId :" + webToLeadConfig.getOID());

		final String sfCampaignIdKey = "00N0f00000Eoc8U";

		for (Iterator<String> itr = FormsHelper.getContentRequestParameterNames(request); itr.hasNext();) {
			final String paraName = itr.next();

			RequestParameter[] paras = request.getRequestParameters(paraName);
			for (RequestParameter paraValue : paras) {
				if (paraValue.isFormField()) {
					requestParams.add(new NameValuePair(paraName, paraValue.getString()));
					if (paraName.equals("Campaign_ID")) {
						requestParams.add(new NameValuePair(sfCampaignIdKey, paraValue.getString()));
					}

				}
			}
		}
		logger.info("Request Parameters :" + requestParams);
		response.setContentType("text/html");

		statusCode = getResponse(endPoint);
		if (statusCode == HttpStatus.SC_OK) {
			response.getWriter().write("success");
			logger.info("Gated content form response code :" + HttpStatus.SC_OK);
		} else {
			response.sendError(statusCode,
					"Sorry, we are having a difficulty processing your request.\nPlease try again later.");
		}

	}

	private int getResponse(String endPoint) {

		HttpClient client = new HttpClient();

		PostMethod postMethod = new PostMethod(endPoint);

		NameValuePair[] dataArray = requestParams.toArray(new NameValuePair[requestParams.size()]);

		postMethod.setRequestBody(dataArray);

		postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			statusCode = client.executeMethod(postMethod);
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