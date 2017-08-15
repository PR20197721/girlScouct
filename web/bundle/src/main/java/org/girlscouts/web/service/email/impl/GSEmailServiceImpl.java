package org.girlscouts.web.service.email.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.service.email.GSEmailService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Service
@Component
public class GSEmailServiceImpl implements GSEmailService {

	@Property(value = "Girl Scouts Email Service")
	static final String LABEL = "process.label";
	@Property(value = "Sends out html email with provided addresses, subject and body.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	public MessageGatewayService messageGatewayService;

	private ResourceResolver rr;

	private static Logger log = LoggerFactory.getLogger(GSEmailServiceImpl.class);

	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendEmail(String subject, List<String> toAddresses, String body)
			throws EmailException, MessagingException {
		if (toAddresses != null && !toAddresses.isEmpty() && subject != null && subject.trim().length() > 0) {
			HtmlEmail email = new HtmlEmail();
			if (subject != null) {
				email.setSubject(subject);
			}
			setRecipients(toAddresses, email);
			setBody(body, null, email);
			email.setHtmlMsg(body);
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			log.error("Girlscouts Email Service: Sending email message subject:%s, toAddresses:%s, body:%s", subject,
					toAddresses.toArray(new String[toAddresses.size()]),
					body);
			try {
				messageGateway.send(email);
			} catch (MailingException e) {
				log.error("Girlscouts Email Service: Failed to send email message subject:%s, toAddresses:%s, body:%s",
						subject, toAddresses.toArray(new String[toAddresses.size()]), body, e);
				throw e;
			}
		} else {
			throw new EmailException(
					"To addresses or subject are null [subject=" + subject + ", toAddresses=" + toAddresses + "]");
		}
	}

	@Override
	public void sendEmail(String subject, List<String> toAddresses, String body, Set<GSEmailAttachment> attachments)
			throws EmailException, MessagingException {
		if (toAddresses != null && !toAddresses.isEmpty() && subject != null && subject.trim().length() > 0) {
			HtmlEmail email = new HtmlEmail();
			if (subject != null) {
				email.setSubject(subject);
			}
			setRecipients(toAddresses, email);
			setBody(body, attachments, email);
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			log.error("Girlscouts Email Service: Sending email message subject:%s, toAddresses:%s", subject,
					toAddresses.toArray(new String[toAddresses.size()]));
			try {
				messageGateway.send(email);
			} catch (MailingException e) {
				log.error(
						"Girlscouts Email Service encountered error: Failed to send email message subject:%s, toAddresses:%s, body:%s",
						subject, toAddresses.toArray(new String[toAddresses.size()]), body, e);
				throw e;
			}
		} else {
			throw new EmailException(
					"To addresses or subject are null [subject=" + subject + ", toAddresses=" + toAddresses + "]");
		}
	}

	private void setBody(String body, Set<GSEmailAttachment> attachments, HtmlEmail email)
			throws MessagingException, EmailException {
		body = parseHtml(body, email, rr);
		email.setHtmlMsg(body);
		if (attachments != null && !attachments.isEmpty()) {
			for (GSEmailAttachment attachment : attachments) {
				if (StringUtils.isNotBlank(attachment.getBaseName()) && StringUtils.isNotBlank(attachment.getFileData())
						&& attachment.getFileType() != null) {
					try {
						email.attach(attachment.getDataSource(), attachment.getFileName(), attachment.getDescription());
					} catch (IOException e) {
						log.error("Girlscouts Email Service encountered error: ", e);
					}
				}
			}
		}
	}

	private void setRecipients(List<String> toAddresses, HtmlEmail email) throws AddressException, EmailException {
		if (toAddresses != null) {
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			for (String address : toAddresses) {
				if (address != null) {
					emailRecipients.add(new InternetAddress(address));
				}
			}
			email.setTo(emailRecipients);
		}
	}

	private String parseHtml(String html, HtmlEmail email, ResourceResolver rr) {
		// Find images and replace them with embeds, embed the image file in the
		// email
		final Pattern imgPattern = Pattern.compile("<img src=\"(.*?)\"");
		final Matcher imgMatcher = imgPattern.matcher(html);
		final StringBuffer imgSb = new StringBuffer();
		while (imgMatcher.find()) {
			byte[] result = null;
			try {
				String renditionPath = getRenditionPath(imgMatcher.group(1));
				Resource imgRes = rr.resolve(renditionPath);
				if (ResourceUtil.isNonExistingResource(imgRes)) {
					imgRes = rr.resolve(renditionPath.replaceAll("%20", " "));
					if (ResourceUtil.isNonExistingResource(imgRes)) {
						throw (new Exception("Cannot find resource: " + renditionPath));
					}
				}
				Node ntFileNode = imgRes.adaptTo(Node.class);
				Node ntResourceNode = ntFileNode.getNode("jcr:content");
				InputStream is = ntResourceNode.getProperty("jcr:data").getBinary().getStream();
				BufferedInputStream bin = new BufferedInputStream(is);
				result = IOUtils.toByteArray(bin);
				bin.close();
				is.close();
			} catch (Exception e) {
				log.error("Girlscouts Email Service encountered error: ", e);
			}
			try {
				String fileName = imgMatcher.group(1).substring(imgMatcher.group(1).lastIndexOf('/') + 1);
				File imgFile = new File(fileName);
				FileUtils.writeByteArrayToFile(imgFile, result);
				imgMatcher.appendReplacement(imgSb, "<img src=cid:" + (email.embed(imgFile, fileName)));
				imgMatcher.appendTail(imgSb);
				html = imgSb.toString();
			} catch (Exception e) {
				log.error("Girlscouts Email Service encountered error: ", e);
			}
		}
		return html;
	}

	public String getRenditionPath(String imgPath) {
		final Pattern pattern = Pattern.compile("/jcr:content/renditions/");
		final Matcher matcher = pattern.matcher(imgPath);
		if (matcher.find()) {
			return imgPath;
		} else {
			return imgPath + "/jcr:content/renditions/original";
		}
	}
}
