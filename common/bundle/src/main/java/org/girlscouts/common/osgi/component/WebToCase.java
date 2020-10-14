package org.girlscouts.common.osgi.component;

import java.util.Map;
import java.util.Set;

public interface WebToCase {

	public String getOID();

	public String getApiURL();

	public Map<String, String> getRecaptchaMap();

	public Set<String> getExpectedParams();

	public boolean isSendEmail();

	public String getCouncilEmailMapPath();
}
