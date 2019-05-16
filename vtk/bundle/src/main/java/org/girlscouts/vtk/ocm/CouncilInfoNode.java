package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.List;

@Node
public class CouncilInfoNode extends JcrNode implements Serializable {
    @Collection(autoUpdate = false)
    private List<MilestoneNode> milestones;

    public List<MilestoneNode> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<MilestoneNode> milestones) {
        this.milestones = milestones;
    }
}
