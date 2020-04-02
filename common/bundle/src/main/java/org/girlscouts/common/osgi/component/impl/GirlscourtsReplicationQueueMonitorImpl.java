package org.girlscouts.common.osgi.component.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.girlscouts.common.osgi.component.GirlscourtsReplicationQueueMonitor;
import org.girlscouts.common.osgi.configuration.GirlscoutsReplicationQueueMonitorConfiguration;
import org.girlscouts.common.osgi.configuration.GirlscoutsServerLoadMonitorConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationQueue;

@Component(service = { GirlscourtsReplicationQueueMonitor.class,
		Runnable.class  }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscourtsReplicationQueueMonitorImpl")
@Designate(ocd = GirlscoutsReplicationQueueMonitorConfiguration.class)
public class GirlscourtsReplicationQueueMonitorImpl implements Runnable,GirlscourtsReplicationQueueMonitor{

	private static final Logger log = LoggerFactory.getLogger(GirlscourtsReplicationQueueMonitor.class);
	private GirlscoutsServerLoadMonitorConfiguration config;
	
	 @Reference
	    private AgentManager agentManager;

	    private static final int MAX_WAIT_TIME = 3;
		
	    private String host = "";
		List<String> emails;
		
		@Activate
		private void activate(GirlscoutsServerLoadMonitorConfiguration config) {
			this.config = config;
			this.emails = Arrays.asList(config.emailAddresses());
			try {
				InetAddress addr;
				addr = InetAddress.getLocalHost();
				this.host = addr.getHostName();
			} catch (UnknownHostException e) {
				log.error("Girl Scouts Replication Queue Monitor encountered error: ", e);
			}
			log.info("Girl Scouts Replication Queue Monitor Activated.");
		}  
	    
	    @Override
	    public void run() {
	    	//checking agents are configured or not
	        if(agentManager.getAgents().isEmpty()) {
	        	log.info("No agents are configured in this instance");
	        }

	        for (Map.Entry<String, Agent> entry : agentManager.getAgents().entrySet()) {
	            Agent agent = entry.getValue();

	            // only check against valid/enabled agents
	            if(agent.isValid() && agent.isEnabled()) {
	                ReplicationQueue replicationQueue = agent.getQueue();
	                
	                if(!replicationQueue.entries().isEmpty()) {
	                    ReplicationQueue.Entry firstEntry = replicationQueue.entries().get(0);
	                    if(firstEntry.getNumProcessed() > MAX_WAIT_TIME) {
	                    	log.warn("Agent [{}] max wait time: {}, expected number of retries <= {}", agent.getId(), firstEntry.getNumProcessed(), MAX_WAIT_TIME);
	                    }
	                } else {
	                	log.debug("Agent [{}] replication queue {} is empty.", replicationQueue.getName());
	                }
	            } else {
	            	log.debug("Agent [{}] is not valid and/or not enabled.");
	            }
	        }
	       
	    }

	}


