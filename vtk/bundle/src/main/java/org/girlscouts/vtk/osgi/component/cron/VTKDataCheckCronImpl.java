package org.girlscouts.vtk.osgi.component.cron;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = VTKDataCheckCronImpl.Config.class)
public class VTKDataCheckCronImpl implements Runnable {

    @ObjectClassDefinition(name="VTK Year Plan Data Check Cron Configuration", description = "VTK Year Plan Data Check Cron Configuration")
    public static @interface Config {
        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "0 0 3 * * ?";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default true;

        @AttributeDefinition(name = "Email Addresses", description = "Notification recipients")
        String[] emailAddresses() default  "dmitriy.bakum@ey.com";

    }


    private static Logger log = LoggerFactory.getLogger(VTKDataCheckCronImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    @Reference
    private GSEmailService gsEmailService;
    private String host = "";
    List<String> emails;

    @Activate
    private void activate(VTKDataCheckCronImpl.Config config) {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        this.emails = Arrays.asList(config.emailAddresses());
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            this.host = addr.getHostName();

        } catch (UnknownHostException e) {
            log.error("Girl Scouts Server Load Monitor encountered error: ", e);
        }
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public void run() {
        log.debug("Executing VTK Year Plan Data Check cron job");
        ResourceResolver rr = null;
        List<String> errorList = new ArrayList<>();
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Resource vtkFolder = rr.resolve("vtk");
            int year = 2015;
            while (vtkFolder != null && !ResourceUtil.isNonExistingResource(vtkFolder)) {
                Node yearNode = vtkFolder.adaptTo(Node.class);
                NodeIterator councils = yearNode.getNodes();
                while (councils.hasNext()) {
                    Node councilNode = councils.nextNode();
                    log.debug("Processing " + councilNode.getPath());
                    if (councilNode.hasProperty("ocm_classname")) {
                        String EXPRESSION = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([" + councilNode.getPath() + "]) and s.[ocm_classname] = 'org.girlscouts.vtk.ocm.TroopNode'";
                        Query q = qm.createQuery(EXPRESSION, Query.JCR_SQL2);
                        QueryResult result = q.execute();
                        if (result != null) {
                            RowIterator rowIter = result.getRows();
                            while (rowIter.hasNext()) {
                                Row row = rowIter.nextRow();
                                Node node = row.getNode();
                                try {
                                    if (node.hasNode("yearPlan")) {
                                        Node yearPlan = node.getNode("yearPlan");
                                        if (yearPlan.hasProperty("ocm_classname")) {
                                            if (yearPlan.hasNode("meetingCanceled")) {
                                                Node cancelledMeetings = yearPlan.getNode("meetingCanceled");
                                                NodeIterator cancelledMeetingsIterator = cancelledMeetings.getNodes();
                                                while (cancelledMeetingsIterator.hasNext()) {
                                                    Node cancelledMeeting = (Node) cancelledMeetingsIterator.next();
                                                    if (cancelledMeeting.hasProperty("ocm_classname")) {
                                                        if (cancelledMeeting.hasNode("assets")) {
                                                            Node assets = cancelledMeeting.getNode("assets");
                                                            NodeIterator assetsIterator = assets.getNodes();
                                                            while (assetsIterator.hasNext()) {
                                                                Node assetNode = (Node) assetsIterator.next();
                                                                if (!assetNode.hasProperty("ocm_classname")) {
                                                                    log.debug(assetNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(assetNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (cancelledMeeting.hasNode("sentEmails")) {
                                                            Node sentEmails = cancelledMeeting.getNode("sentEmails");
                                                            NodeIterator sentEmailsIterator = sentEmails.getNodes();
                                                            while (sentEmailsIterator.hasNext()) {
                                                                Node sentEmailNode = (Node) sentEmailsIterator.next();
                                                                if (!sentEmailNode.hasProperty("ocm_classname")) {
                                                                    log.debug(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (cancelledMeeting.hasNode("attendance")) {
                                                            Node attendance = cancelledMeeting.getNode("attendance");
                                                            NodeIterator attendanceIterator = attendance.getNodes();
                                                            while (attendanceIterator.hasNext()) {
                                                                Node attendanceNode = (Node) attendanceIterator.next();
                                                                if (!attendanceNode.hasProperty("ocm_classname")) {
                                                                    log.debug(attendanceNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(attendanceNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (cancelledMeeting.hasNode("achievement")) {
                                                            Node achievement = cancelledMeeting.getNode("achievement");
                                                            NodeIterator achievementIterator = achievement.getNodes();
                                                            while (achievementIterator.hasNext()) {
                                                                Node achievementNode = (Node) achievementIterator.next();
                                                                if (!achievementNode.hasProperty("ocm_classname")) {
                                                                    log.debug(achievementNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(achievementNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (cancelledMeeting.hasNode("notes")) {
                                                            Node notes = cancelledMeeting.getNode("notes");
                                                            NodeIterator noteIterator = notes.getNodes();
                                                            while (noteIterator.hasNext()) {
                                                                Node noteNode = (Node) noteIterator.next();
                                                                if (!noteNode.hasProperty("ocm_classname")) {
                                                                    log.debug(noteNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(noteNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        log.debug(cancelledMeeting.getPath() + " is missing ocm_classname");
                                                        errorList.add(cancelledMeeting.getPath() + " is missing ocm_classname");
                                                    }
                                                }
                                            }
                                            if (yearPlan.hasNode("meetingEvents")) {
                                                Node meetingEvents = yearPlan.getNode("meetingEvents");
                                                NodeIterator meetingEventIterator = meetingEvents.getNodes();
                                                while (meetingEventIterator.hasNext()) {
                                                    Node meetingEvent = (Node) meetingEventIterator.next();
                                                    if (meetingEvent.hasProperty("refId")) {
                                                        String refPath = meetingEvent.getProperty("refId").getString();
                                                        try {
                                                            Node refferredNode = session.getNode(refPath);
                                                            if (refferredNode == null) {
                                                                log.debug("refId for " + meetingEvent.getPath() + " has invalid path " + refPath);
                                                                errorList.add("refId for " + meetingEvent.getPath() + " has invalid path " + refPath);
                                                            }
                                                        } catch (Exception refNodeMissingex) {
                                                            log.debug("refId for " + meetingEvent.getPath() + " has invalid path " + refPath);
                                                            errorList.add("refId for " + meetingEvent.getPath() + " has invalid path " + refPath);
                                                        }
                                                    } else {
                                                        log.debug(meetingEvent.getPath() + " is missing refId");
                                                        errorList.add(meetingEvent.getPath() + " is missing refId");
                                                    }
                                                    if (meetingEvent.hasProperty("ocm_classname")) {
                                                        if (meetingEvent.hasNode("assets")) {
                                                            Node assets = meetingEvent.getNode("assets");
                                                            NodeIterator assetsIterator = assets.getNodes();
                                                            while (assetsIterator.hasNext()) {
                                                                Node assetNode = (Node) assetsIterator.next();
                                                                if (!assetNode.hasProperty("ocm_classname")) {
                                                                    log.debug(assetNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(assetNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meetingEvent.hasNode("sentEmails")) {
                                                            Node sentEmails = meetingEvent.getNode("sentEmails");
                                                            NodeIterator sentEmailsIterator = sentEmails.getNodes();
                                                            while (sentEmailsIterator.hasNext()) {
                                                                Node sentEmailNode = (Node) sentEmailsIterator.next();
                                                                if (!sentEmailNode.hasProperty("ocm_classname")) {
                                                                    log.debug(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meetingEvent.hasNode("attendance")) {
                                                            Node attendance = meetingEvent.getNode("attendance");
                                                            NodeIterator attendanceIterator = attendance.getNodes();
                                                            while (attendanceIterator.hasNext()) {
                                                                Node attendanceNode = (Node) attendanceIterator.next();
                                                                if (!attendanceNode.hasProperty("ocm_classname")) {
                                                                    log.debug(attendanceNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(attendanceNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meetingEvent.hasNode("achievement")) {
                                                            Node achievement = meetingEvent.getNode("achievement");
                                                            NodeIterator achievementIterator = achievement.getNodes();
                                                            while (achievementIterator.hasNext()) {
                                                                Node achievementNode = (Node) achievementIterator.next();
                                                                if (!achievementNode.hasProperty("ocm_classname")) {
                                                                    log.debug(achievementNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(achievementNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meetingEvent.hasNode("notes")) {
                                                            Node notes = meetingEvent.getNode("notes");
                                                            NodeIterator noteIterator = notes.getNodes();
                                                            while (noteIterator.hasNext()) {
                                                                Node noteNode = (Node) noteIterator.next();
                                                                if (!noteNode.hasProperty("ocm_classname")) {
                                                                    log.debug(noteNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(noteNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        log.debug(meetingEvent.getPath() + " is missing ocm_classname");
                                                        errorList.add(meetingEvent.getPath() + " is missing ocm_classname");
                                                    }
                                                }

                                            }
                                            if (yearPlan.hasNode("meetings")) {
                                                Node meetings = yearPlan.getNode("meetings");
                                                NodeIterator meetingsIterator = meetings.getNodes();
                                                while (meetingsIterator.hasNext()) {
                                                    Node meeting = (Node) meetingsIterator.next();
                                                    if (meeting.hasProperty("ocm_classname")) {
                                                        if (meeting.hasNode("assets")) {
                                                            Node assets = meeting.getNode("assets");
                                                            NodeIterator assetsIterator = assets.getNodes();
                                                            while (assetsIterator.hasNext()) {
                                                                Node assetNode = (Node) assetsIterator.next();
                                                                if (!assetNode.hasProperty("ocm_classname")) {
                                                                    log.debug(assetNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(assetNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meeting.hasNode("sentEmails")) {
                                                            Node sentEmails = meeting.getNode("sentEmails");
                                                            NodeIterator sentEmailsIterator = sentEmails.getNodes();
                                                            while (sentEmailsIterator.hasNext()) {
                                                                Node sentEmailNode = (Node) sentEmailsIterator.next();
                                                                if (!sentEmailNode.hasProperty("ocm_classname")) {
                                                                    log.debug(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meeting.hasNode("attendance")) {
                                                            Node attendance = meeting.getNode("attendance");
                                                            NodeIterator attendanceIterator = attendance.getNodes();
                                                            while (attendanceIterator.hasNext()) {
                                                                Node attendanceNode = (Node) attendanceIterator.next();
                                                                if (!attendanceNode.hasProperty("ocm_classname")) {
                                                                    log.debug(attendanceNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(attendanceNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meeting.hasNode("achievement")) {
                                                            Node achievement = meeting.getNode("achievement");
                                                            NodeIterator achievementIterator = achievement.getNodes();
                                                            while (achievementIterator.hasNext()) {
                                                                Node achievementNode = (Node) achievementIterator.next();
                                                                if (!achievementNode.hasProperty("ocm_classname")) {
                                                                    log.debug(achievementNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(achievementNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (meeting.hasNode("notes")) {
                                                            Node notes = meeting.getNode("notes");
                                                            NodeIterator noteIterator = notes.getNodes();
                                                            while (noteIterator.hasNext()) {
                                                                Node noteNode = (Node) noteIterator.next();
                                                                if (!noteNode.hasProperty("ocm_classname")) {
                                                                    log.debug(noteNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(noteNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        log.debug(meeting.getPath() + " is missing ocm_classname");
                                                        errorList.add(meeting.getPath() + " is missing ocm_classname");
                                                    }
                                                }
                                            }
                                            if (yearPlan.hasNode("activities")) {
                                                Node activities = yearPlan.getNode("activities");
                                                NodeIterator activitiesIterator = activities.getNodes();
                                                while (activitiesIterator.hasNext()) {
                                                    Node activity = (Node) activitiesIterator.next();
                                                    if (activity.hasProperty("ocm_classname")) {
                                                        if (activity.hasNode("assets")) {
                                                            Node assets = activity.getNode("assets");
                                                            NodeIterator assetsIterator = assets.getNodes();
                                                            while (assetsIterator.hasNext()) {
                                                                Node assetNode = (Node) assetsIterator.next();
                                                                if (!assetNode.hasProperty("ocm_classname")) {
                                                                    log.debug(assetNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(assetNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (activity.hasNode("sentEmails")) {
                                                            Node sentEmails = activity.getNode("sentEmails");
                                                            NodeIterator sentEmailsIterator = sentEmails.getNodes();
                                                            while (sentEmailsIterator.hasNext()) {
                                                                Node sentEmailNode = (Node) sentEmailsIterator.next();
                                                                if (!sentEmailNode.hasProperty("ocm_classname")) {
                                                                    log.debug(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(sentEmailNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (activity.hasNode("attendance")) {
                                                            Node attendance = activity.getNode("attendance");
                                                            NodeIterator attendanceIterator = attendance.getNodes();
                                                            while (attendanceIterator.hasNext()) {
                                                                Node attendanceNode = (Node) attendanceIterator.next();
                                                                if (!attendanceNode.hasProperty("ocm_classname")) {
                                                                    log.debug(attendanceNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(attendanceNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (activity.hasNode("achievement")) {
                                                            Node achievement = activity.getNode("achievement");
                                                            NodeIterator achievementIterator = achievement.getNodes();
                                                            while (achievementIterator.hasNext()) {
                                                                Node achievementNode = (Node) achievementIterator.next();
                                                                if (!achievementNode.hasProperty("ocm_classname")) {
                                                                    log.debug(achievementNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(achievementNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                        if (activity.hasNode("notes")) {
                                                            Node notes = activity.getNode("notes");
                                                            NodeIterator noteIterator = notes.getNodes();
                                                            while (noteIterator.hasNext()) {
                                                                Node noteNode = (Node) noteIterator.next();
                                                                if (!noteNode.hasProperty("ocm_classname")) {
                                                                    log.debug(noteNode.getPath() + " is missing ocm_classname");
                                                                    errorList.add(noteNode.getPath() + " is missing ocm_classname");
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        log.debug(activity.getPath() + " is missing ocm_classname");
                                                        errorList.add(activity.getPath() + " is missing ocm_classname");
                                                    }
                                                }
                                            }
                                            if (yearPlan.hasNode("schedule")) {
                                                Node schedule = yearPlan.getNode("schedule");
                                                if (schedule.hasProperty("ocm_classname")) {
                                                    //TODO
                                                } else {
                                                    log.debug(schedule.getPath() + " is missing ocm_classname");
                                                    errorList.add(schedule.getPath() + " is missing ocm_classname");
                                                }

                                            }
                                            if (yearPlan.hasNode("locations")) {
                                                Node locations = yearPlan.getNode("locations");
                                                NodeIterator iterator = locations.getNodes();
                                                while (iterator.hasNext()) {
                                                    Node location = (Node) iterator.next();
                                                    if (location.hasProperty("ocm_classname")) {
                                                    } else {
                                                        log.debug(location.getPath() + " is missing ocm_classname");
                                                        errorList.add(location.getPath() + " is missing ocm_classname");
                                                    }
                                                }
                                            }
                                        } else {
                                            log.debug(yearPlan.getPath() + " Invalid YearPlan Node");
                                            errorList.add(yearPlan.getPath() + " Invalid YearPlan Node");
                                        }
                                    }
                                } catch (Exception e) {
                                    log.debug("Unexpected error while checking "+node.getPath());
                                    errorList.add("Unexpected error while checking "+node.getPath());
                                }
                            }
                        }
                    }
                }
                vtkFolder = rr.resolve("vtk"+year);
                year ++;
            }
            emailCronRpt(errorList);
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

    public void emailCronRpt(List<String> errors) {
        try {
                log.info("Sending VTK Data Check Notification");
                StringBuffer html = new StringBuffer();
                if(errors.size() > 0){
                    html.append("<p>"+errors.size()+" data errors have been detected : </p>");
                    html.append("<ol>");
                    for(String error:errors){
                        html.append("<li>"+error+"</li>");
                    }
                    html.append("</ol>");
                }else{
                    html.append("<p>No data errors have been detected</p>");
                }

                String subject = "VTK Data Check Notification: Server " + host;
                this.gsEmailService.sendEmail(subject, emails, html.toString());
                log.info("VTK Data Check Notification email sent for {}", host);
        } catch (Exception e) {
            log.error("Girl Scouts VTK Data Check Notification email encountered error: ", e);
        }
    }

}