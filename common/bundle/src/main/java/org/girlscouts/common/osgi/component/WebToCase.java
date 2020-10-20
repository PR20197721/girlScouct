package org.girlscouts.common.osgi.component;

import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;

public interface WebToCase {

	public String getOID();

	public String getApiURL();

	public Map<String, String> getRecaptchaMap();

	public Set<String> getExpectedParams();

	public boolean isSendEmail();

	public String getCaseSource();

    public String getTier();

    public String getRecordType();

	public int sendEmail(SlingHttpServletRequest request, Dictionary<String, Object> properties,
			CouncilCodeToPathMapper councilCodeToPathMapper);

}
