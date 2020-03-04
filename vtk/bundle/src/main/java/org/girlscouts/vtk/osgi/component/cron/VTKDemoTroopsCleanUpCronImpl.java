package org.girlscouts.vtk.osgi.component.cron;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.component.ConfigManager;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.mail.internet.InternetAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = VTKDemoTroopsCleanUpCronImpl.Config.class)
public class VTKDemoTroopsCleanUpCronImpl implements Runnable {

    @ObjectClassDefinition(name="VTK Demo Troops Clean Up Cron Configuration",
            description = "VTK Demo Troops Clean Up Cron Configuration")
    public static @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "0 30 3 ? * *";

        @AttributeDefinition(name = "Concurrent task",
                description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default true;

    }


    private static Logger log = LoggerFactory.getLogger(VTKDemoTroopsCleanUpCronImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    @Reference
    private MessageGatewayService messageGatewayService;
    @Reference
    private ConfigManager configManager;



    @Activate
    private void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public void run() {
        log.debug("Executing VTK Demo troops clean up cron job");
        removeDemoTroops();
    }

    private void removeDemoTroops() {
        ResourceResolver rr = null;
        try {
            String sql = "SELECT * FROM [nt:unstructured] as s where ISDESCENDANTNODE([" + VtkUtil.getYearPlanBase(null, null) + "]) and s.[sfTroopId] like 'SHARED_%' and s.[ocm_classname]='org.girlscouts.vtk.ocm.TroopNode'";
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.JCR_SQL2);
            QueryResult result = q.execute();
            NodeIterator itr = result.getNodes();
            while (itr.hasNext()) {
                try {
                    Node node = itr.nextNode();

                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DATE, -2);
                    Date yesterdayDate = yesterday.getTime();

                    Calendar cal = node.getProperty("lastModifiedDate").getDate();
                    Date eventDate = cal.getTime();

                    if (eventDate.before(yesterdayDate) || eventDate.equals(yesterdayDate)) {
                        session.removeItem(node.getPath());
                    }
                } catch (Exception e) {
                    log.error("Error Occurred: ", e);
                }
            }
            session.save();
            emailCronRpt(null);
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

    public void emailCronRpt(String msg) {
        try {
            MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
            String DEMO_CRON_EMAIL = configManager.getConfig("DEMO_CRON_EMAIL");
            HtmlEmail email = new HtmlEmail();
            java.util.List<InternetAddress> toAddresses = new java.util.ArrayList();
            toAddresses.add(new InternetAddress(DEMO_CRON_EMAIL));
            toAddresses.add(new InternetAddress("dmitriy.bakum@ey.com"));
            email.setTo(toAddresses);
            if (msg != null) {
                email.setSubject("GirlScouts VTK Demo Site Cron ERROR");
                email.setTextMsg("Error removing test nodes from database on " + new java.util.Date() + ":" + msg);
            } else {
                email.setSubject("GirlScouts VTK Demo Site Cron");
                email.setTextMsg("Successfully removed all test nodes from database on " + new java.util.Date() + ".");
            }
            messageGateway.send(email);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }

}