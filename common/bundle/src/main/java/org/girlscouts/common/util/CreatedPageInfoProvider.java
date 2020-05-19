package org.girlscouts.common.util;

import com.day.cq.wcm.api.Page;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component(metatype = false, immediate = true)
@Service
@Properties(value = { @Property(name = "pageInfoProviderType", value = "sites.listView.info.provider."
		+ CreatedPageInfoProvider.providerType) })

@SuppressWarnings("deprecation")
public class CreatedPageInfoProvider implements com.day.cq.wcm.api.PageInfoProvider {
	private static final Logger logger = LoggerFactory.getLogger(CreatedPageInfoProvider.class);
	public static final String providerType = "dataNode";

	public void updatePageInfo(SlingHttpServletRequest request, JSONObject info, Resource resource)
			throws JSONException {
		Page page = resource.adaptTo(Page.class);
		JSONObject pageInfo = new JSONObject();
		Node dataNode = page.getContentResource("data").adaptTo(Node.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // foundation-time element requires ISO 8601 format
		try {
			if (null != dataNode && dataNode.hasProperty("start")) {
				Date sDate = dataNode.getProperty("start").getDate().getTime();
				String timezone = null;
				if (dataNode.hasProperty("timezone")) {
					timezone = dataNode.getProperty("timezone").getString();
				}
				if (null != timezone) {
					sdf.setTimeZone(TimeZone.getTimeZone(timezone));
				}
				JSONObject values = new JSONObject();
				values.put("time", sdf.format(sDate));
				values.put("value", sDate.getTime());

				pageInfo.put(providerType, "");
				pageInfo.put("startDate", values.toString()); // Send stringified JSON to be parsed in component JS
			}
			info.put(providerType, pageInfo);
		} catch (RepositoryException e) {
			logger.error("error occured" + e);
		}

	}
}
