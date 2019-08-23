package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.CouncilInfo;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.ocm.CouncilInfoNode;
import org.girlscouts.vtk.ocm.MilestoneNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsCouncilInfoOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsCouncilInfoOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsCouncilInfoOCMServiceImpl")
public class GirlScoutsCouncilInfoOCMServiceImpl implements GirlScoutsCouncilInfoOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsCouncilInfoOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public CouncilInfo create(CouncilInfo object) {
        CouncilInfoNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public CouncilInfo update(CouncilInfo object) {
        CouncilInfoNode councilInfoNode = NodeToModelMapper.INSTANCE.toNode(object);

        //updates milestone children of councilInfo
        //make sure this loop is done before updating the councilinfo node itself, otherwise the
        //councilInfo node will not have the updated milstone children
        List<MilestoneNode> milestoneNodes = councilInfoNode.getMilestones();
        for (MilestoneNode milestoneNode : milestoneNodes){
            if (milestoneNode.getPath() == null){
                milestoneNode.setPath(councilInfoNode.getPath() + "/milestones/" + milestoneNode.getUid());
                girlScoutsOCMRepository.create(milestoneNode);
            } else {
                girlScoutsOCMRepository.update(milestoneNode);
            }
        }
        councilInfoNode = girlScoutsOCMRepository.update(councilInfoNode);


        //the councilInfoNode will have all milestones, even those that have been deleted
        //to find out which ones were removed, we must compare the two lists
        //TODO There must be a quicker way of doing this
        List<MilestoneNode> newMilestones = milestoneNodes;
        List<MilestoneNode> oldMilestones = councilInfoNode.getMilestones();
        for (MilestoneNode oldMilestone : oldMilestones){
            boolean stillExists = false;
            for (MilestoneNode newMilestone : newMilestones){
                if (oldMilestone.getPath().equals(newMilestone.getPath())){
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists){
                girlScoutsOCMRepository.delete(oldMilestone);
            }
        }

        councilInfoNode.setMilestones(newMilestones);

        return NodeToModelMapper.INSTANCE.toModel(councilInfoNode);
    }

    @Override
    public CouncilInfo read(String path) {
        CouncilInfoNode node = (CouncilInfoNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(CouncilInfo object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public CouncilInfo findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, CouncilInfoNode.class));
    }

    @Override
    public List<CouncilInfo> findObjects(String path, Map<String, String> params) {
        List<CouncilInfoNode> nodes = girlScoutsOCMRepository.findObjects(path, params, CouncilInfoNode.class);
        List<CouncilInfo> models = new ArrayList<>();
        nodes.forEach(CouncilInfoNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(CouncilInfoNode));
        });
        return models;
    }

}
