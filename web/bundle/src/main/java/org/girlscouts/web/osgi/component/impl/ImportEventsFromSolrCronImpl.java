package org.girlscouts.web.osgi.component.impl;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
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
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = ImportEventsFromSolrCronImpl.Config.class)
public class ImportEventsFromSolrCronImpl implements Runnable, MuleSoftActivitiesConstants {
    @ObjectClassDefinition(name = "Mulesoft events import job Cron Configuration")
    public static @interface Config {
        @AttributeDefinition(name = "Cron-job expression") String scheduler_expression() default "0 0 3 * * ?";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently") boolean scheduler_concurrent() default true;

        @AttributeDefinition(name = "Email Addresses", description = "Notification recipients") String[] emailAddresses() default "dmitriy.bakum@ey.com";

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
            for (String mapping : mappings) {
                try {
                    String[] configRecord = mapping.split("::");
                    if (configRecord.length >= 2) {
                        councilMap.put(configRecord[0], configRecord[1]);
                    } else {
                        log.error("Malformatted council mapping record: " + mapping);
                    }
                } catch (Exception e) {
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
        if (isAuthor) {
            log.debug("Executing Solr Event import cron job");
            ResourceResolver rr = null;
            List<String> errorList = new ArrayList<>();
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Date modifiedDate = readTimeStamp(rr);
                if (modifiedDate != null) {
                    ActivityEntity[] activities = restClient.getEvents(modifiedDate);
                    if (activities != null && activities.length > 0) {
                        List<String> toActivate = new LinkedList<>();
                        List<String> toDelete = new LinkedList<>();
                        for (ActivityEntity activity : activities) {
                            try {
                                log.debug("Processing action " + activity.getAction() + " for : " + activity.getPayload().toString());
                                if ("PUT".equals(activity.getAction())) {
                                    toActivate.add(createUpdateActivity(activity.getPayload(), rr));
                                } else {
                                    if ("DELETE".equals(activity.getAction())) {
                                        toDelete.add(deleteActivity(activity.getPayload(), rr));
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Error Occurred: ", e);
                            }
                        }
                    } else {
                        log.debug("No activities returned from MuleSoft API for modifiedDate=" + modifiedDate);
                    }
                } else {
                    log.debug("Will not pull activities because last modified date is null");
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
    }

    private String createUpdateActivity(PayloadEntity payload, ResourceResolver rr) {
        try {
            if (StringUtils.isBlank(payload.getCouncilCode()) || StringUtils.isBlank(payload.getId()) || StringUtils.isBlank(payload.getTitle()) || StringUtils.isBlank(payload.getStart())) {
                throw new GirlScoutsException(null, "Required fields (councilCode/id/title/start) missing.");
            }
            String councilName = councilMap.get(payload.getCouncilCode());
            if (councilName == null) {
                throw new GirlScoutsException(null, "No mapping found for council code: " + payload.getCouncilCode());
            }
            int year = getYear(payload);
            String parentPath = "/content/" + councilName + "/en/sf-events-repository/" + year;
            Resource activityYearFolder;
            try {
                activityYearFolder = ResourceUtil.getOrCreateResource(rr, parentPath, NT_PAGE, null, true);
            } catch (Exception e) {
                log.error("Error occurred:", e);
                throw new GirlScoutsException(e, "Fail to get/create parent path: " + parentPath);
            }
            if (activityYearFolder != null) {
                Page activityPage = getEvent(parentPath, payload.getId(), rr);
                try {
                    if (activityPage == null) {
                        try {
                            activityPage = rr.adaptTo(PageManager.class).create(parentPath, null, "girlscouts/templates/event-page", payload.getTitle(), false);
                            log.info("Event page [path=" + activityPage.getPath() + "; eid=" + payload.getId() + "] created successfully.");
                        } catch (Exception e) {
                            log.error("Error occurred:", e);
                            throw new GirlScoutsException(e, "Fail to create event page under " + parentPath);
                        }
                    }
                    setActivityPageProperties(payload, activityPage);
                    return activityPage.getPath();
                } catch (RepositoryException e) {
                    log.error("Error occurred:", e);
                    throw new GirlScoutsException(e, "Exception throw when adding data to" + activityPage.getPath() + "/jcr:content");
                }

            }
        } catch (Exception e) {
            log.error("Error occured while creating/updating activity: " + payload.toString());
        }
        return null;
    }

    private int getYear(PayloadEntity payload) throws GirlScoutsException {
        DateFormat ACTIVITY_DATE_FORMAT = new SimpleDateFormat(ACTIVITY_DATE_FORMAT_PATTERN);
        Calendar calendar = Calendar.getInstance();
        Date startDate;
        try {
            startDate = ACTIVITY_DATE_FORMAT.parse(payload.getStart());
        } catch (ParseException e) {
            log.error("Error occurred:", e);
            throw new GirlScoutsException(e, "start date format error: " + payload.getStart());
        }
        calendar.setTime(startDate);
        return calendar.get(Calendar.YEAR);
    }

    private void setActivityPageProperties(PayloadEntity payload, Page activityPage) throws RepositoryException, GirlScoutsException {
        Node jcrNode = activityPage.getContentResource().adaptTo(Node.class);
        setStringProperty(jcrNode, "jcr:title", payload.getTitle());
        setCalendarProperty(jcrNode, "start", getDateInAEMFormat(payload.getStart(), payload.getTimezone()));
        setCalendarProperty(jcrNode, "end", getDateInAEMFormat(payload.getEnd(), payload.getTimezone()));
        setCalendarProperty(jcrNode, "visibleDate", getDateInAEMFormat(payload.getVisibleDate(), payload.getTimezone()));
        setStringProperty(jcrNode, "eid", payload.getId());
        setStringProperty(jcrNode, "address", payload.getAddress());
        setStringProperty(jcrNode, "details", payload.getDetails());
        setStringProperty(jcrNode, "locationLabel", payload.getLocationLabel());
        setStringProperty(jcrNode, "srchdisp", payload.getSrchdisp());
        setBooleanProperty(jcrNode, "memberOnly", payload.getMemberOnly());
        setStringProperty(jcrNode, "timezone", payload.getTimezone());
        setStringProperty(jcrNode, "register", payload.getRegister());
        setStringProperty(jcrNode, "priceRange", payload.getPriceRange());
        if (StringUtils.isBlank(payload.getColor())) {
            setStringProperty(jcrNode, "color", DEFAULT_ACTIVITY_COLOR);
        } else {
            setStringProperty(jcrNode, "color", payload.getColor());
        }
        if (StringUtils.isBlank(payload.getThumbImage())) {
            setStringProperty(jcrNode, "thumbImage", DEFAULT_ACTIVITY_THUMB);
        } else {
            setStringProperty(jcrNode, "thumbImage", payload.getThumbImage());
        }
        if (StringUtils.isBlank(payload.getImage())) {
            setStringProperty(jcrNode, "image", DEFAULT_ACTIVITY_IMAGE);
        } else {
            String imageVal = payload.getImage().replaceAll("https?:\\/\\/www\\.[^\\.]+\\.org", "");
            setStringProperty(jcrNode, "imagePath", imageVal);
        }
        List<String> tags = payload.getTags();
        if (tags != null) {
            List<String> aemTags = new LinkedList<>();
            for (String tag : tags) {
                String tagString = ACTIVITY_TAG_NAMESPACE + ":" + tag;
                if (activityPage.adaptTo(TagManager.class).resolve(tagString) != null) {
                    aemTags.add(tagString);
                } else {
                    // if invalid tags found, throw exception and the event
                    // page is not saved
                    throw new GirlScoutsException(null, "Invalid Tag String: " + tagString + ", no such tag exist under /etc/tags");
                }
            }
            setStringArrProperty(jcrNode, "cq:tags", (String[]) aemTags.toArray());
        }
        jcrNode.getSession().save();
        log.info("Event UPDATED: Event page [path=" + activityPage.getPath() + "; eid=" + payload.getId() + "] updated successfully.");
    }

    private void setStringProperty(Node jcrNode, String name, String value) {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
        }
    }

    private void setStringArrProperty(Node jcrNode, String name, String[] value) {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
        }
    }

    private void setCalendarProperty(Node jcrNode, String name, Calendar value) {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
        }
    }

    private void setBooleanProperty(Node jcrNode, String name, Boolean value) {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
        }
    }

    public static Calendar getDateInAEMFormat(String dateStr, String activityTimeZone) {
        try {
            DateFormat ACTIVITY_DATE_FORMAT = new SimpleDateFormat(ACTIVITY_DATE_FORMAT_PATTERN);
            String councilTimeZone = "America/New_York";//default
            if (!StringUtils.isBlank(activityTimeZone)) {
                councilTimeZone = activityTimeZone.substring(activityTimeZone.lastIndexOf("(") + 1, activityTimeZone.length() - 1);
            }
            TimeZone timeZone = TimeZone.getTimeZone(councilTimeZone);
            ACTIVITY_DATE_FORMAT.setTimeZone(timeZone);
            Calendar date = Calendar.getInstance();
            date.setTime(ACTIVITY_DATE_FORMAT.parse(dateStr));
            return date;
        } catch (Exception e) {
            log.error("Error occurred:", e);
        }
        return null;

    }

    private String deleteActivity(PayloadEntity payload, ResourceResolver rr) {
        try {
        } catch (Exception e) {
            log.error("Error occured while deleting activity: " + payload.toString());
        }
        return null;
    }

    public void emailEventImportRpt(List<String> events) {
        try {
            log.info("Sending Solr events import Notification");
            StringBuffer html = new StringBuffer();
            if (events.size() > 0) {
                html.append("<p>" + events.size() + " events have been imported : </p>");
                html.append("<ol>");
                for (String event : events) {
                    html.append("<li>" + event + "</li>");
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

    private Page getEvent(String activityYearFolder, String id, ResourceResolver rr) {
        String sql = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE(s, '" + activityYearFolder + "') AND s.[jcr:content/eid]='" + id + "'";
        try {
            log.debug("Executing JCR-SQL2 query: " + sql);
            for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext(); ) {
                Resource activityPage = it.next();
                log.debug("Found existing activity page: " + activityPage.getPath());
                return activityPage.adaptTo(Page.class);
            }
        } catch (Exception e) {
            log.error("Error occured:" + e);
        }
        return null;
    }

    private Date readTimeStamp(ResourceResolver rr) {
        try {
            ValueMap props = rr.getResource(CONFIG_PATH).adaptTo(ValueMap.class);
            if (props.containsKey(LAST_UPD_PROP_NAME)) {
                return props.get(LAST_UPD_PROP_NAME, Date.class);
            }else{
                DateFormat DATE_FORMAT = new SimpleDateFormat(AEM_DATE_FORMAT_PATTERN);
                return DATE_FORMAT.parse("2008-04-23T18:25:43.511Z");

            }
        } catch (Exception e) {
            log.error("Failed to read TimeStamp" + CONFIG_PATH);
        }
        return null;
    }

    private void writeTimeStamp(ResourceResolver rr, Calendar date) {
        try {
            Session session = rr.adaptTo(Session.class);
            Resource lastUpdNode = ResourceUtil.getOrCreateResource(rr, CONFIG_PATH, NT_UNSTRUCTURED, null, true);
            Node configNode = lastUpdNode.adaptTo(Node.class);
            configNode.setProperty(LAST_UPD_PROP_NAME, date);
            session.save();
        } catch (Exception e) {
            log.error("Failed to store the TimeStamp.", e);
        }
    }

}