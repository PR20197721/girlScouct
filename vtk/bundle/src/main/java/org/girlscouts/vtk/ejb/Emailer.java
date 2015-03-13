package org.girlscouts.vtk.ejb;

import java.util.ArrayList;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.auth.permission.PermissionConstants;
@Component
@Service(value = Emailer.class)
public class Emailer {
	@Reference
	UserUtil userUtil;
	
	@Reference
	private MessageGatewayService messageGatewayService;

	public void send(User user, EmailMeetingReminder emr) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_SEND_EMAIL_ID))
			throw new IllegalAccessException();
		try {
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);
			
			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();

			String[] bccStrings,toStrings;
			if(emr.getBcc()!=null && !emr.getBcc().isEmpty()){
				bccStrings = emr.getBcc().split(";");
				for (int i = 0; i < bccStrings.length; i++) {
					if(!bccStrings[i].isEmpty()){
						emailRecipients.add(new InternetAddress(bccStrings[i]));
					}
				}

			}
			if(emr.getTo()!=null && !emr.getTo().isEmpty()){
				email.addTo(emr.getTo());
//				toStrings = emr.getTo().split(";");
//				for (int i = 0; i < toStrings.length; i++) {
//					if(!toStrings[i].isEmpty()){
//						emailRecipients.add(new InternetAddress(toStrings[i]));
//					}
//				}
			}
			
			email.setSubject(emr.getSubj());
			email.setHtmlMsg(emr.getHtml());
			if(!emailRecipients.isEmpty()){
				email.setBcc(emailRecipients);
				messageGateway.send(email);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
