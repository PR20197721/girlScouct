package org.girlscouts.vtk.replication;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;

public class OutboxAgentFilter implements AgentFilter {
    private static String OUTBOX = "outbox";

    public boolean isIncluded(Agent agent) {
        // TODO: by ID??? Better way???
        return agent.getId().equals(OUTBOX);
    }
}
