package org.girlscouts.web.webtolead.config;

import java.util.Dictionary;


import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.osgi.service.component.ComponentContext;

@Component(
		metatype = true,
		label = "Girlscouts Web To Lead configuration Service", 
		description = "Girlscouts Web To Lead configuration Service"
		)
@Service(value = {WebToLeadConfig.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts Web To Lead Configuration Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 

	@Property(name = "oid", label="Organization ID"),
	@Property(name = "campaignIDPrimaryName", label="Campaign ID Primary Name"),
	@Property(name = "campaignIDSecondaryName", label="Campaign ID Secondary Name"),
	@Property(name = "apiURL", label="Salesforce API URL path", description="Salesforce web-to-lead webservice URL")
})

public class WebToLeadConfigImpl implements WebToLeadConfig{

	
	private final String OID = "oid";
	private final String CAMPAIGN_ID_NAME_1 = "campaignIDPrimaryName";
	private final String CAMPAIGN_ID_NAME_2 = "campaignIDSecondaryName";
	private final String API_URL = "apiURL";
	
	
	private String oid;
	private String campaignIDPrimaryName;
	private String campaignIDSecondaryName;
	private String apiURL;
	
	@SuppressWarnings("deprecation")
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.oid=OsgiUtil.toString(configs.get(OID), null);
		this.campaignIDPrimaryName=OsgiUtil.toString(configs.get(CAMPAIGN_ID_NAME_1), null);
		this.campaignIDSecondaryName=OsgiUtil.toString(configs.get(CAMPAIGN_ID_NAME_2), null);
		this.apiURL=OsgiUtil.toString(configs.get(API_URL), null);
		
	}
	
	
	@Override
	public String getOID() {
		return this.oid;
	}

	@Override
	public String getCampaignIDPrimaryName() {
		return this.campaignIDPrimaryName;
	}

	@Override
	public String getCampaignIDSecondaryName() {
		return this.campaignIDSecondaryName;
	}

	@Override
	public String getAPIURL() {
		return this.apiURL;
	}
	
}
