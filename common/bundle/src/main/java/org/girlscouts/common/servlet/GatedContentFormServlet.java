package org.girlscouts.common.servlet;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.girlscouts.common.osgi.component.WebToLead;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

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
	private int statusCode;

    @Reference
    private WebToLead webToLead;

    @Reference
    private CouncilCodeToPathMapper councilCodeToPathMapper;

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
        Resource resource = request.getResource();
        PageManager pm = resource.getResourceResolver().adaptTo(PageManager.class);
        Page currentPage = pm.getContainingPage(resource);
        Page homepage = currentPage.getAbsoluteParent(2);
        Page site = homepage.getParent();
        String councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
        if (!StringUtils.isBlank(councilCode)) {
            requestParams.add(new NameValuePair("CouncilCode",councilCode));
        }
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
		statusCode = getResponse(webToLead.getApiURL(), requestParams);
		if (statusCode == HttpStatus.SC_OK) {
			response.getWriter().write("success");
			logger.info("Gated content form response code :" + HttpStatus.SC_OK);
		} else {
			response.sendError(statusCode, "Sorry, we are having a difficulty processing your request.\nPlease try again later.");
		}
	}

	private int getResponse(String endPoint, List<NameValuePair> requestParams) {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(endPoint);
		NameValuePair[] dataArray = requestParams.toArray(new NameValuePair[requestParams.size()]);
        logger.debug("Calling "+endPoint+ " with "+requestParams);
		postMethod.setRequestBody(dataArray);
		postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			statusCode = client.executeMethod(postMethod);
            InputStream in = null;
            ByteArrayOutputStream outStream = null;
            String content = "";
            try {
                in = new GZIPInputStream(postMethod.getResponseBodyAsStream());
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
				logger.error("Method failed: " + postMethod.getStatusLine());
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
                                        logger.error("SFMC responded with error: " + errorDetails[1]);
                                        return HttpStatus.SC_SERVICE_UNAVAILABLE;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            logger.debug("Response Body:"+content);
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