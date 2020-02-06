package org.girlscouts.common.osgi.cron.impl;

import org.apache.sling.api.resource.*;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.constants.TrashcanConstants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = TrashcanCleanUpProcess.Config.class)
public class TrashcanCleanUpProcess implements Runnable, TrashcanConstants {

    private static Logger log = LoggerFactory.getLogger(TrashcanCleanUpProcess.class);

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected SlingSettingsService settingsService;

    protected Map<String, Object> serviceParams;
    @Reference
    private Scheduler scheduler;

    private int schedulerID;

    boolean isAuthor = false;

    @Activate
    private void activate(TrashcanCleanUpProcess.Config config) {
        schedulerID = config.schedulerName().hashCode();
        this.serviceParams = new HashMap<String, Object>();
        this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        log.info("Girl Scouts Trashcan Clean Up Service Activated.");
        isAuthor = isAuthor();
    }
    @Modified
    protected void modified(TrashcanCleanUpProcess.Config config) {
        removeScheduler();
        schedulerID = config.schedulerName().hashCode(); // update schedulerID
        addScheduler(config);
    }
    @Deactivate
    protected void deactivate(TrashcanCleanUpProcess.Config config) {
        removeScheduler();
    }
    /**
     * Remove a scheduler based on the scheduler ID
     */
    private void removeScheduler() {
        log.debug("Removing Scheduler Job '{}'", schedulerID);
        scheduler.unschedule(String.valueOf(schedulerID));
    }
    /**
     * Add a scheduler based on the scheduler ID
     */
    private void addScheduler(TrashcanCleanUpProcess.Config config) {
        if (config.serviceEnabled()) {
            ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
            sopts.name(String.valueOf(schedulerID));
            sopts.canRunConcurrently(false);
            scheduler.schedule(this, sopts);
            log.debug("Scheduler added succesfully");
        } else {
            log.debug("TrashcanCleanUpProcessImpl is Disabled, no scheduler job created");
        }
    }
    @Override
    public void run() {
        log.info("Running Girl Scouts Trashcan Clean Up");
        if (!isAuthor) {
            log.error("Publisher instance - will not execute Girl Scouts Trashcan Clean Up");
            return;
        }else{
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(this.serviceParams);
                Resource assetsTrashCan = rr.resolve(ASSET_TRASHCAN_PATH);
                Resource pagesTrashCan = rr.resolve(PAGE_TRASHCAN_PATH);
                cleanUpExpiredTrash(assetsTrashCan);
                cleanUpExpiredTrash(pagesTrashCan);
            } catch (LoginException e) {
                log.error("encountered error: ", e);
            } finally {
                if(rr != null){
                    try {
                        rr.close();
                    } catch (Exception e) {
                    }
                }
            }
            log.error("Finished Girl Scouts Trashcan Clean Up");
        }
    }

    private void cleanUpExpiredTrash(Resource trashcan) {
        Iterator<Resource> siteFolders = trashcan.listChildren();
        Calendar deleteBeforeDate = GregorianCalendar.getInstance();
        List<String> listToDelete = new LinkedList<>();
        while(siteFolders.hasNext()){
            Resource siteFolder = siteFolders.next();
            Iterator<Resource> trashedPages = siteFolder.listChildren();
            while(trashedPages.hasNext()){
                Resource trashedPage = trashedPages.next();
                Resource content = trashedPage.getChild("jcr:content");
                ValueMap vm = content.getValueMap();
                Calendar trashCannedDate = vm.get("trashcan-move-date", Calendar.class);
                deleteBeforeDate.add(Calendar.MONTH, -6);
                deleteBeforeDate.setTimeZone(trashCannedDate.getTimeZone());
                log.debug("Checking if item was moved to trashcan before "+deleteBeforeDate.getTime());
                if(trashCannedDate != null && trashCannedDate.before(deleteBeforeDate)){
                    listToDelete.add(trashedPage.getPath());
                    log.debug("Added "+trashedPage.getPath()+" to delete list");
                }
            }
        }
        if(listToDelete != null & listToDelete.size()>0) {
            Session session = trashcan.getResourceResolver().adaptTo(Session.class);
            int count = 0;
            for (String deleteItem : listToDelete) {
                try {
                    log.debug("Deleting "+deleteItem);
                    session.removeItem(deleteItem);
                    count ++;
                } catch (RepositoryException e) {
                    log.error("Error occurred while deleting "+deleteItem, e);
                }
                if(count > 100){
                    try {
                        session.save();
                    } catch (RepositoryException e) {
                        log.error("Error occurred: ", e);
                    }
                }
            }
            try {
                session.save();
            } catch (RepositoryException e) {
                log.error("Error occurred: ", e);
            }
        }
    }

    protected boolean isAuthor() {
        log.info("Checking if running on author instance");
        if (settingsService.getRunModes().contains("author")) {
            return true;
        }
        return false;
    }

    @ObjectClassDefinition(name = "Girl Scouts Trashcan Cleanup Process Configuration", description = "Girl Scouts Trashcan Cleanup Process Configuration")
    public static @interface Config {
        /**
         * schedulerName
         * @return String name
         */
        @AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
        public String schedulerName() default "Girl Scouts Trashcan Cleanup Process";
        /**
         * schedulerConcurrent
         * @return schedulerConcurrent
         */
        @AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
        boolean schedulerConcurrent() default false;
        /**
         * serviceEnabled
         * @return serviceEnabled
         */
        @AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
        boolean serviceEnabled() default true;
        /**
         * schedulerExpression
         * @return schedulerExpression
         */
        @AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run every night at 2am.", type = AttributeType.STRING)
        String schedulerExpression() default "0 0 2 * * ?";
    }
}
