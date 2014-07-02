package org.girlscouts.vtk.impl.helpers;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.girlscouts.vtk.helpers.CouncilMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouncilMapperImpl implements CouncilMapper, ConfigListener {
    private static final Logger log = LoggerFactory.getLogger(CouncilMapperImpl.class);
    private String defaultBranch;
    private Map<String, String> councilMap;

    @SuppressWarnings("rawtypes")
    public void updateConfig(Dictionary configs) {
        defaultBranch = (String)configs.get("defaultBranch");        
        String[] mappings = (String[])configs.get("councilMapping");
        councilMap = new HashMap<String, String>();
        for (int i = 0; i < mappings.length; i++) {
            String[] configRecord = mappings[i].split(":");
            if (configRecord.length >= 2) {
                councilMap.put(configRecord[0], configRecord[1]);
            } else {
                log.error("Malformatted council mapping record: " + mappings[i]);
            }
        }
    }

    public String getCouncilBranch(String id) {
        return null;
    }

}