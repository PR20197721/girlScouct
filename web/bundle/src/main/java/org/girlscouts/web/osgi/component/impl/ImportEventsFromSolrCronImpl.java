package org.girlscouts.web.osgi.component.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.web.osgi.MuleSoftActivitiesConstants;
import org.girlscouts.web.osgi.component.GirlscoutsVtkConfigProvider;
import org.girlscouts.web.osgi.service.MulesoftActivitiesRestClient;
import org.girlscouts.web.rest.entity.mulesoft.ActivityEntity;
import org.girlscouts.web.rest.entity.mulesoft.PayloadEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = ImportEventsFromSolrCronImpl.Config.class)
public class ImportEventsFromSolrCronImpl implements Runnable, MuleSoftActivitiesConstants {

    @ObjectClassDefinition(name="Mulesoft events import job Cron Configuration")
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
    @Reference
    private GirlscoutsVtkConfigProvider vtkConfigProvider;

    private Map<String, String> councilMap;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    private String host = "";
    private List<String> emails;
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
        councilMap = new HashMap<String, String>();
        String[] mappings = vtkConfigProvider.getCouncilMapping();
        if (mappings != null) {
            for (String mapping: mappings) {
                try {
                    String[] configRecord = mapping.split("::");
                    if (configRecord.length >= 2) {
                        councilMap.put(configRecord[0], configRecord[1]);
                    } else {
                        log.error("Malformatted council mapping record: " + mapping);
                    }
                }catch(Exception e){
                    log.error("Girl Scouts Solr Events import encountered error: ", e);
                }
            }
        } else {
            log.warn("No mappings set");
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
                        List<String> toActivate = new LinkedList<>();
                        List<String> toDelete = new LinkedList<>();
                        rr = resolverFactory.getServiceResourceResolver(resolverParams);
                        for (ActivityEntity activity : activities) {
                            try {
                                log.debug("Processing action "+activity.getAction()+" for : "+activity.getPayload().toString());
                                if ("PUT".equals(activity.getAction())) {
                                    toActivate.add(createUpdateActivity(activity.getPayload()));
                                } else {
                                    if ("DELETE".equals(activity.getAction())) {
                                        toDelete.add(deleteActivity(activity.getPayload()));
                                    }
                                }

                            }catch(Exception e){
                                log.error("Error Occurred: ", e);
                            }
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

    private String createUpdateActivity(PayloadEntity payload) {
        try{
            String councilCode = payload.getCouncilCode();
            String id = payload.getId();
            String title = payload.getTitle();
            String start = payload.getStart();
            if (StringUtils.isBlank(councilCode)|| StringUtils.isBlank(id) || StringUtils.isBlank(title) || StringUtils.isBlank(start)) {
                throw new GirlScoutsException(null, "Required fields (councilCode/id/title/start) missing.");
            }
            String councilName = councilMap.get(councilCode);
            if (councilName == null) {
                throw new GirlScoutsException(null, "No mapping found for council code: " + councilCode);
            }
            Calendar calendar = Calendar.getInstance();
            Date startDate;
            try {
                startDate = NO_TIMEZONE_FORMAT.parse(start);
            } catch (ParseException e) {
                errorString.append("<p>Failed to parse date: " + start + "</p>");
                e.printStackTrace();
                throw new GirlScoutsException(e,"start date format error: " + start);
            }
        }catch(Exception e){
           log.error("Error occured while creating/updating activity: "+payload.toString());
        }
        return null;
    }
    private String deleteActivity(PayloadEntity payload) {
        try{

        }catch(Exception e){
            log.error("Error occured while deleting activity: "+payload.toString());
        }
        return null;
    }

    private Date getLastModifiedDate() {
        try {
            Date date = DATE_FORMAT.parse("2008-04-23T18:25:43.511Z");
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

    private Page getEvent(String path, String id, ResourceResolver rr) {
        String sql = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, '" + path + "') AND [eid]='" + id + "'";
        try {
            for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
                return it.next().getParent().getParent().adaptTo(Page.class);
            }
        } catch (Exception e) {
            log.error("Error occured:"+e);
        }
        return null;
    }

    private void readTimeStamp(ResourceResolver rr) {
        try {
            ValueMap props = rr.getResource(CONFIG_PATH).adaptTo(ValueMap.class);
            if (props.containsKey(LAST_UPD)) {
                Date date = props.get(LAST_UPD, Date.class);
                lastUpdated = date==null ? lastUpdated : date;
                log.info("Last import time stamp=" + ZIP_DATE_FORMAT.format(lastUpdated));
            }
        } catch (Exception e) {
            log.error("Failed to read " + CONFIG_PATH);
        }
    }

    private void writeTimeStamp(ResourceResolver rr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastUpdated);
        try {
            Session session = rr.adaptTo(Session.class);
            Node configNode = JcrUtil.createPath(CONFIG_PATH, "nt:unstructured", session);
            configNode.setProperty(LAST_UPD, calendar);
            session.save();
            log.info("The last updated timestamp has been set as " + ZIP_DATE_FORMAT.format(lastUpdated) + " and written to "
                    + CONFIG_PATH);
        } catch (Exception e) {
            errorString.append("<p>Failed to store the timestamp.</p>");
            log.error("Failed to store the timeStamp.", e);
        }
    }

}