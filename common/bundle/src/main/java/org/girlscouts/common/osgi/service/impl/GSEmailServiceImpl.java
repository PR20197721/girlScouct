package org.girlscouts.common.osgi.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
import org.apache.sling.commons.threads.ModifiableThreadPoolConfig;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolConfig;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.configuration.GSEmailServiceConfig;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(service = {GSEmailService.class }, immediate = true, name = "org.girlscouts.osgi.service.impl.GSEmailServiceImpl")
@Designate(ocd = GSEmailServiceConfig.class)
public class GSEmailServiceImpl implements GSEmailService {

    private static Logger log = LoggerFactory.getLogger(GSEmailServiceImpl.class);

	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	public MessageGatewayService messageGatewayService;
    @Reference
    private ThreadPoolManager threadPoolManager;

	Map<String, Object> serviceParams = new HashMap<String, Object>();
    protected ComponentContext context;
    private MessageGateway<HtmlEmail> gateway;
    private static final int EMAIL_RETRIES = 3;
    private int minThreadPoolSize = 1;
    private int maxThreadPoolSize = 10;
    private ThreadPool threadPool;

	@Activate
	private void activate(ComponentContext context) {
	    this.context = context;
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		try {
            this.minThreadPoolSize = Integer.getInteger(getConfig("minThreadPoolSize"));
		}catch(Exception e){
		    log.error("Error occurred:",e);
        }
        try {
            this.maxThreadPoolSize =  Integer.getInteger(getConfig("maxThreadPoolSize"));
        }catch(Exception e){
            log.error("Error occurred:",e);
        }
        ModifiableThreadPoolConfig threadPoolConfig = new ModifiableThreadPoolConfig();
        threadPoolConfig.setMinPoolSize(minThreadPoolSize);
        threadPoolConfig.setMaxPoolSize(maxThreadPoolSize);
        threadPoolConfig.setPriority(ThreadPoolConfig.ThreadPriority.NORM);

        // Create threadpool from config.
        threadPool = threadPoolManager.create(threadPoolConfig, "Girl Scouts Email Thread Pool");
        // Create the gateway.
        gateway = messageGatewayService.getGateway(HtmlEmail.class);

        if (gateway == null) {
            throw new NullPointerException("Unable to get Message gateway from messageGatewayService.  Look for errors specific to the day.cq .mailer bundle");
        } else {
            log.info("MessageGateway Created and non-null. (This is a good thing)");
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
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}, body:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]), body);
                sendLater(email);
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
	public void sendEmail(String subject, List<String> toAddresses, String body, String fromAddress)
			throws EmailException, MessagingException {
		if (toAddresses != null && !toAddresses.isEmpty() && subject != null && subject.trim().length() > 0) {
			HtmlEmail email = new HtmlEmail();
			if (subject != null) {
				email.setSubject(subject);
			}
            email.setFrom("girlscouts@amsmail.adobecqms.net");
			if(fromAddress != null){
				email.addReplyTo(fromAddress);
			}
			setRecipients(toAddresses, email);
			setBody(body, null, email);
			email.setHtmlMsg(body);
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}, body:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]), body);
                sendLater(email);
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
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]));
                sendLater(email);
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
	@Override
	public void sendEmail(String subject, List<String> toAddresses, String body, Set<GSEmailAttachment> attachments, String fromAddress)
			throws EmailException, MessagingException {
		if (toAddresses != null && !toAddresses.isEmpty() && subject != null && subject.trim().length() > 0) {
			HtmlEmail email = new HtmlEmail();
			if (subject != null) {
				email.setSubject(subject);
			}
            email.setFrom("girlscouts@amsmail.adobecqms.net");
            if(fromAddress != null){
                email.addReplyTo(fromAddress);
            }
			setRecipients(toAddresses, email);
			setBody(body, attachments, email);
			try {
				log.info("Girlscouts Email Service: Sending email message subject:{}, toAddresses:{}", subject,
						toAddresses.toArray(new String[toAddresses.size()]));
                sendLater(email);
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
				if (StringUtils.isNotBlank(attachment.getBaseName()) && attachment.getFileData()!= null && (attachment.getFileData().length >0 )
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

    protected String getConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return String.valueOf(properties.get(property));
        } else {
            return "";
        }
    }

    public void sendLater(final HtmlEmail email) {
	    log.debug("Attempting to send email:");
        log.debug("Subject:"+email.getSubject());
        log.debug("To:");
        try {
            email.getToAddresses().stream().forEach(s -> log.debug(s.toString()));
        }catch(Exception e){

        }
        log.debug("FROM:"+email.getFromAddress());
        log.debug("ReplyTo:");
        try {
            email.getReplyToAddresses().stream().forEach(s -> log.debug(s.toString()));
        }catch(Exception e){

        }
        threadPool.submit(() -> {
            for (int i = 0; i < EMAIL_RETRIES; i++) {
                try {
                    log.debug("Sending Email attempt "+i);
                    gateway.send(email);
                    log.debug("Email sent successfully.");
                    break;
                } catch (MailingException me) {
                    if (i == EMAIL_RETRIES - 1) {
                        log.error("Email sending failed. No more retries");
                    } else {
                        log.debug("Email sending failed. Retries left: " + (EMAIL_RETRIES - i));
                    }
                }
            }
        });
    }
}
