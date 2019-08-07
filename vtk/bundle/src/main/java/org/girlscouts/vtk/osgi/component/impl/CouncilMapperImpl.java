package org.girlscouts.vtk.osgi.component.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.component.ConfigListener;
import org.girlscouts.vtk.osgi.component.ConfigManager;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@Component
@Service
public class CouncilMapperImpl implements CouncilMapper, ConfigListener {
    public static final String DEFAULT_BRANCH = "/content/girlscouts-usa";
    private static final Logger log = LoggerFactory.getLogger(CouncilMapperImpl.class);
    private String defaultBranch;
    private Map<String, String> councilMap;
    @Reference
    private ConfigManager configManager;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    public void init() {
        configManager.register(this);
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
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
                    log.error("Malformatted council mapping record: " + mappings[i]);
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
        String branch = getCouncilBranch(id) + "/";
        String url = "";
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            url = rr.map(branch);
        } catch (Exception e) {
            log.error("Cannot get file node: " + branch + " due to ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return url;
    }

    public String getCouncilUrl() {
        return getCouncilUrl(null);
    }

    public String getCouncilName(String councilCode) {
        return councilMap.get(councilCode);
    }

}