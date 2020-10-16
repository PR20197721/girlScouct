package org.girlscouts.common.osgi.component;

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;

public interface WebToCase {

	public String getOID();

	public String getApiURL();

	public Map<String, String> getRecaptchaMap();

	public Set<String> getExpectedParams();

	public boolean isSendEmail();

	public int sendEmail(SlingHttpServletRequest request, Dictionary<String, Object> properties,
			CouncilCodeToPathMapper councilCodeToPathMapper);

}
