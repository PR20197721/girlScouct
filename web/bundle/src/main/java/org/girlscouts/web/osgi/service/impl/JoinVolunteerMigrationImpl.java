package org.girlscouts.web.osgi.service.impl;

import com.adobe.xfa.Bool;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.service.JoinVolunteerMigration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(service = {JoinVolunteerMigration.class}, immediate = true, name = "org.girlscouts.web.osgi.service.impl.JoinVolunteerMigrationImpl")
@Designate(ocd = JoinVolunteerMigrationImpl.Config.class)
public class JoinVolunteerMigrationImpl implements JoinVolunteerMigration {

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private String oldJoin = "";
    private String newJoin = "";

    private String oldVolunteer = "";
    private String newVolunteer = "";

    private String oldRenew = "";
    private String newRenew = "";

    @Activate
    private void activate(Config config) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        this.oldJoin = config.oldJoin();
        this.newJoin = config.newJoin();
        this.oldVolunteer = config.oldVolunteer();
        this.newVolunteer = config.newVolunteer();
        this.oldRenew = config.oldRenew();
        this.newRenew = config.newRenew();
        log.info("Activated.");
    }

    @Override
    public void migrateJoinLink(String path, boolean dryRun, Boolean addTargetValueToHref) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            log.debug("Path: "+path);
            Resource resource = rr.resolve(path);
            migrateNode(dryRun, resource, this.oldJoin, this.newJoin, addTargetValueToHref);
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

    @Override
    public void migrateVolunteerLink(String path, boolean dryRun, Boolean addTargetValueToHref) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            log.debug("Path: "+path);
            Resource resource = rr.resolve(path);
            migrateNode(dryRun, resource, this.oldVolunteer, this.newVolunteer,addTargetValueToHref);
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

    @Override
    public void migrateRenewLink(String path, boolean dryRun, Boolean addTargetValueToHref) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            log.debug("Path: "+path);
            Resource resource = rr.resolve(path);
            migrateNode(dryRun, resource, this.oldRenew, this.newRenew,addTargetValueToHref);
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

    private void migrateNode(boolean dryRun, Resource resource, String oldValue, String newValue, Boolean addTargetValueToHref) throws RepositoryException {
        Node node = resource.adaptTo(Node.class);
        final ValueFactory valueFactory = node.getSession().getValueFactory();
        PropertyIterator iterator = node.getProperties();
        boolean saveNeeded = false;
        while (iterator.hasNext()) {
            Property property = iterator.nextProperty();
            if (property.getType() == PropertyType.STRING) {
                try {
                    String value = property.getString();
                    if (value.contains(oldValue)) {
                        log.debug("before update: " + property.getName() + "=\n" + value);
                        value = updateValue(value, oldValue, newValue,addTargetValueToHref);
                        node.setProperty(property.getName(), value);
                        saveNeeded = true;
                        log.debug("after update: " + property.getName() + "=\n" + value);
                    }
                } catch (Exception e) {
                    log.error("possibly multivalued field: ", e);
                    try {
                        Value[] valueArray = property.getValues();
                        for (int i = 0; i < valueArray.length; i++) {
                            String value = valueArray[i].getString();
                            if (value.contains(oldValue)) {
                                log.debug("before update: " + property.getName() + "=\n" + value);
                                value = updateValue(value, oldValue, newValue,addTargetValueToHref);
                                valueArray[i] = valueFactory.createValue(value);
                                saveNeeded = true;
                                log.debug("after update: " + property.getName() + "=\n" + value);
                            }
                        }
                        node.setProperty(property.getName(), valueArray);
                    } catch (Exception e2) {
                        log.error("Exception occurred: ", e);
                    }
                }
            }
        }
        if (saveNeeded) {
            if (!dryRun) {
                log.debug("saving");
                node.getSession().save();
            } else {
                log.debug("Dry Run: Will not save!");
            }
        }
    }

    private String updateValue(String value, String oldURL, String newUrl, Boolean addTargetValueToHref){
        //exact match, possible plain text field
        if(value.trim().equals(oldURL)) {
            log.debug("Found exact match for old url: {}",oldURL);
            value = newUrl.trim();
        }else{
            //is html
            Pattern htmlPattern = Pattern.compile("</?[^>]+>");
            Matcher htmlMatcher = htmlPattern.matcher(value);
            if(htmlMatcher.find()) {
                log.debug("processing html");
                //value = value.replaceAll(oldURL, newUrl);
                log.debug("checking for pattern \"" + oldURL.replaceAll("/","\\/") + ".*?\"");
                Pattern pattern = Pattern.compile("\"" + oldURL.replaceAll("/","\\/") + ".*?\"");
                Matcher matcher = pattern.matcher(value);
                StringBuffer bufStr = new StringBuffer();
                while (matcher.find()) {
                    String matchedText = matcher.group();
                    log.debug("Replacing {} with {}", matchedText, "\"" + newUrl + "\"");
                    matcher.appendReplacement(bufStr, "\"" + newUrl + "\"");
                }
                matcher.appendTail(bufStr);
                value = bufStr.toString();
            }else{
                log.debug("processing textfield");
                String replacePattern = oldURL + ".*";
                value = value.replaceAll(replacePattern,newUrl);
            }
        }
        if(addTargetValueToHref){ // add target Value to the link
            value = value.replaceAll(newUrl,"target='_blank' "+newUrl);
        }
        
        return value;
    }

    @ObjectClassDefinition(name = "Join Volunteer Renew Migration Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Old Join URL", type = AttributeType.STRING) String oldJoin() default "https://girlscouts.secure.force.com/girl";
        @AttributeDefinition(name = "New Join URL", type = AttributeType.STRING) String newJoin() default "https://www.girlscouts.org/vs2tempjoin#join";
        @AttributeDefinition(name = "Old Volunteer URL", type = AttributeType.STRING) String oldVolunteer() default "https://girlscouts.secure.force.com";
        @AttributeDefinition(name = "New Volunteer URL", type = AttributeType.STRING) String newVolunteer() default "https://www.girlscouts.org/vs2tempjoin#volunteer";
        @AttributeDefinition(name = "Old Renew URL", type = AttributeType.STRING) String oldRenew() default "https://gsmembers.force.com/members/login";
        @AttributeDefinition(name = "New Renew URL", type = AttributeType.STRING) String newRenew() default "https://www.girlscouts.org/vs2temprenew";
    }

    public String getOldJoin() {
        return oldJoin;
    }

    public String getNewJoin() {
        return newJoin;
    }

    public String getOldVolunteer() {
        return oldVolunteer;
    }

    public String getNewVolunteer() {
        return newVolunteer;
    }

    public String getOldRenew() {
        return oldRenew;
    }

    public String getNewRenew() {
        return newRenew;
    }
}
