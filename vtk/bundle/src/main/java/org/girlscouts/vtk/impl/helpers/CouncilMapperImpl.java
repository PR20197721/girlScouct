package org.girlscouts.vtk.impl.helpers;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class CouncilMapperImpl implements CouncilMapper, ConfigListener {
	private static final Logger log = LoggerFactory
			.getLogger(CouncilMapperImpl.class);
	public static final String DEFAULT_BRANCH = "/content/girlscouts-usa";
	private String defaultBranch;
	private Map<String, String> councilMap;
	private ResourceResolver resourceResolver;

	@Reference
	private ConfigManager configManager;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Activate
	public void init() {
		configManager.register(this);
		try {
			resourceResolver = resourceResolverFactory
					.getResourceResolver(null);
		} catch (LoginException e) {
			log.error("Cannot get ResourceResolver.");
		}
	}

	@SuppressWarnings("rawtypes")
	public void updateConfig(Dictionary configs) {
		defaultBranch = (String) configs.get("defaultBranch");
		if (defaultBranch == null) {
			defaultBranch = DEFAULT_BRANCH;
			log.error("Default mapping is null. Use /content/girlscouts-usa");
		}

		String[] mappings = (String[]) configs.get("councilMapping");
		councilMap = new HashMap<String, String>();
		if (mappings != null) {
			for (int i = 0; i < mappings.length; i++) {
				String[] configRecord = mappings[i].split("::");
				if (configRecord.length >= 2) {
					councilMap.put(configRecord[0], configRecord[1]);
				} else {
					log.error("Malformatted council mapping record: "
							+ mappings[i]);
				}
			}
		} else {
			log.warn("No mappings set");
		}
	}

	public String getCouncilBranch(String id) {
		String branch = null;
		if (id != null) {
			branch = councilMap.get(id);
		}
		if (branch == null) {
			branch = defaultBranch;
		}
		return "/content/" + branch;
	}

	public String getCouncilBranch() {
		return getCouncilBranch(null);
	}

	public String getCouncilUrl(String id) {
		String branch = getCouncilBranch(id);
		String url = resourceResolver.map(branch + "/");
		return url;
	}

	public String getCouncilUrl() {
		return getCouncilUrl(null);
	}
	
	public String getCouncilName(String councilCode){
		return councilMap.get(councilCode);
	}

}