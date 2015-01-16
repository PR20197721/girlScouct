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
@Service(value=Emailer.class)
public class Emailer {
	
	@Reference
	private MessageGatewayService messageGatewayService;	
	
	public void test(EmailMeetingReminder emr){
		
	try{	
		MessageGateway<HtmlEmail> messageGateway = messageGatewayService
				.getGateway(HtmlEmail.class);
		
		
		ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
		emailRecipients.add(new InternetAddress("cwu@northpointdigital.com"));
		emailRecipients.add(new InternetAddress(emr.getCc()));
		emailRecipients.add(new InternetAddress("ayakobovich@northpointdigital.com"));
		
		
		HtmlEmail email = new HtmlEmail();
		email.setHtmlMsg(emr.getHtml())	;	
		email.setTo(emailRecipients);
		email.setSubject(emr.getSubj());

		messageGateway.send(email);
		
	}catch(Exception e){e.printStackTrace();}
		
	}
}
