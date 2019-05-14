package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
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
    public ValueMap getNode(String path) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Resource resource = rr.resolve(path);
            if(resource != null){
                return resource.getValueMap();
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
        return null;
    }

    @Override
    public ValueMap getNodeById(String id) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            Node node = session.getNodeByIdentifier(id);
            Resource resource = rr.resolve(node.getPath());
            return resource.getValueMap();
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
        return null;
    }

    @Override
    public ValueMap createPath(String path) {
        ResourceResolver rr = null;
        try {
            if(path != null && !path.startsWith("/content/girlscouts-vtk")) {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                Node node = JcrUtils.getOrCreateByPath(path, NodeType.NT_UNSTRUCTURED, session);
                Resource resource = rr.resolve(node.getPath());
                return resource.getValueMap();
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
        return null;
    }

    @Override
    public boolean removeNode(String path) {
        ResourceResolver rr = null;
        try {
            if(path != null && !path.startsWith("/content/girlscouts-vtk")) {
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

    @Override
    public boolean setNodeProperties(String nodePath, Map<String, String> singleProps, Map<String, String[]> multiProps) {
        if((singleProps != null && !singleProps.isEmpty() || (multiProps != null && !multiProps.isEmpty())) && nodePath != null && !nodePath.startsWith("/content/girlscouts-vtk")) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                Node node = session.getNode(nodePath);
                if(singleProps != null) {
                    for (String key : singleProps.keySet()) {
                        node.setProperty(key, singleProps.get(key));
                    }
                }
                if(multiProps != null) {
                    for (String key : multiProps.keySet()) {
                        node.setProperty(key, multiProps.get(key));
                    }
                }
                session.save();
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
        }
        return false;
    }

}
