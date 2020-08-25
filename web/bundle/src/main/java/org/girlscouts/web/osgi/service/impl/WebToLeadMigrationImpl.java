package org.girlscouts.web.osgi.service.impl;

import com.day.cq.wcm.foundation.forms.FormStructureHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.service.WebToLeadMigration;
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

@Component(service = {WebToLeadMigration.class}, immediate = true, name = "org.girlscouts.web.osgi.service.impl.WebToLeadMigrationImpl")
@Designate(ocd = WebToLeadMigrationImpl.Config.class)
public class WebToLeadMigrationImpl  implements WebToLeadMigration {

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
        String[] fieldNameMapping = {"00N0f00000Eoc8W::GirlAge","00N0f00000Eoc8X::GirlFirstName","00N0f00000Eoc8Y::GirlLastName","00N0f00000Eoc8Z::Grade","00NG000000FHH1I::Alum","00NG000000FHH2f::University","city::City","email::Email","email_address::Email","first-name::FirstName","first_name::FirstName","firstname::FirstName","girl-first-name::GirlFirstName","girl-last-name::GirlLastName","Girl_Age__c::GirlAge","girl_first_name::GirlFirstName","girl_last_name::GirlLastName","girlfirstname::GirlFirstName","girllastname::GirlLastName","grade::Grade","Grade__c::Grade","grade_c::Grade","las_name::LastName","last-name::LastName","last_name::LastName","lastname::LastName","phone::Phone","Phone-Number::Phone","Phone_Number::Phone","phonenumber::Phone","School::SchoolName","school::SchoolName","School-Attending::SchoolName","School__c::SchoolName","schoolattending::SchoolName","state::State","title::Title","University_Name::University","universityname::University","zip::ZipCode","zip_code::ZipCode"};
        for(String map:fieldNameMapping){
            String[]pair = map.split("::");
            fieldNameMap.put(pair[0], pair[1]);
        }
        log.info("Activated.");
    }

    @Override
    public void migrateWebToLeadForm(String path, boolean dryRun) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            log.debug("node="+path);
            Resource formResource = rr.resolve(path);
            log.debug("formResource="+formResource);
            FormStructureHelper formStructureHelper = formStructureHelperFactory.getFormStructureHelper(formResource);
            log.debug("formStructureHelper="+formStructureHelper);
            Iterable<Resource> iterable = formStructureHelper.getFormElements(formResource);
            Iterator it = iterable.iterator();
            while(it.hasNext()){
                Resource formField = (Resource)it.next();
                ModifiableValueMap fieldProps = formField.adaptTo(ModifiableValueMap.class);
                fieldProps.put("leadType","General");
                if(fieldProps.containsKey("name")) {
                    String fieldName = fieldProps.get("name", String.class);
                    if(fieldNameMap.containsKey(fieldName)) {
                        if(!dryRun) {
                            log.debug("Updating "+fieldName+" to "+ fieldNameMap.get(fieldName));
                            fieldProps.put("name", fieldNameMap.get(fieldName));
                        }
                    }
                }
                if(!dryRun) {
                    formField.getResourceResolver().commit();
                }
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

    @ObjectClassDefinition(name = "MuleSoft Activities Rest client configuration", description = "MuleSoft Activities Rest client configuration")
    public @interface Config {

    }
}
