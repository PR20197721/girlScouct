package org.girlscouts.web.osgi.component.impl;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.constants.PageReplicationConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.common.util.PageReplicationUtil;
import org.girlscouts.web.osgi.MuleSoftActivitiesConstants;
import org.girlscouts.common.osgi.component.GirlscoutsVtkConfigProvider;
import org.girlscouts.web.osgi.service.MulesoftActivitiesRestClient;
import org.girlscouts.web.rest.entity.mulesoft.ActivityEntity;
import org.girlscouts.web.rest.entity.mulesoft.PayloadEntity;
import org.girlscouts.web.service.replication.PageReplicator;
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
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_ORDERED_FOLDER;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = ImportEventsFromSolrCronImpl.Config.class)
public class ImportEventsFromSolrCronImpl implements Runnable, MuleSoftActivitiesConstants, PageReplicationConstants {
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
    @Reference
    private PageReplicator pageReplicator;
    @Reference
    protected Replicator replicator;
    @Reference
    CouncilCodeToPathMapper councilMapper;

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
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public void run() {
        if (isAuthor) {
            log.debug("Executing Solr Event import cron job");
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Date modifiedDate = readTimeStamp(rr);
                Calendar newModifiedDate = Calendar.getInstance();
                if (modifiedDate != null) {
                    String json = restClient.getEvents(modifiedDate);
                    ActivityEntity[] activities = new Gson().fromJson(json, ActivityEntity[].class);
                    if (activities != null && activities.length > 0) {
                        List<String> toActivate = new LinkedList<>();
                        List<String> toDelete = new LinkedList<>();
                        List<String> errorList = new LinkedList<>();
                        for (ActivityEntity activity : activities) {
                            if (activity.getPayload() != null) {
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
                                    errorList.add(activity.getAction() + "::" + activity.getPayload().getCouncilCode() + "::" + activity.getPayload().getTitle() + "::" + activity.getPayload().getId() + "::" + e.getMessage());
                                    log.error("Error Occurred while processing eid=" + activity.getPayload().getId() + ", title=" + activity.getPayload().getTitle(), e);
                                }
                            }
                        }
                        activateActivities(rr, toActivate, errorList);
                        if (errorList.size() == 0) {
                            try {
                                writeTimeStamp(rr, newModifiedDate);
                            } catch (Exception e1) {
                                log.error("Error occurred:", e1);
                            }
                        }
                        sendEmail(toActivate, toDelete, errorList, json, newModifiedDate);
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
            log.debug("Solr Event import cron job completed");
        }
    }

    private void activateActivities(ResourceResolver rr, List<String> toActivate, List<String> errorList) throws RepositoryException {
        if (toActivate != null && toActivate.size() > 0) {
            log.debug("creating delayed activation node for " + toActivate.size() + " activities");
            Node dateRolloutNode = PageReplicationUtil.getDateRolloutNode(rr.adaptTo(Session.class), rr, false);
            dateRolloutNode.setProperty(PARAM_CRAWL, true);
            dateRolloutNode.setProperty(PARAM_ACTIVATE, true);
            dateRolloutNode.setProperty(PARAM_UPDATE_REFERENCES, false);
            dateRolloutNode.setProperty(PARAM_STATUS, STATUS_CREATED);
            dateRolloutNode.setProperty(WORKFLOW_INITIATOR_NAME, "ActivityImportCronJob");
            dateRolloutNode.setProperty(PARAM_PAGES, toActivate.toArray(new String[toActivate.size()]));
            dateRolloutNode.getSession().save();
            log.debug("created instant activation node at " + dateRolloutNode.getPath());
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.info("calling RolloutTemplatePageService");
                        try {
                            pageReplicator.processReplicationNode(dateRolloutNode.getPath());
                        } catch (Exception e) {
                            PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                            log.error("Rollout Workflow encountered error: ", e);
                        }
                    }
                }).start();
            } catch (Exception e) {
                log.debug("Error occurred while processing replication at " + dateRolloutNode.getPath());
                errorList.add("Error occurred while processing replication at " + dateRolloutNode.getPath());
            }
        }
    }

    private void sendEmail(List<String> toActivate, List<String> toDelete, List<String> errorList, String json, Calendar date) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("<h1>Activated Activities</h1>");
            sb.append("<ul>");
            for (String activity : toActivate) {
                sb.append("<li>" + activity + "</li>");
            }
            sb.append("</ul>");
            sb.append("<h1>Deleted Activities</h1>");
            sb.append("<ul>");
            for (String activity : toDelete) {
                sb.append("<li>" + activity + "</li>");
            }
            sb.append("</ul>");
            DateFormat format = new SimpleDateFormat(MODIFIED_DATE_FORMAT);
            String jsonDate = format.format(date.getTime());
            Set<GSEmailAttachment> attachments = new HashSet<>();
            GSEmailAttachment attachment = new GSEmailAttachment("activities-"+jsonDate, json, null, GSEmailAttachment.MimeType.APPLICATION_JSON);
            attachments.add(attachment);
            if (errorList.size() == 0) {
                gsEmailService.sendEmail("GS Activities Sync Process Completed", emails, sb.toString(), attachments);
            } else {
                sb.append("<h1>Activities with Errors</h1>");
                sb.append("<ul>");
                for (String activity : errorList) {
                    sb.append("<li>" + activity + "</li>");
                }
                sb.append("</ul>");

                gsEmailService.sendEmail("GS Activities Sync Process Completed (with Errors)", emails, sb.toString(), attachments);

            }
        } catch (Exception e) {
            log.error("Unable to send email due to exception being thrown", e);
        }
    }

    private String createUpdateActivity(PayloadEntity payload, ResourceResolver rr) throws GirlScoutsException {
        try {
            if (StringUtils.isBlank(payload.getCouncilCode()) || StringUtils.isBlank(payload.getId()) || StringUtils.isBlank(payload.getTitle()) || StringUtils.isBlank(payload.getStart())) {
                throw new GirlScoutsException(null, "Required fields (councilCode/id/title/start) missing.");
            }
            String councilName = councilMapper.getCouncilPath(payload.getCouncilCode());
            if (councilName == null) {
                throw new GirlScoutsException(null, "No mapping found for council code: " + payload.getCouncilCode());
            }
            int year = getYear(payload);
            String parentPath = "/content/" + councilName + "/en/sf-events-repository/" + year;
            Resource activityYearFolder;
            try {
                log.debug("Getting activities year path: " + parentPath);
                activityYearFolder = ResourceUtil.getOrCreateResource(rr, parentPath, NT_SLING_ORDERED_FOLDER, null, true);
            } catch (Exception e) {
                log.error("Error occurred:", e);
                throw new GirlScoutsException(e, "Fail to get/create parent path: " + parentPath);
            }
            if (activityYearFolder != null) {
                Page activityPage = getEvent(parentPath, payload.getId(), rr);
                try {
                    if (activityPage == null) {
                        try {
                            String pageNodeTitle = payload.getTitle().replaceAll("[^a-zA-Z0-9]", "-");
                            activityPage = rr.adaptTo(PageManager.class).create(parentPath, null, "girlscouts/templates/event-page", pageNodeTitle, false);
                            log.info("Event page [path=" + activityPage.getPath() + "; eid=" + payload.getId() + "] created successfully.");
                        } catch (Exception e) {
                            log.error("Error occurred:", e);
                            throw new GirlScoutsException(e, "Fail to create event page under " + parentPath);
                        }
                    }
                    setActivityPageProperties(payload, activityPage, rr);
                    return activityPage.getPath();
                } catch (RepositoryException e) {
                    log.error("Error occurred:", e);
                    throw new GirlScoutsException(e, "Exception throw when adding data to" + activityPage.getPath() + "/jcr:content");
                }

            }
        } catch (Exception e) {
            log.error("Error occured while creating/updating activity: " + payload.getTitle());
            throw new GirlScoutsException(e, "Error occured while creating/updating activity: " + payload.getTitle());
        }
        return null;
    }

    private int getYear(PayloadEntity payload) throws GirlScoutsException {
        DateFormat ACTIVITY_DATE_FORMAT = new SimpleDateFormat(MuleSoftActivitiesConstants.ACTIVITY_DATE_FORMAT);
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

    private void setActivityPageProperties(PayloadEntity payload, Page activityPage, ResourceResolver rr) throws RepositoryException, GirlScoutsException {
        String councilTimezone = getCouncilSiteTimezone(payload, rr);
        Node jcrNode = activityPage.getContentResource().adaptTo(Node.class);
        Node dataNode = null;
        if (jcrNode.hasNode("data")) {
            dataNode = jcrNode.getNode("data");
        } else {
            dataNode = jcrNode.addNode("data");
        }
        if (dataNode != null) {
            //Title has to be on jcr:content node
            setStringProperty(jcrNode, "jcr:title", payload.getTitle());
            //Other attributes in jcr:content/data
            setCalendarProperty(dataNode, "start", getDateInAEMFormat(payload.getStart(), payload.getTimezone(), councilTimezone));
            setCalendarProperty(dataNode, "end", getDateInAEMFormat(payload.getEnd(), payload.getTimezone(), councilTimezone));
            setCalendarProperty(dataNode, "visibleDate", getDateInAEMFormat(payload.getVisibleDate(), payload.getTimezone(), councilTimezone));
            setStringProperty(dataNode, "eid", payload.getId());
            setStringProperty(dataNode, "address", payload.getAddress());
            setStringProperty(dataNode, "details", payload.getDetails());
            setStringProperty(dataNode, "locationLabel", payload.getLocationLabel());
            setStringProperty(dataNode, "srchdisp", payload.getSrchdisp());
            setBooleanProperty(dataNode, "memberOnly", payload.getMemberOnly());
            setStringProperty(dataNode, "timezone", payload.getTimezone());
            setStringProperty(dataNode, "register", payload.getRegister());
            setStringProperty(dataNode, "priceRange", payload.getPriceRange());
            if (StringUtils.isBlank(payload.getColor())) {
                setStringProperty(dataNode, "color", DEFAULT_ACTIVITY_COLOR);
            } else {
                setStringProperty(dataNode, "color", payload.getColor());
            }
            if (StringUtils.isBlank(payload.getThumbImage())) {
                setStringProperty(dataNode, "thumbImage", DEFAULT_ACTIVITY_THUMB);
            } else {
                setStringProperty(dataNode, "thumbImage", payload.getThumbImage());
            }
            if (StringUtils.isBlank(payload.getImage())) {
                setStringProperty(dataNode, "image", DEFAULT_ACTIVITY_IMAGE);
            } else {
                String imageVal = payload.getImage().replaceAll("https?:\\/\\/www\\.[^\\.]+\\.org", "");
                setStringProperty(dataNode, "imagePath", imageVal);
            }
            List<String> tags = payload.getTags();
            if (tags != null) {
                List<String> aemTags = new ArrayList<>();
                for (String tagStr : tags) {
                    String fullTagString = ACTIVITY_TAG_NAMESPACE + ":" + tagStr;
                    fullTagString = fullTagString.toLowerCase();
                    TagManager tagManager = rr.adaptTo(TagManager.class);
                    Tag tag = null;
                    try {
                        tag = tagManager.resolve(fullTagString);
                    } catch (Exception e) {
                        log.error("Error occurred while resolving a tag {}", fullTagString, e);
                    }
                    if (tag == null) {
                        try {
                            tag = tagManager.createTag(fullTagString, tagStr, tagStr);
                        } catch (Exception e) {
                            log.error("Error occurred while creating a tag {}", fullTagString, e);
                        }
                    }
                    if (tag != null) {
                        aemTags.add(fullTagString);
                    } else {
                        // if invalid tags found, throw exception and the event page is not saved
                        throw new GirlScoutsException(null, "Invalid Tag String: " + fullTagString + ", no such tag exist under /etc/tags");
                    }
                }
                if (aemTags != null && aemTags.size() > 0) {
                    String[] aemTagsArr = aemTags.toArray(new String[aemTags.size()]);
                    setStringArrProperty(dataNode, "cq:tags", aemTagsArr);
                }
            }
            log.info("Saving Activity:[path=" + activityPage.getPath() + "; eid=" + payload.getId() + "].");
            dataNode.getSession().save();
            log.info("Activity UPDATED:[path=" + activityPage.getPath() + "; eid=" + payload.getId() + "].");
        }
    }

    private String getCouncilSiteTimezone(PayloadEntity payload, ResourceResolver rr) {
        String councilTimezone = "";
        try {
            String councilName = councilMapper.getCouncilPath(payload.getCouncilCode());
            if (councilName != null && councilName.trim().length() > 0) {
                String councilSite = "/content/" + councilName;
                Resource councilSiteRes = rr.resolve(councilSite + "/en/jcr:content");
                if (councilSiteRes != null && !ResourceUtil.isNonExistingResource(councilSiteRes)) {
                    ValueMap vm = councilSiteRes.adaptTo(ValueMap.class);
                    if (vm.containsKey("timezone")) {
                        councilTimezone = vm.get("timezone", String.class);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: looking council timezone");
        }
        return councilTimezone;
    }

    private void setStringProperty(Node jcrNode, String name, String value) throws GirlScoutsException {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
            throw new GirlScoutsException(e, "Error occurred while setting " + name + "=" + value);
        }
    }

    private void setStringArrProperty(Node jcrNode, String name, String[] value) throws GirlScoutsException {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
            throw new GirlScoutsException(e, "Error occurred while setting " + name + "=" + value);
        }
    }

    private void setCalendarProperty(Node jcrNode, String name, Calendar value) throws GirlScoutsException {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
            throw new GirlScoutsException(e, "Error occurred while setting " + name + "=" + value);
        }
    }

    private void setBooleanProperty(Node jcrNode, String name, Boolean value) throws GirlScoutsException {
        try {
            jcrNode.setProperty(name, value);
        } catch (Exception e) {
            log.error("Error occurred while setting " + name + "=" + value);
            throw new GirlScoutsException(e, "Error occurred while setting " + name + "=" + value);
        }
    }

    public Calendar getDateInAEMFormat(String dateStr, String activityTimeZone, String councilSiteTimezone) throws GirlScoutsException {
        try {
            DateFormat ACTIVITY_DATE_FORMAT = new SimpleDateFormat(MuleSoftActivitiesConstants.ACTIVITY_DATE_FORMAT);
            String councilTimeZone = "America/New_York";//default
            if (!StringUtils.isBlank(activityTimeZone)) {
                councilTimeZone = activityTimeZone.substring(activityTimeZone.lastIndexOf("(") + 1, activityTimeZone.length() - 1);
            } else {
                if (!StringUtils.isBlank(councilSiteTimezone)) {
                    councilTimeZone = councilSiteTimezone;
                }
            }
            TimeZone timeZone = TimeZone.getTimeZone(councilTimeZone);
            Calendar date = Calendar.getInstance();
            date.setTime(ACTIVITY_DATE_FORMAT.parse(dateStr));
            date.setTimeZone(timeZone);
            return date;
        } catch (Exception e) {
            log.error("Error occurred:", e);
            throw new GirlScoutsException(e, "Error occurred parsing date " + dateStr + " for " + activityTimeZone);
        }
    }

    private String deleteActivity(PayloadEntity payload, ResourceResolver rr) throws GirlScoutsException {
        String deletedPath = payload.getTitle();
        try {
            String councilName = councilMapper.getCouncilPath(payload.getCouncilCode());
            if (councilName == null) {
                throw new GirlScoutsException(null, "No mapping found for council code: " + payload.getCouncilCode());
            }
            int year = getYear(payload);
            String parentPath = "/content/" + councilName + "/en/sf-events-repository/" + year;
            Page page = getEvent(parentPath, payload.getId(), rr);
            if (page != null) {
                try {
                    deletedPath = page.getPath();
                    replicator.replicate(rr.adaptTo(Session.class), ReplicationActionType.DEACTIVATE, page.getPath());
                    PageManager pageManager = rr.adaptTo(PageManager.class);
                    pageManager.delete(page, false, true);
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                    throw new GirlScoutsException(e, "Error occurred unpublishing activity " + page.getPath());
                }
            } else {
                log.debug("Could not locate activity with eid=" + payload.getId() + " in " + parentPath);
                throw new GirlScoutsException(null,"Could not locate activity with eid=" + payload.getId() +" in "+parentPath);
            }
        } catch (Exception e) {
            log.error("Error occured while deleting activity: eid=" + payload.getId() + ", title=" + payload.getTitle());
            throw new GirlScoutsException(e, "Error occurred deleting activity ");
        }
        return deletedPath;
    }

    protected boolean isAuthor() {
        log.info("Checking if running on author instance");
        if (settingsService.getRunModes().contains("author")) {
            return true;
        }
        return false;
    }

    private Page getEvent(String activityYearFolder, String id, ResourceResolver rr) {
        String sql = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE(s, '" + activityYearFolder + "') AND s.[jcr:content/data/eid]='" + id + "'";
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
            } else {
                DateFormat format = new SimpleDateFormat(AEM_DATE_FORMAT);
                return format.parse("2008-04-23T18:25:43.511Z");

            }
        } catch (Exception e) {
            log.error("Failed to read TimeStamp" + CONFIG_PATH);
        }
        return null;
    }

    private void writeTimeStamp(ResourceResolver rr, Calendar date) {
        try {
            DateFormat format = new SimpleDateFormat(AEM_DATE_FORMAT);
            log.debug("Updating modified timestamp to " + format.format(date.getTime()));
            Session session = rr.adaptTo(Session.class);
            Resource lastUpdNode = ResourceUtil.getOrCreateResource(rr, CONFIG_PATH, NT_UNSTRUCTURED, null, true);
            Node configNode = lastUpdNode.adaptTo(Node.class);
            configNode.setProperty(LAST_UPD_PROP_NAME, date);
            session.save();
            log.debug("Saved modified timestamp to " + format.format(date.getTime()));
        } catch (Exception e) {
            log.error("Failed to store the TimeStamp.", e);
        }
    }

}