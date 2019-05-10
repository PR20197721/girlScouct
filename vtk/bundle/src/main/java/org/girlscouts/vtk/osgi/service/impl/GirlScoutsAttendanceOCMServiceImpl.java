package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.ocm.AttendanceNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsAttendanceOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsAttendanceOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsAttendanceOCMServiceImpl")
public class GirlScoutsAttendanceOCMServiceImpl implements GirlScoutsAttendanceOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsAttendanceOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Attendance create(Attendance object) {
        AttendanceNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Attendance update(Attendance object) {
        AttendanceNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Attendance read(String path) {
        AttendanceNode node = (AttendanceNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Attendance object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Attendance findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, AttendanceNode.class));
    }

    @Override
    public List<Attendance> findObjects(String path, Map<String, String> params) {
        List<AttendanceNode> nodes = girlScoutsOCMRepository.findObjects(path, params, AttendanceNode.class);
        List<Attendance> models = new ArrayList<>();
        nodes.forEach(attendanceNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(attendanceNode));});
        return models;
    }

}
