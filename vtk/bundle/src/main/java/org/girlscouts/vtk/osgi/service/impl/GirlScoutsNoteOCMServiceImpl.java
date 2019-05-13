package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Note;
import org.girlscouts.vtk.ocm.NoteNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsNoteOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsNoteOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsNoteOCMServiceImpl")
public class GirlScoutsNoteOCMServiceImpl implements GirlScoutsNoteOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsNoteOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Note create(Note object) {
        NoteNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Note update(Note object) {
        NoteNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Note read(String path) {
        NoteNode node = (NoteNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Note object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Note findObject(String path, Map<String, String> params) {
        NoteNode node = girlScoutsOCMRepository.findObject(path, params, NoteNode.class);
        if(node != null) {
            return NodeToModelMapper.INSTANCE.toModel(node);
        }else{
            return null;
        }
    }

    @Override
    public List<Note> findObjects(String path, Map<String, String> params) {
        List<NoteNode> nodes = girlScoutsOCMRepository.findObjects(path, params, NoteNode.class);
        if(nodes != null){
            List<Note> models = new ArrayList<>();
            nodes.forEach(noteNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(noteNode));});
            return models;
        }else{
            return null;
        }
    }
}
