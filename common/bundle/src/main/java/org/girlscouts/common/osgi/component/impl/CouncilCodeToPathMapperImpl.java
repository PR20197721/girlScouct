package org.girlscouts.common.osgi.component.impl;

import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Component(service = {
		CouncilCodeToPathMapper.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.CouncilCodeToPathMapperImpl")
@Designate(ocd = CouncilCodeToPathMapperImpl.Config.class)
public class CouncilCodeToPathMapperImpl implements CouncilCodeToPathMapper {

	private static final Logger log = LoggerFactory.getLogger(CouncilCodeToPathMapperImpl.class);

    private HashMap<String,String> councilCodeToPath;
    private HashMap<String,String> pathToCouncilCode;


	@Activate
	private void activate(Config config) {
        councilCodeToPath = new HashMap<String, String>();
        pathToCouncilCode = new HashMap<String, String>();
        String[] mappings = config.councilMapping();
        if (mappings != null) {
            for (String mapping : mappings) {
                try {
                    String[] configRecord = mapping.split("::");
                    if (configRecord.length >= 2) {
                        councilCodeToPath.put(configRecord[0], configRecord[1]);
                        pathToCouncilCode.put("/content/"+configRecord[1], configRecord[0]);
                    } else {
                        log.error("Malformatted council mapping record: " + mapping);
                    }
                } catch (Exception e) {
                    log.error("Encountered error: ", e);
                }
            }
        } else {
            log.warn("No mappings set");
        }
        log.info("Activated.");
	}

    @Override
    public String getCouncilCode(String path) {
	    if(pathToCouncilCode != null && pathToCouncilCode.size() != 0){
	        return pathToCouncilCode.get(path);
        }
        return null;
    }

    @Override
    public String getCouncilPath(String code) {
        if(councilCodeToPath != null && councilCodeToPath.size() != 0){
            return councilCodeToPath.get(code);
        }
        return null;
    }

    @Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Deactivated.");
	}



    @ObjectClassDefinition(name = "Girl Scouts Council Code to Path Mapping")
    public @interface Config {

        @AttributeDefinition(name = "Council Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway")
        String[] councilMapping();
    }

}
