package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.List;

public class CouncilInfo extends JcrNode implements Serializable {

    private List<Milestone> milestones;

    public CouncilInfo() {
    }

    public CouncilInfo(String path) {
        this.setPath(path);
    }

    public java.util.List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(java.util.List<Milestone> milestones) {
        this.milestones = milestones;
    }
}
