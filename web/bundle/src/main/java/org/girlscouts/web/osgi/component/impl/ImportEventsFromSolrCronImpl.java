package org.girlscouts.web.osgi.component.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.web.osgi.MuleSoftActivitiesConstants;
import org.girlscouts.web.osgi.service.MulesoftActivitiesRestClient;
import org.girlscouts.web.rest.entity.mulesoft.ActivityEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = ImportEventsFromSolrCronImpl.Config.class)
public class ImportEventsFromSolrCronImpl implements Runnable, MuleSoftActivitiesConstants {

    @ObjectClassDefinition(name="VTK Year Plan Data Check Cron Configuration", description = "VTK Year Plan Data Check Cron Configuration")
    public static @interface Config {
        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "0 0 3 * * ?";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default true;

        @AttributeDefinition(name = "Email Addresses", description = "Notification recipients")
        String[] emailAddresses() default  "dmitriy.bakum@ey.com";

    }

    private static Logger log = LoggerFactory.getLogger(ImportEventsFromSolrCronImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private GSEmailService gsEmailService;
    @Reference
    private MulesoftActivitiesRestClient restClient;
    @Reference
    protected SlingSettingsService settingsService;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    private String host = "";
    List<String> emails;
    boolean isAuthor = false;

    @Activate
    private void activate(ImportEventsFromSolrCronImpl.Config config) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        this.emails = Arrays.asList(config.emailAddresses());
        isAuthor = isAuthor();
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            this.host = addr.getHostName();

        } catch (UnknownHostException e) {
            log.error("Girl Scouts Solr Events import encountered error: ", e);
        }
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public void run() {
        if(isAuthor) {
            log.debug("Executing Solr Event import cron job");
            ResourceResolver rr = null;
            List<String> errorList = new ArrayList<>();
            Date modifiedDate = getLastModifiedDate();
            if(modifiedDate != null) {
                try {
                    ActivityEntity[] activities = restClient.getEvents(modifiedDate);
                    if (activities != null && activities.length > 0) {
                        rr = resolverFactory.getServiceResourceResolver(resolverParams);
                        for (ActivityEntity activity : activities) {
                            log.debug(activity.getTitle());
                        }
                    } else {
                        log.debug("No activities returned from MuleSoft API for modifiedDate=" + modifiedDate);
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
            }else{
                log.debug("Will not pull activities because last modified date is null");
            }
        }
    }

    private Date getLastModifiedDate() {
        try {
            Date date = dateFormat.parse("2008-04-23T18:25:43.511Z");
            return date;
        }catch(Exception e){
            log.error("Error occurred getting modifiedTime value");
        }
        return null;
    }

    public void emailEventImportRpt(List<String> events) {
        try {
                log.info("Sending Solr events import Notification");
                StringBuffer html = new StringBuffer();
                if(events.size() > 0){
                    html.append("<p>"+events.size()+" events have been imported : </p>");
                    html.append("<ol>");
                    for(String event:events){
                        html.append("<li>"+event+"</li>");
                    }
                    html.append("</ol>");
                    String subject = "Solr events import: Server " + host;
                    this.gsEmailService.sendEmail(subject, emails, html.toString());
                    log.info("Solr events import Notification email sent for {}", host);
                }
        } catch (Exception e) {
            log.error("Girl Scouts Solr events import email encountered error: ", e);
        }
    }

    protected boolean isAuthor() {
        log.info("Checking if running on author instance");
        if (settingsService.getRunModes().contains("author")) {
            return true;
        }
        return false;
    }

}