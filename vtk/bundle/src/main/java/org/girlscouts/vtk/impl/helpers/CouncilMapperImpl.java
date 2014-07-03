package org.girlscouts.vtk.impl.helpers;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class CouncilMapperImpl implements CouncilMapper, ConfigListener {
    private static final Logger log = LoggerFactory.getLogger(CouncilMapperImpl.class);
    private Record defaultRecord;
    private Map<String, Record> councilMap;

    @Reference
    ConfigManager configManager;
    
    @Activate
    public void init() {
        configManager.register(this);
    }
    
    private static class Record {
        private String branch;
        private String url;

        Record(String branch, String url) {
            this.branch = branch;
            this.url = url;
        }
        
        String getBranch() {
            return branch;
        }
        
        String getUrl() {
            return url;
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void updateConfig(Dictionary configs) {
        String defaultMapping = (String)configs.get("defaultMapping");        
        if (defaultMapping != null) {
            String[] mappingCols = defaultMapping.split("::");
            if (mappingCols.length >= 2) {
                defaultRecord = new Record(mappingCols[0], mappingCols[1]);
            } else {
                log.error("Malformatted default mapping: " + defaultMapping);
            }
        } else {
            log.error("Default mapping is null.");
        }

        String[] mappings = (String[])configs.get("councilMapping");
        councilMap = new HashMap<String, Record>();
        if (mappings != null) {
            for (int i = 0; i < mappings.length; i++) {
                String[] configRecord = mappings[i].split("::");
                if (configRecord.length >= 3) {
                    councilMap.put(configRecord[0], new Record(configRecord[1], configRecord[2]));
                } else {
                    log.error("Malformatted council mapping record: " + mappings[i]);
                }
            }
        } else {
            log.warn("No mappings set");
        }
    }

    private Record getCouncilRecord(String id) {
        Record record = null;
        if (id != null) {
            record = councilMap.get(id);
        }
        if (record == null) {
            record = defaultRecord;
        }
        return record;
    }

    public String getCouncilBranch(String id) {
        return "/content/" + getCouncilRecord(id).getBranch();
    }

    public String getCouncilBranch() {
        return getCouncilBranch(null);
    }
    
    public String getCouncilUrl(String id) {
        return getCouncilRecord(id).getUrl();
    }

    public String getCouncilUrl() {
        return getCouncilUrl(null);
    }
    
}