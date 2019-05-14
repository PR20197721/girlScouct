package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRService;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.QueryResult;
import java.util.Map;

@Component(service = {GirlScoutsJCRService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsJCRServiceImpl")
public class GirlScoutsJCRServiceImpl implements GirlScoutsJCRService {

    private static Logger log = LoggerFactory.getLogger(GirlScoutsJCRServiceImpl.class);

    @Reference
    private GirlScoutsJCRRepository girlScoutsJCRRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public ValueMap getNode(String path) {
        return girlScoutsJCRRepository.getNode(path);
    }

    @Override
    public ValueMap getNodeById(String id) {
        return girlScoutsJCRRepository.getNodeById(id);
    }

    @Override
    public ValueMap createPath(String path) {
        return girlScoutsJCRRepository.createPath(path);
    }

    @Override
    public boolean removeNode(String path) {
        return girlScoutsJCRRepository.removeNode(path);
    }

    @Override
    public boolean setNodeProperties(String nodePath, Map<String, String> singleProps, Map<String, String[]> multiProps) {
        return girlScoutsJCRRepository.setNodeProperties(nodePath,singleProps,multiProps);
    }
}
