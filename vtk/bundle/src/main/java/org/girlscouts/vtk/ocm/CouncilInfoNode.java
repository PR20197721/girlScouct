package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

@Node
public class CouncilInfoNode extends JcrNode implements Serializable {
    @Collection(autoUpdate = false)
    private List<MilestoneNode> milestones;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public List<MilestoneNode> getMilestones() {
        logger.error("Getting Milestones");
        return milestones;
    }

    public void setMilestones(List<MilestoneNode> milestones) {
        this.milestones = milestones;
    }
}
