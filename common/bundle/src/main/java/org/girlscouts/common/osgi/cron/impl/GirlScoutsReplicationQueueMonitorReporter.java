package org.girlscouts.common.osgi.cron.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.girlscouts.common.constants.PageReplicationConstants;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = GirlScoutsReplicationQueueMonitorReporter.Config.class)
public class GirlScoutsReplicationQueueMonitorReporter implements Runnable {

	@ObjectClassDefinition(name = "Girl Scouts Replication Queue Monitor Reporter Configuration", description = "Girl Scouts Replication Queue Monitor Reporter Configuration")
	public static @interface Config {
		/**
		 * schedulerName
		 * 
		 * @return String name
		 */
		@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
		public String schedulerName() default "Girl Scouts Replication Queue Monitor Reporter";

		/**
		 * schedulerConcurrent
		 * 
		 * @return schedulerConcurrent
		 */
		@AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
		boolean schedulerConcurrent() default false;

		/**
		 * serviceEnabled
		 * 
		 * @return serviceEnabled
		 */
		@AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
		boolean serviceEnabled() default false;

		/**
		 * schedulerExpression
		 * 
		 * @return schedulerExpression
		 */
		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run every night at 2am.", type = AttributeType.STRING)
		String schedulerExpression() default "0 0 2 * * ?";

		/**
		 * emailAddresses
		 * 
		 * @return emailAddresses
		 */
		@AttributeDefinition(name = "Email Addresses", description = "Replication Queue Monitor recipents")
		String[] emailAddresses();

		/**
		 * queueTimeHolderPath
		 * 
		 * @return queueTimeHolderPath
		 */

		@AttributeDefinition(name = "queueTimeHolderPath", description = "queueTimeHolderPath", type = AttributeType.STRING)
		String queueTimeHolderPath();

	}

	private static final Logger logger = LoggerFactory.getLogger(GirlScoutsReplicationQueueMonitorReporter.class);

	@Reference
	private Scheduler scheduler;

	@Reference
	private SlingSettingsService slingeSettings;

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Reference
	public MessageGatewayService messageGatewayService;

	private MessageGateway<HtmlEmail> gateway;

	protected Map<String, Object> serviceParams;

	private int schedulerID;

	private boolean author = false;

	private String queTimeHolderPath;

	String[] emailIds;

	@Activate
	private void activate(GirlScoutsReplicationQueueMonitorReporter.Config config) {

		logger.info("Girl Scouts Replication Queue Monitor Service activated..!");

		this.serviceParams = new HashMap<>();
		this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");

		schedulerID = config.schedulerName().hashCode();
		queTimeHolderPath = config.queueTimeHolderPath();
		emailIds = config.emailAddresses();
		author = isAuthor();

		gateway = messageGatewayService.getGateway(HtmlEmail.class);

		if (gateway == null) {
			throw new NullPointerException(
					"Unable to get Message gateway from messageGatewayService.  Look for errors specific to the day.cq .mailer bundle");
		} else {
			logger.info("MessageGateway Created and non-null. (This is a good thing)");
		}
	}

	@Modified
	protected void modified(GirlScoutsReplicationQueueMonitorReporter.Config config) {
		removeScheduler();
		schedulerID = config.schedulerName().hashCode();
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(GirlScoutsReplicationQueueMonitorReporter.Config config) {
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */
	private void removeScheduler() {
		logger.debug("Removing Scheduler Job '{}'", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(GirlScoutsReplicationQueueMonitorReporter.Config config) {
		if (config.serviceEnabled()) {
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			logger.debug("Scheduler added succesfully");
		} else {
			logger.debug("GirlscourtsReplicationQueueMonitorReporter is Disabled, no scheduler job created");
		}
	}

	@Override
	public void run() {
		logger.info("Running Girl Scouts Replication Queue Monitor Reporter Service");
		if (author) {
			sendReport();
		} else {
			logger.info(
					"Publisher instance - will not execute Running Girl Scouts Replication Queue Monitor Reporter Service");
		}

	}

	private void sendReport() {
		ResourceResolver resourceResolver = null;
		Session session = null;
		try {
			resourceResolver = resolverFactory.getServiceResourceResolver(this.serviceParams);
			session = resourceResolver.adaptTo(Session.class);
			Node queTimeHolderNode = resourceResolver.resolve(queTimeHolderPath).adaptTo(Node.class);
			Map<String, Long> map = new HashMap<>();
			if (null != queTimeHolderNode) {
				PropertyIterator pi = queTimeHolderNode.getProperties();
				while (pi.hasNext()) {
					Property property = pi.nextProperty();
					if (!property.getName().equals("jcr:primaryType") && !property.getName().equals("jcr:mixinTypes")) {
						map.put(property.getName(), TimeUnit.MILLISECONDS.toMinutes(property.getLong()));
						queTimeHolderNode.setProperty(property.getName(), Long.valueOf(0));
					}
				}
				session.save();
			}
			sendMail(map);
		} catch (LoginException | RepositoryException e) {
			logger.error("encountered error: ", e);
		} finally {
			if (null != session) {
				session.logout();
			}
			if (null != resourceResolver) {
				resourceResolver.close();
			}
		}

	}

	private void sendMail(Map<String, Long> map) {
		logger.info("Running sendMail()");
		HtmlEmail email = new HtmlEmail();
		ArrayList<InternetAddress> emailRecipients = new ArrayList<>();
		try {
			if (null != emailIds) {
				for (String address : emailIds) {
					emailRecipients.add(new InternetAddress(address));
				}
				email.setSubject(PageReplicationConstants.GS_REPLICATION_QUEUE_MONITOR_SUBJECT);
				email.setTo(emailRecipients);
				String body = "Maximum queue wait time on " + new Date() + "<br/>"
						+ "<table width='100%' border='1' align='center'>" + "<tr align='center'>"
						+ "<td><b>Replication Agent ID <b></td>" + "<td><b>Wait Time (in Minutues)<b></td>" + "</tr>";

				for (Map.Entry<String, Long> entry : map.entrySet()) {
					body = body + "<tr align='center'>" + "<td>" + entry.getKey() + "</td>" + "<td>" + entry.getValue()
							+ "</td>" + "</tr>";

				}
				email.setHtmlMsg(body);
				gateway = messageGatewayService.getGateway(HtmlEmail.class);
				gateway.send(email);
			} else {
				logger.error("No email address found for Replication Queue Monitor Reporter service");
			}
		} catch (Exception e) {
			logger.error("Error occured " + e);
		}

	}

	private boolean isAuthor() {
		logger.info("Checking if running on author instance");

		return slingeSettings.getRunModes().contains("author");
	}

}
