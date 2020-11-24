package org.girlscouts.common.osgi.component;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
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

    void sendEmail(SlingHttpServletRequest request, List<NameValuePair> data, List<String> errors, boolean debug);
}
