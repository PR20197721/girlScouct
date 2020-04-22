package org.girlscouts.common.osgi.cron.impl;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.InternetAddress;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationQueue;
import com.day.text.csv.Csv;

@Component(service = Runnable.class, immediate = true, name = "org.girlscouts.common.osgi.cron.impl.GirlScoutsReplicationQueueMonitor")
@Designate(ocd = GirlScoutsReplicationQueueMonitor.Config.class)
public class GirlScoutsReplicationQueueMonitor implements Runnable {
	@ObjectClassDefinition(name = "GirlScouts Replication Queue Monitor Configuration", description = "Service Configuration")
	public @interface Config {

		/**
		 * schedulerName
		 * 
		 * @return String name
		 */
		@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
		public String schedulerName() default "Girl Scouts Replication Queue Monitor";

		/**
		 * serviceEnabled
		 * 
		 * @return serviceEnabled
		 */
		@AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
		boolean serviceEnabled() default false;

		/**
		 * schedulerConcurrent
		 * 
		 * @return schedulerConcurrent
		 */
		@AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
		boolean schedulerConcurrent() default false;

		/**
		 * schedulerExpression
		 * 
		 * @return schedulerExpression
		 */
		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: runs every 15 minutes.", type = AttributeType.STRING)
		String schedulerExpression() default "0 0/15 * 1/1 * ? *";

		/**
		 * emailAddresses
		 * 
		 * @return emailAddresses
		 */
		@AttributeDefinition(name = "Email Addresses", description = "Replication Queue Monitor recipents. Click on '+' symbol to add additional email addresses.")
		String[] emailAddresses();

		/**
		 * 
		 * maxWaitTime
		 * 
		 * @return maxWaitTime
		 */
		@AttributeDefinition(name = "Max Wait Time", description = "Max wait time of queue in minutes", type = AttributeType.INTEGER)
		int maxWaitTime() default 15;

		/**
		 * maxQueueSize
		 * 
		 * @return maxQueueSize
		 */

		@AttributeDefinition(name = "Max Queue Size", description = "Max queue size ", type = AttributeType.INTEGER)
		int maxQueueSize() default 100;

	}

	@Reference
	private Scheduler scheduler;

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Reference
	private SlingSettingsService slingeSettings;

	@Reference
	private AgentManager agentManager;

	@Reference
	public GSEmailService gsEmailService;

	private int schedulerID;

	String[] emailIds;

	int maxWaitTime;
	int maxQueueSize;

	private boolean author = false;

	protected Map<String, Object> serviceParams;

	private static final Logger logger = LoggerFactory.getLogger(GirlScoutsReplicationQueueMonitor.class);

	public static final String GS_REPLICATION_QUEUE_MONITOR_SUBJECT = "Replication Queue Monitor Notification";
	public static final String GS_REPLICATION_QUEUE_ATTACHEMENT_NAME = "gs_replication_queue_log";
	public static final String ENV_DETAILS_PATH = "/etc/gs-activations";

	@Activate
	private void activate(GirlScoutsReplicationQueueMonitor.Config config) {
		logger.info("Girl Scouts Replication Queue Monitor activated..!");

		this.serviceParams = new HashMap<>();
		this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");

		schedulerID = config.schedulerName().hashCode();
		emailIds = config.emailAddresses();
		logger.info("Email Recipients Configured for GirlScoutsReplicationQueueMonitor are :" + emailIds);

		maxWaitTime = config.maxWaitTime();
		logger.info("max wait time  :" + maxWaitTime);

		maxQueueSize = config.maxQueueSize();
		logger.info("max queue size  :" + maxQueueSize);

		author = isAuthor();

	}

