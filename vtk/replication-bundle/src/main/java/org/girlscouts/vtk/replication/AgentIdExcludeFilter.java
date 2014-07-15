package org.girlscouts.vtk.replication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;

public class AgentIdExcludeFilter implements AgentFilter {
    private Set<String> ids;

    public AgentIdExcludeFilter(String... ids) {
        this.ids = new HashSet<String>(Arrays.asList(ids));
    }

    public boolean isIncluded(Agent agent) {
        return !ids.contains(agent.getId());
    }
}
