package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component(service = {VTKDataMigrationUtil.class}, immediate = true, name = "org.girlscouts.vtk.osgi.component.util.VTKDataMigrationUtil")
@Designate(ocd = VTKDataMigrationUtil.VTKDataMigrationUtilConfiguration.class)
public class VTKDataMigrationUtil{

    @ObjectClassDefinition(name = "Girl Scouts VTK VS 2.0 Data Migration Configuration")
    public @interface VTKDataMigrationUtilConfiguration {
        @AttributeDefinition(name = "Path to Troop Id Mapping File", type = AttributeType.STRING) String troopIdMappingFile() default "/content/dam/vtk-vs2-data-migration/troop-id.csv";

        @AttributeDefinition(name = "Path to User Id Mapping File", type = AttributeType.STRING) String userIdMappingFile() default "/content/dam/vtk-vs2-data-migration/user-id.csv";

    }

    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    private String troopIdMappingFile;
    private String userIdMappingFile;

    private Map<String, String> troopIdMapping = new HashMap<String, String>();
    private Map<String, String> userIdMapping = new HashMap<String, String>();
    private Map<String, String> contactIdMapping = new HashMap<String, String>();

    @Activate
    void activate(VTKDataMigrationUtilConfiguration config) {
        this.troopIdMappingFile = config.troopIdMappingFile();
        this.userIdMappingFile = config.userIdMappingFile();
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Resource csvResource = rr.resolve(this.troopIdMappingFile);
            if(csvResource != null && !ResourceUtil.isNonExistingResource(csvResource)){
                Asset asset = csvResource.adaptTo(Asset.class);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(asset.getRendition("original").getStream()));
                    String line = "";
                    String cvsSplitBy = ",";
                    while ((line = br.readLine()) != null) {
                        String[] mappingArray = line.split(cvsSplitBy);
                        if(mappingArray != null && mappingArray.length >=2){
                            troopIdMapping.put(mappingArray[0],mappingArray[1]);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred:",e);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            log.error("Error occurred:",e);
                        }
                    }
                }
                log.debug("Loaded {} mappings for troop id", troopIdMapping.size());
            }
            csvResource = rr.resolve(this.userIdMappingFile);
            if(csvResource != null && !ResourceUtil.isNonExistingResource(csvResource)){
                Asset asset = csvResource.adaptTo(Asset.class);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(asset.getRendition("original").getStream()));
                    String line = "";
                    String cvsSplitBy = ",";
                    while ((line = br.readLine()) != null) {
                        String[] mappingArray = line.split(cvsSplitBy);
                        if(mappingArray != null && mappingArray.length >=3){
                            userIdMapping.put(mappingArray[0],mappingArray[2]);
                            contactIdMapping.put(mappingArray[1],mappingArray[2]);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred:",e);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            log.error("Error occurred:",e);
                        }
                    }
                }
                log.debug("Loaded {} mappings for user id", userIdMapping.size());
                log.debug("Loaded {} mappings for contact id", contactIdMapping.size());
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        log.info("Girl Scouts VTK VS 2.0 Data Migration activated.");
    }

    public Map<String,String> getTroopIdMapping(){
        return this.troopIdMapping;
    }

    public Map<String,String> getUserIdMapping(){
        return this.userIdMapping;
    }

    public Map<String, String> getContactIdMapping() {
        return this.contactIdMapping;
    }
}