	@Modified
	protected void modified(GirlScoutsReplicationQueueMonitor.Config config) {
		logger.info("Girl Scouts Replication Queue Monitor service is modified..!");
		removeScheduler();
		schedulerID = config.schedulerName().hashCode();
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(GirlScoutsReplicationQueueMonitor.Config config) {
		logger.info("Girl Scouts Replication Queue Monitor service is deactivated..!");
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
	private void addScheduler(GirlScoutsReplicationQueueMonitor.Config config) {
		logger.info("Running addScheduler()");
		if (config.serviceEnabled()) {
			emailIds = config.emailAddresses();
			logger.info("EmailIds Updated : " + emailIds);
			maxWaitTime = config.maxWaitTime();
			maxQueueSize = config.maxQueueSize();
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			logger.debug("Scheduler added succesfully");
		} else {
			logger.debug("GirlscourtsReplicationQueueMonitor is Disabled, no scheduler job created");
		}
	}

	@Override
	public void run() {
		logger.info("Running Girl Scouts Replication Queue Monitor Reporter Service");

		ResourceResolver resourceResolver = null;

		if (author) {
			try {
				resourceResolver = resolverFactory.getResourceResolver(serviceParams);
				montorReplicationQueue(resourceResolver);
			} catch (LoginException e) {
				logger.debug("LoginException" + e);
			} finally {
				if (null != resourceResolver) {
					resourceResolver.close();
				}
			}

		} else {
			logger.info(
					"Publisher instance - will not execute Running Girl Scouts Replication Queue Monitor Reporter Service");
		}

	}

	/**
	 * 
	 * @param resolverResourceResolver
	 */

	private void montorReplicationQueue(ResourceResolver resourceResolver) {
		logger.info("Running Girl Scouts Replication Queue Monitor");
		try {
			if (null != agentManager) {
				logger.info("Retrieving Replication Agents.");
				JSONObject agentsObj = new JSONObject();
				for (Map.Entry<String, Agent> entry : agentManager.getAgents().entrySet()) {
					JSONArray agentsDataArray = new JSONArray();
					logger.info("Agents found " + agentManager.getAgents().entrySet());
					Agent agent = entry.getValue();
					String agentId = entry.getKey();
					if (agent.isValid() && agent.isEnabled()) {
						logger.info("Agent ID " + agentId);
						ReplicationQueue replicationQueue = agent.getQueue();
						if (!replicationQueue.entries().isEmpty()) {
							logger.info("Replication Queue is not empty " + agentId);
							for (ReplicationQueue.Entry rqEntry : replicationQueue.entries()) {
								JSONObject actionItems = new JSONObject();
								ReplicationAction action = rqEntry.getAction();
								long currentTime = System.currentTimeMillis();
								long queueEntryTime = action.getTime();
								long waitTime = currentTime - queueEntryTime;
								if ((replicationQueue.entries().size() > maxQueueSize)
										|| (waitTime > TimeUnit.MINUTES.toMillis(maxWaitTime))) {
									actionItems.put("entryTime", TimeUnit.MILLISECONDS.toMinutes(waitTime));
									actionItems.put("path", action.getPath());
									agentsDataArray.put(actionItems);
									agentsObj.put(agentId, agentsDataArray);
								}

							}

						}
					}
				}
				if (agentsObj.length() > 0) {
					sendNotification(agentsObj, resourceResolver, maxQueueSize);
				} else {
					logger.info("Replication Queue is Empty");
				}
			}
		} catch (Exception e) {
			logger.debug("Error occured " + e);
		}

	}

	private void sendNotification(JSONObject agentsObj, ResourceResolver resourceResolver, int queueSize) {
		logger.info("Running sendNotification()");
		ArrayList<String> emailRecipients = new ArrayList<>();
		StringBuilder msgBody = new StringBuilder();
		try {
			if (null != emailIds) {
				for (String address : emailIds) {
					emailRecipients.add(new InternetAddress(address).toString());
				}
				StringWriter writer = new StringWriter();
				Csv csvWriter = new Csv();
				csvWriter.writeInit(writer);

				Iterator<?> iterator = agentsObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					JSONArray arr = agentsObj.getJSONArray(key);
					csvWriter.writeRow("Agent ID ", key);
					csvWriter.writeRow("Path", "Max Wait Time");
					long maxQueueTime = 0;
					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						maxQueueTime = maxQueueTime > (Long) obj.get("entryTime") ? maxQueueTime
								: (Long) obj.get("entryTime");
						csvWriter.writeRow(obj.get("path").toString(), obj.get("entryTime").toString());

					}
					msgBody.append("Max queue wait time for replication agent <b>" + key + "</b> :" + maxQueueTime
							+ "<br/><br/>");
					if (arr.length() > queueSize) {
						csvWriter.writeRow("Queue size of replication agent" + key, String.valueOf(arr.length()));
						msgBody.append(
								"Queue size of replication agent <b>" + key + "</b> :" + arr.length() + "<br/><br/>");
					}
					csvWriter.writeRow("");
					csvWriter.writeRow("");

				}
				csvWriter.close();
				writer.close();
				String csvContents = writer.toString();
				Set<GSEmailAttachment> attachments = new HashSet<>();
				String environment = getEnvironment(resourceResolver);
				String subject = environment != null ? environment + "" + GS_REPLICATION_QUEUE_MONITOR_SUBJECT
						: GS_REPLICATION_QUEUE_MONITOR_SUBJECT;

				String fileName = GS_REPLICATION_QUEUE_ATTACHEMENT_NAME + "_" + getDate();
				GSEmailAttachment attachment = new GSEmailAttachment(fileName, csvContents, null,
						GSEmailAttachment.MimeType.TEXT_CSV);
				attachments.add(attachment);
				gsEmailService.sendEmail(subject, emailRecipients, msgBody.toString(), attachments);
				logger.info("Notification sent..!");

			} else {
				logger.error("No email address found for Replication Queue Monitor Reporter service");
			}
		} catch (Exception e) {
			logger.debug("Error occured" + e);
		}
	}

	private String getEnvironment(ResourceResolver rr) {
		logger.info("Getting environment");
		String env = "";
		try {
			Resource gsActivationsRes = rr.resolve(ENV_DETAILS_PATH);
			ValueMap vm = ResourceUtil.getValueMap(gsActivationsRes);
			env = vm.get("environment", String.class);
		} catch (Exception e) {
			logger.info("PageReplicationUtil encountered error: ", e);
		}
		return env;
	}

	private String getDate() {
		String date = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd'T'HHmmssZ");
		date = formatter.format(new Date());
		return date;
	}

	private boolean isAuthor() {
		logger.info("Checking if running on author instance");
		return slingeSettings.getRunModes().contains("author");
	}

}
