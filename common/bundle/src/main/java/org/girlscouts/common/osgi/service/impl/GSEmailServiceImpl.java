package org.girlscouts.common.osgi.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(service = {
		GSEmailService.class }, immediate = true, name = "org.girlscouts.osgi.service.impl.GSEmailServiceImpl")
public class GSEmailServiceImpl implements GSEmailService {


	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	public MessageGatewayService messageGatewayService;
	Map<String, Object> serviceParams = new HashMap<String, Object>();

	private static Logger log = LoggerFactory.getLogger(GSEmailServiceImpl.class);

	@Activate
	private void activate(ComponentContext context) {
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
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
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}, body:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]), body);
				messageGateway.send(email);
				log.info("Girlscouts Email Service: Email message sent successfully");
			} catch (MailingException e) {
				log.error("Girlscouts Email Service: Failed to send email message subject:%s, toAddresses:{}, body:{}",
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
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]));
				messageGateway.send(email);
				log.info("Girlscouts Email Service: Email message sent successfully");
			} catch (Exception e) {
				log.error(
						"Girlscouts Email Service encountered error: Failed to send email message subject:{}, toAddresses:{}, body:{}",
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
		body = parseHtml(body, email);
		email.setHtmlMsg(body);
		if (attachments != null && !attachments.isEmpty()) {
			for (GSEmailAttachment attachment : attachments) {
				if (StringUtils.isNotBlank(attachment.getBaseName())  && (attachment.getFileData().length >0 && attachment.getFileData()!= null)
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
				if (address != null && !address.isEmpty()) {
					emailRecipients.add(new InternetAddress(address));
				}
			}
			email.setTo(emailRecipients);
		}
	}

	private String parseHtml(String html, HtmlEmail email) {
		// Find images and replace them with embeds, embed the image file in the
		// email
		final Pattern imgPattern = Pattern.compile("<img src=\"(.*?)\"");
		final Matcher imgMatcher = imgPattern.matcher(html);
		final StringBuffer imgSb = new StringBuffer();
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
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
		} catch (LoginException e) {
			log.error("Girlscouts Email Service encountered error: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e) {
				log.error("Girlscouts Email Service encountered error while closing resource resolver: ", e);
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
