package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlScoutsJCRRepository.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsJCRRepositoryImpl")
public class GirlScoutsJCRRepositoryImpl implements GirlScoutsJCRRepository {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsJCRRepositoryImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    @Reference
    private QueryBuilder qBuilder;

    @Activate
    private void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public boolean removeNode(String path) {
        ResourceResolver rr = null;
        try {
            if (path != null && !path.startsWith("/content/girlscouts-vtk")) {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                if (session.itemExists(path)) {
                    session.removeItem(path);
                    session.save();
                }
                return true;
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return false;
    }

}
