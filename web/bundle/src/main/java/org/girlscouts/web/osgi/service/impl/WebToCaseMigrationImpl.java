package org.girlscouts.web.osgi.service.impl;

import com.day.cq.wcm.foundation.forms.FormStructureHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.service.WebToCaseMigration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(service = {WebToCaseMigration.class}, immediate = true, name = "org.girlscouts.web.osgi.service.impl.WebToCaseMigrationImpl")
@Designate(ocd = WebToCaseMigrationImpl.Config.class)
public class WebToCaseMigrationImpl implements WebToCaseMigration {

    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private FormStructureHelperFactory formStructureHelperFactory;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Map<String, String> fieldNameMap = new HashMap<>();

    @Activate
    private void activate(ComponentContext context) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        String[] fieldNameMapping = {"00NG000000DdNmL::00N22000000ltnr","00NG000000DdNmN::00N22000000ltnt","00NG000000DdNmM::00N22000000ltns"};
        for(String map:fieldNameMapping){
            String[]pair = map.split("::");
            fieldNameMap.put(pair[0], pair[1]);
        }
        log.info("Activated.");
    }

    @Override
    public void migrateWebToCaseForm(String path, boolean dryRun) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            log.debug("Form: "+path);
            Resource formResource = rr.resolve(path);
            ModifiableValueMap formProps = formResource.adaptTo(ModifiableValueMap.class);
            FormStructureHelper formStructureHelper = formStructureHelperFactory.getFormStructureHelper(formResource);
            Iterable<Resource> iterable = formStructureHelper.getFormElements(formResource);
            Iterator it = iterable.iterator();
            while(it.hasNext()){
                Resource formField = (Resource)it.next();
                ModifiableValueMap fieldProps = formField.adaptTo(ModifiableValueMap.class);
                if(fieldProps.containsKey("name")) {
                    String fieldName = fieldProps.get("name", String.class);
                    if(fieldNameMap.containsKey(fieldName)) {
                        log.debug("Updating Field: "+formField.getPath()+", property:"+fieldName+" to "+ fieldNameMap.get(fieldName));
                        if(!dryRun) {
                            fieldProps.put("name", fieldNameMap.get(fieldName));
                        }
                    }
                }
            }
            if(!dryRun) {
                log.debug("saving");
                rr.commit();
            }else{
                log.debug("Dry Run: Will not save!");
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
    }

    @ObjectClassDefinition(name = "Web To Case Migration configuration")
    public @interface Config {

    }
}
