package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
import javax.jcr.query.QueryResult;
import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlScoutsJCRRepository.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsJCRRepositoryImpl")
public class GirlScoutsJCRRepositoryImpl implements GirlScoutsJCRRepository {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsJCRRepositoryImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private QueryBuilder qBuilder;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    private void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public QueryResult executeQuery(String query) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(query, Query.SQL);
            return q.execute();
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
    public SearchResult executeQuery(Map<String, String> predicates, Integer start, Integer hitsPerPage, Boolean excerpt) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            com.day.cq.search.Query query = qBuilder.createQuery(PredicateGroup.create(predicates), session);
            if(excerpt != null) {
                query.setExcerpt(excerpt);
            }
            if(start != null){
                query.setStart(start);
            }
            if(hitsPerPage != null){
                query.setHitsPerPage(hitsPerPage);
            }
            return query.getResult();
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
    public Node getNode(String path) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Resource resource = rr.resolve(path);
            if(resource != null){
                return resource.adaptTo(Node.class);
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
    public Node getNodeById(String id) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            return session.getNodeByIdentifier(id);
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
    public Node createPath(String path) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            return JcrUtils.getOrCreateByPath(path, NodeType.NT_UNSTRUCTURED, session);
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
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            if(session.itemExists(path)){
                session.removeItem(path);
                session.save();
            }
            return true;
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
        if((singleProps != null && !singleProps.isEmpty() || (multiProps != null && !multiProps.isEmpty()))) {
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
