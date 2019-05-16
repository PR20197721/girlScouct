package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.osgi.service.GirlScoutsJCRRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean removeNode(String path) {
        return girlScoutsJCRRepository.removeNode(path);
    }

}
