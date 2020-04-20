package org.girlscouts.common.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

@Component(metatype = false, immediate = true)
@Service
@Properties(value = { @Property(name = "pageInfoProviderType", value = "sites.listView.info.provider."
		+ CreatedPageInfoProvider.providerType) })

@SuppressWarnings("deprecation")
public class CreatedPageInfoProvider implements com.day.cq.wcm.api.PageInfoProvider {
	private static final Logger logger = LoggerFactory.getLogger(CreatedPageInfoProvider.class);

	public static final String providerType = "created";

	public void updatePageInfo(SlingHttpServletRequest request, JSONObject info, Resource resource)
			throws JSONException {

		logger.info("Running updatePageInfo()");
		Page page = resource.adaptTo(Page.class);
		JSONObject myProjectInfo = new JSONObject();
		Node dataNode = page.getContentResource("data").adaptTo(Node.class);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		try {
			if (null != dataNode && dataNode.getName().equals("data") && dataNode.hasProperty("start")) {
				Date sDate = dataNode.getProperty("start").getDate().getTime();
				logger.info("Date Value :"+sDate);
				String timezone = null;
				if (dataNode.hasProperty("timezone")) {
					timezone = dataNode.getProperty("timezone").getString();
					logger.info("timezone :"+timezone);
				}
				if (null != timezone) {
					sdf.setTimeZone(TimeZone.getTimeZone(timezone));
				}
				myProjectInfo.put("created", "");
				myProjectInfo.put("createdBy", sdf.format(sDate));
				logger.info("Formatted Date "+sdf.format(sDate));

			}
			info.put(providerType, myProjectInfo);
		} catch (RepositoryException e) {
			logger.error("error occured" + e);
		}

	}
}
