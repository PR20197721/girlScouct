package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.models.EmailMeetingReminder;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Service(value = Emailer.class)
public class Emailer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Reference
    private UserUtil userUtil;
    @Reference
    private MessageGatewayService messageGatewayService;

    public void send(User user, Troop troop, EmailMeetingReminder emr) throws IllegalAccessException {
        ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
        logger.info("VTK Reminder Email Attempt Begin.");
        try {
            if (user != null && !(userUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID) || userUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ACT_ID))) {
                throw new IllegalAccessException();
            }
            MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
            HtmlEmail email = new HtmlEmail();
            String[] bccStrings, toStrings;
            if (emr.getBcc() != null && !emr.getBcc().isEmpty()) {
                bccStrings = emr.getBcc().split(";");
                for (int i = 0; i < bccStrings.length; i++) {
                    if (!bccStrings[i].isEmpty()) {
                        emailRecipients.add(new InternetAddress(bccStrings[i]));
                    }
                }

            }
            if (emr.getTo() != null && !emr.getTo().isEmpty()) {
                email.addTo(emr.getTo());
            }
            try {
                if (user.getEmail() != null && user.getEmail().trim().length() > 0) {
                    email.addReplyTo(user.getEmail());

                }
            } catch (Exception e) {
                logger.error("VTK Reminder Email Error - Reply To: Recipients: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
                logger.error("VTK Reminder Email Error - Reply To:", e);
            }
            email.setSubject(emr.getSubj());
            email.setHtmlMsg(emr.getHtml());
            if (!emailRecipients.isEmpty()) {
                email.setBcc(emailRecipients);
                messageGateway.send(email);
            }
            logger.info("VTK Reminder Email Success!  Sent to: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
        } catch (IllegalAccessException iae) {
            logger.error("VTK Reminder Email Error: Recipients: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
            logger.error("VTK Reminder Email Error: ", iae);
            throw iae;
        } catch (Throwable e) {
            logger.error("VTK Reminder Email Error: Recipients: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
            logger.error("VTK Reminder Email Error: ", e);
        } finally {
            logger.info("VTK Reminder Email Attempt End.");
        }

    }
}
