package org.girlscouts.common.components.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GatedContentForm {

	@Self
	private Resource resource;

	@Inject
	private String remember;

	@Inject
	@Named("show-birthday")
	private String showBirthdayPopup;

	@Inject
	@Named("age-limit")
	private String ageLimit;

	@Inject
	@Named("privacy-policy")
	private String privacyPolicyUrl;

	@Inject
	@Named("expiration")
	private String cookieExpirationPeriod;

	@Inject
	@Named("salesforce-campaign-id")
	private String salesforceCampaignId;

	@Inject
	@Named("council-name")
	private String councilName;

	@Inject
	private Resource extensions;

	@Inject
	private Resource files;

	@Inject
	@Named("all-video-links")
	private String allVideoLinksEnabled;

	private Map<String, Object> attrMap = new HashMap<>();

	private List<String> extensionList = new ArrayList<>();

	private List<String> filesList = new ArrayList<>();

	@PostConstruct
	public void init() {

		attrMap.put("remember", remember);
		attrMap.put("ageLimit", ageLimit);
		attrMap.put("cookieExpirationPeriod", cookieExpirationPeriod);
		attrMap.put("salesforceCampaignId", salesforceCampaignId);
		attrMap.put("allVideoLinksEnabled", allVideoLinksEnabled);

		Iterator<Resource> extItr = extensions.listChildren();
		while (extItr.hasNext()) {
			extensionList.add(extItr.next().getValueMap().get("extension", String.class));
		}

		Iterator<Resource> filesItr = files.listChildren();
		while (filesItr.hasNext()) {
			String filePath = filesItr.next().getValueMap().get("file", String.class);
			if (null != filePath && StringUtils.isNotEmpty(filePath)) {
				if (StringUtils.startsWithIgnoreCase(filePath, "/content")) {
					if (filePath.lastIndexOf(".") != -1) {
						filesList.add(filePath);
					} else {
						filePath += ".html";
						filesList.add(filePath);
					}
				} else {
					filesList.add(filePath);
				}

			}
		}

	}

	public String getCouncilName() {
		return councilName;
	}

	public String getShowBirthdayPopup() {
		return showBirthdayPopup;
	}

	public String getPrivacyPolicyUrl() {
		return privacyPolicyUrl;
	}

	public Map<String, Object> getAttrMap() {
		return attrMap;
	}

	public List<String> getExtensionList() {
		return extensionList;
	}

	public List<String> getFilesList() {
		return filesList;
	}

}
