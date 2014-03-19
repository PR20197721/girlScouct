package org.girlscouts.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;


@Component(immediate=true, metatype=false)
@Service(Servlet.class)
@Properties({
	@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(name = "sling.servlet.methods", value = "POST"),
	@Property(name = "sling.servlet.selectors", value="mail")
})
@SuppressWarnings("serial")
public class EmailServlet extends SlingAllMethodsServlet{

	private final String SEND_TO = "igor.kaplunov1@gmail.com";
	
	@Reference
	private MessageGatewayService messageGatewayService;
	
	private static final Logger log = LoggerFactory
            .getLogger(EmailServlet.class);
	
	private Map<String, String> parameters;
	
	@Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
	
		
		
		@SuppressWarnings("unchecked")
		Enumeration<String> parameterNames = request.getParameterNames();
		
		Map<String, String> parameters = new HashMap<String, String>();                       
		while (parameterNames.hasMoreElements()) {
		  final String key = parameterNames.nextElement();
		  parameters.put(key, request.getParameter(key));
		}
		
		String emailContent = "";
		for(String key : parameters.keySet()){
			emailContent= emailContent + key + " : " + parameters.get(key) + "\n";
		}
		
		HtmlEmail email = new HtmlEmail();
		ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
		try {
			emailRecipients.add(new InternetAddress(SEND_TO));
			email.setTo(emailRecipients);
			email.setHtmlMsg(emailContent);
			
			
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			
			log.info("messageGateway : " + messageGateway);
			
			messageGateway.send(email);

		} catch (AddressException e) {
			
			log.error(e.getMessage());
		} catch (EmailException e) {
			
			log.error(e.getMessage());
		}
		
		
		
	}
	
	
	
}
