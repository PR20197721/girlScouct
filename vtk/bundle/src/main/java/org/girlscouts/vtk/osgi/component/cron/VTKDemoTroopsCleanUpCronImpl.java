package org.girlscouts.vtk.osgi.component.cron;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.component.ConfigManager;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;

@Component(metatype = true, immediate = true, label = "AEM Demo Cron Job for VTK DEMO", description = "rm demo temp nodes from db")
@Service(value = {Runnable.class})
@Properties({@Property(name = "service.description", value = "Girl Scouts VTK demo troops clean up job", propertyPrivate = true), @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = true), @Property(name = "scheduler.expression", label = "scheduler.expression", value = "4 40 4 1 1 ?", description = "cron expression"), @Property(name = "scheduler.concurrent", boolValue = false, propertyPrivate = true), @Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)})
public class VTKDemoTroopsCleanUpCronImpl implements Runnable {
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
            String sql = "SELECT * FROM [nt:unstructured] as s where ISDESCENDANTNODE([/vtk" + VtkUtil.getYearPlanBase(null, null) + "]) and s.[sfTroopId] like 'SHARED_%' and (s.[ocm_classname]='org.girlscouts.vtk.models.Troop' OR s.[ocm_classname]='org.girlscouts.vtk.ocm.TroopNode')";
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.JCR_SQL2);
            QueryResult result = q.execute();
            NodeIterator itr = result.getNodes();
            while (itr.hasNext()) {
                try {
                    session.removeItem(itr.nextNode().getPath());
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



