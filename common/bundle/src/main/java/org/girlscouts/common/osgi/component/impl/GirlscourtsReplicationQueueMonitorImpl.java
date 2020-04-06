package org.girlscouts.common.osgi.component.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.osgi.configuration.GirlscoutsReplicationQueueMonitorConfiguration;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationQueue;
/**
 * 
 * @author Kiran.Boregowda
 *
 */

@Component(immediate = true, service = {Runnable.class,
		GirlscourtsReplicationQueueMonitorImpl.class})
@Designate(ocd = GirlscoutsReplicationQueueMonitorConfiguration.class)
//@ServiceVendor("Girl Scouts")
public class GirlscourtsReplicationQueueMonitorImpl implements Runnable {

	@Reference
	private SlingSettingsService slingeSettings;

	@Reference
	private Scheduler scheduler;

	@Reference
	protected ResourceResolverFactory resolverFactory;
	
	@Reference
	private GSEmailService gsEmailService;
	
	@Reference
	private AgentManager agentManager;



	private int schedulerId;

	private boolean author;

	private boolean serviceEnabled;
	private boolean sendNotifications;
	List<String> emailIds;
	private long maxWaitTime;

	GirlscoutsReplicationQueueMonitorConfiguration config;
	

	private static final Logger log = LoggerFactory.getLogger(GirlscourtsReplicationQueueMonitorImpl.class);

	@Activate
	private void activate(
			GirlscoutsReplicationQueueMonitorConfiguration config) {
		log.info("ReplicationMonitorQueueServiceImpl activated..");
		author = slingeSettings.getRunModes().contains("author");
		sendNotifications = config.sendNotifications();
		emailIds = Arrays.asList(config.emailAddresses()); 
		serviceEnabled = config.serviceEnabled();
		maxWaitTime = TimeUnit.MINUTES.toMillis(config.maxTime());
				
		log.info("sendNotifications::.."+sendNotifications);
		log.info("emailIds::.."+emailIds);
		log.info("serviceEnabled::.."+serviceEnabled);
		log.info("maxWaitTime::.."+maxWaitTime);
		
		schedulerId = config.schedulerName().hashCode();
		addScheduler(config);
		/*
		 * serviceEnabled = replicationQueueMonitorConfig.serviceEnabled();
		 * sendNotifications =
		 * replicationQueueMonitorConfig.sendNotifications(); emailIds =
		 * replicationQueueMonitorConfig.emailAddresses(); maxWaitTime =
		 * replicationQueueMonitorConfig.maxTime();
		 */

	}
	@Deactivate
	protected void deactivate(GirlscoutsReplicationQueueMonitorConfiguration config) {
		removeScheduler();
	}

	@Modified
	protected void modified(GirlscoutsReplicationQueueMonitorConfiguration config) {
		removeScheduler();
		schedulerId = config.schedulerName().hashCode();
		addScheduler(config);

	}

	private void removeScheduler() {
		scheduler.unschedule(String.valueOf(schedulerId));
	}

	private void addScheduler(GirlscoutsReplicationQueueMonitorConfiguration config) {
		if (config.serviceEnabled()) {
			ScheduleOptions scheduleOptions = scheduler
					.EXPR(config.schedulerExpression());
			scheduleOptions.name(config.schedulerName());
			scheduleOptions.canRunConcurrently(false);
			scheduler.schedule(this, scheduleOptions);
			log.info("Scheduler added");
		} else {
			log.info("Scheduler disabled");
		}

	}

	@Override
	public void run() {
		log.info("Repilication Monitor Service is Running..!");
		log.info("maxWaitTime::.."+maxWaitTime);
		if (isAuthor()) {
			log.info("serviceEnabled : "+serviceEnabled);
			log.info("checking for replication queue");
			monitorReplicationQueue();
		}
		else {
			log.info("Scheduler disabled or its not an author instance");
		}

	}

	private void monitorReplicationQueue() {
		log.info("inside monitorReplicationQueue");
		JSONObject repAgentObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		try {
			if (null != agentManager) {
				for (Map.Entry<String, Agent> entry : agentManager.getAgents()
						.entrySet()) {
					Agent agent = entry.getValue();

					if (null != agent && agent.isValid() && agent.isEnabled()) {
						ReplicationQueue replicationQueue = agent.getQueue();
						if (!replicationQueue.entries().isEmpty()) {
							for (ReplicationQueue.Entry rqEntry : replicationQueue
									.entries()) {
								JSONObject repAgentObjItem = new JSONObject();
								int i=0;
								ReplicationAction action = rqEntry.getAction();
								long queEntryTime = action.getTime();
								long currentTime = System.currentTimeMillis();
								//long freq = 86400000; //for 1 day
								//long freq = 360000; // for 6m
								long freq = maxWaitTime;
								if(currentTime-queEntryTime >freq ) {
									repAgentObjItem.put("Entry Time", action.getTime());
									repAgentObjItem.put("Path", action.getPath());
									repAgentObjItem.put("user", action.getUserId());
									repAgentObjItem.put("Type", action.getType());
									//repAgentObj.put("Replication Agent", entry.getValue());
									//repAgentObj.put(entry.getValue().toString(), repAgentObjItem);
									jsonArray.put(repAgentObjItem);
								}
								i++;
							}
							log.info(">>>>>>>>>jsonArray Queue Found :"
									+ jsonArray.toString());
							repAgentObj.put("replicationAgent", jsonArray);
							log.info(">>>>>>>>>repAgentObj Queue Found :"
									+ repAgentObj.toString());
						} else {
							log.info(
									"Agent [{}] replication queue {} is empty.",
									replicationQueue.getName());
						}

					} else {
						log.info("Agent [{}] is not valid or not enabled.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(repAgentObj != null) {
			sendNotificationsEmail(repAgentObj);
		}
	}


	public boolean isAuthor() {
		return author;
	}
	
	private void sendNotificationsEmail(JSONObject repAgentObj) {
		// TODO Auto-generated method stub
		try {
			if (sendNotifications == true) {
				log.info("Sending max time replication queue Notification");
				//log.info(">>>>>>>>> sendNotificationsEmail Kiran :" + jsonArray.toString());
			
					JSONObject jsonQueue = new JSONObject(repAgentObj.toString());
					//JSONArray jsonArray = jsonQueue.getJSONArray("JArray1");
					
					for(int i = 0; i<jsonQueue.length(); i++){
						log.info("key = " +jsonQueue.names().getString(i) + " value = " + jsonQueue.get(jsonQueue.names().getString(i)));
					}
				
				String html = "max time replication queue  is :";
				String subject = "Max Time for replication queue:";
				//this.gsEmailService.sendEmail(subject, emails, html);
				log.info("sendMaintenanceEmail::.."+emailIds);
				
				this.gsEmailService.sendEmail(subject, emailIds, html);
				
				log.info("Max Time Replication Queue Notification email sent");
			}
		} catch (Exception e) {
			log.error("Girl Scouts max time replication queue Notification encountered error: ", e);
		}
	} 
	
}
