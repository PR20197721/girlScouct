package org.girlscouts.common.osgi.cron.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

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

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationQueue;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = GirlscourtsReplicationQueueMonitor.Config.class)
public class GirlscourtsReplicationQueueMonitor implements Runnable {

	@ObjectClassDefinition(name = "Girl Scouts Replication Queue Monitor Service Configuration", description = "Girl Scouts Replication Queue Monitor Service Configuration")
	public static @interface Config {
		/**
		 * schedulerName
		 * 
		 * @return String name
		 */
		@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
		public String schedulerName()

		default "Girl Scouts Replication Queue Monitor";

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
		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: runs every 15 minutes.", type = AttributeType.STRING)
		String schedulerExpression() default "0 0/15 * 1/1 * ? *";

		/**
		 * queueTimeHolderPath
		 * 
		 * @return queueTimeHolderPath
		 */

		@AttributeDefinition(name = "queueTimeHolderPath", description = "queueTimeHolderPath", type = AttributeType.STRING)
		String queueTimeHolderPath();

	}

	private static final Logger logger = LoggerFactory.getLogger(GirlscourtsReplicationQueueMonitor.class);

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Reference
	private Scheduler scheduler;

	@Reference
	private SlingSettingsService slingeSettings;

	@Reference
	private AgentManager agentManager;

	private int schedulerID;

	private boolean author = false;

	private String queTimeHolderPath;

	protected Map<String, Object> serviceParams;

	@Activate
	private void activate(GirlscourtsReplicationQueueMonitor.Config config) {

		logger.info("Girl Scouts Replication Queue Monitor Service activated..!");

		this.serviceParams = new HashMap<>();
		this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");

		schedulerID = config.schedulerName().hashCode();
		queTimeHolderPath = config.queueTimeHolderPath();
		author = isAuthor();
	}

	@Modified
	protected void modified(GirlscourtsReplicationQueueMonitor.Config config) {
		removeScheduler();
		schedulerID = config.schedulerName().hashCode();
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(GirlscourtsReplicationQueueMonitor.Config config) {
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
	private void addScheduler(GirlscourtsReplicationQueueMonitor.Config config) {
		if (config.serviceEnabled()) {
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
		logger.info("Running Girl Scouts Replication Queue Monitor Service");
		if (author) {
			monitorReplicationQueue();
		} else {
			logger.info("Publisher instance - will not execute Running Girl Scouts Replication Queue Monitor Service");
		}

	}

	private void monitorReplicationQueue() {
		logger.info("Running monitorReplicationQueue()");
		ResourceResolver resourceResolver = null;
		Session session = null;
		try {
			resourceResolver = resolverFactory.getServiceResourceResolver(this.serviceParams);
			session = resourceResolver.adaptTo(Session.class);
			Node queTimeHolderNode = resourceResolver.resolve(queTimeHolderPath).adaptTo(Node.class);
			if (null != agentManager) {
				for (Map.Entry<String, Agent> entry : agentManager.getAgents().entrySet()) {
					Agent agent = entry.getValue();
					String agentId = entry.getKey();

					if (agent.isValid() && agent.isEnabled()) {
						logger.info("Agent ID " + agentId);
						ReplicationQueue replicationQueue = agent.getQueue();
						long waitTime = 0;
						if (queTimeHolderNode.hasProperty(agentId)) {
							waitTime = queTimeHolderNode.getProperty(agentId).getLong();
						}
						if (!replicationQueue.entries().isEmpty()) {
							for (ReplicationQueue.Entry rqEntry : replicationQueue.entries()) {
								ReplicationAction action = rqEntry.getAction();
								long queueEntryTime = action.getTime();
								long currentTime = System.currentTimeMillis();
								waitTime = waitTime > (currentTime - queueEntryTime) ? waitTime
										: currentTime - queueEntryTime;
							}

						}

						queTimeHolderNode.setProperty(agentId, waitTime);
					}

				}
				session.save();
			}

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

	protected boolean isAuthor() {
		logger.info("Checking if running on author instance");
		return slingeSettings.getRunModes().contains("author");

	}

}
