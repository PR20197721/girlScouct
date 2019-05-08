package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoService;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsRepoService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsRepoServiceImpl")
public class GirlScoutsRepoServiceImpl implements GirlScoutsRepoService {

    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMServiceImpl.class);

    @Reference
    private GirlScoutsRepository girlScoutsRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public QueryResult executeQuery(String query) {
        return null;
    }

    @Override
    public Node getNode(String path) {
        return null;
    }

}
