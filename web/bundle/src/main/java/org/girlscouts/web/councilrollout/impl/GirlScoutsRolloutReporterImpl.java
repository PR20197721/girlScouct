package org.girlscouts.web.councilrollout.impl;

import java.util.ArrayList;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.GirlScoutsRolloutReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value = GirlScoutsRolloutReporter.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.girlscoutsrolloutreporter", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts Content Rollout Report Service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class GirlScoutsRolloutReporterImpl implements GirlScoutsRolloutReporter{
	
	@Reference
	public MessageGatewayService messageGatewayService;

	private final Logger log = LoggerFactory.getLogger(GirlScoutsRolloutReporterImpl.class);

	public void execute(String subject, ArrayList<String> messageLog, ResourceResolver rr)
					throws WCMException {

		Resource addressesRes = rr.resolve("/etc/msm/rolloutreports");
		ValueMap vm = ResourceUtil.getValueMap(addressesRes);
		try {
			final String[] addresses = vm.get("emails",String[].class);
			if(addresses.length > 0){
				send(subject, messageLog, addresses);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    //send email using cq email service
	public void send(String subject, ArrayList<String> messageLog, String[] addresses) 
			throws WCMException{
		try {

			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			for(String address : addresses){
				emailRecipients.add(new InternetAddress(address));
			}
			email.setSubject(subject);
			StringBuffer htmlBuffer = new StringBuffer();
			for(String message : messageLog){
				htmlBuffer.append(message + "<br>");
			}
			email.setHtmlMsg(htmlBuffer.toString());
			if(!emailRecipients.isEmpty()){
				email.setTo(emailRecipients);
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
				messageGateway.send(email);
			}else{
				log.error("No email address found for council rollout report");
			}

		} catch (Exception e) {
            e.printStackTrace();
		}

	}
}