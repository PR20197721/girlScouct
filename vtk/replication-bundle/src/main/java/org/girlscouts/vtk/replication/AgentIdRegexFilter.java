package org.girlscouts.vtk.replication;

import java.util.regex.Pattern;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;

public class AgentIdRegexFilter implements AgentFilter {
    private Pattern pattern;

    public AgentIdRegexFilter(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean isIncluded(Agent agent) {
        return pattern.matcher(agent.getId()).matches();
    }
}
