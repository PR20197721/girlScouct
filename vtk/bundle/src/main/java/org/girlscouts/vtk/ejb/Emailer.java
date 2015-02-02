package org.girlscouts.vtk.ejb;

import java.util.ArrayList;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value = Emailer.class)
public class Emailer {

	@Reference
	private MessageGatewayService messageGatewayService;

	public void test(EmailMeetingReminder emr) {

		try {
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);
			
			HtmlEmail email = new HtmlEmail();
			String[] ccStrings,toStrings;
			if(!emr.getCc().isEmpty()){
				ccStrings = emr.getCc().split(";");
			}
			if(!emr.getTo().isEmpty()){
				toStrings = emr.getTo().split(";");
			}
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			for (int i = 0; i < toStrings.length; i++) {
				if(!toStrings[i].isEmpty()){
					emailRecipients.add(new InternetAddress(toStrings[i]));
				}
			}
			for (int i = 0; i < ccStrings.length; i++) {
				if(!ccStrings[i].isEmpty()){
					emailRecipients.add(new InternetAddress(ccStrings[i]));
				}
			}

			// email.setHostName("mail.whatserver.com");
			// email.setFrom("me@apache.org");
			email.setHtmlMsg(emr.getHtml());
			email.setTo(emailRecipients);
			email.setSubject(emr.getSubj());

			messageGateway.send(email);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
