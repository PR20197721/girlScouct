package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.google.gson.Gson;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.exception.VtkException;
import org.girlscouts.vtk.ocm.*;
import org.girlscouts.vtk.osgi.cache.VTKMeetingLibraryCache;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.*;

@Component(service = {GirlScoutsOCMRepository.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsOCMRepositoryImpl")
public class GirlScoutsOCMRepositoryImpl implements GirlScoutsOCMRepository {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMRepositoryImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private VTKMeetingLibraryCache libraryCache;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    private Mapper mapper;

    @Activate
    private void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        List<Class> ocmClasses = new ArrayList<Class>();
        ocmClasses.add(AchievementNode.class);
        ocmClasses.add(ActivityNode.class);
        ocmClasses.add(AssetNode.class);
        ocmClasses.add(AttendanceNode.class);
        ocmClasses.add(CalNode.class);
        ocmClasses.add(CouncilInfoNode.class);
        ocmClasses.add(CouncilNode.class);
        ocmClasses.add(FinanceNode.class);
        ocmClasses.add(JcrCollectionHoldStringNode.class);
        ocmClasses.add(JcrNode.class);
        ocmClasses.add(LocationNode.class);
        ocmClasses.add(MeetingCanceledNode.class);
        ocmClasses.add(MeetingENode.class);
        ocmClasses.add(MeetingNode.class);
        ocmClasses.add(MilestoneNode.class);
        ocmClasses.add(NoteNode.class);
        ocmClasses.add(SentEmailNode.class);
        ocmClasses.add(TroopNode.class);
        ocmClasses.add(YearPlanComponentNode.class);
        ocmClasses.add(YearPlanNode.class);
        mapper = new AnnotationMapperImpl(ocmClasses);
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public <T extends JcrNode> T create(T object) {
        if (object != null && object.getPath() != null && !object.getPath().startsWith("/content/girlscouts-vtk")) {
            log.debug("Creating node at:", object.getPath());
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                if (!ocm.objectExists(object.getPath())) {
                    object.setCreatedDate(new GregorianCalendar());
                    object.setLastModifiedDate(new GregorianCalendar());
                    String path = object.getPath();
                    String parentPath = path.substring(0, path.lastIndexOf("/"));
                    if (!session.itemExists(parentPath)) {
                        JcrUtil.createPath(parentPath, NodeType.NT_UNSTRUCTURED, session);
                    }
                    log.debug("Inserting node at: " + object.getPath());
                    ocm.insert(object);
                    ocm.save();
                } else {
                    throw new VtkException("Node at: " + object.getPath() + " already exists!");
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
            return (T) read(object.getPath());
        } else {
            return null;
        }
    }

    @Override
    public <T extends JcrNode> T update(T object) {
        if (object != null && object.getPath() != null && !object.getPath().startsWith("/content/girlscouts-vtk")) {
            log.debug("Updating node at:", object.getPath());
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                object.setLastModifiedDate(new GregorianCalendar());
                log.debug("Updating node at: " + object.getPath());
                ocm.update(object);
                ocm.save();
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
            return (T) read(object.getPath());
        } else {
            return null;
        }

    }

    @Override
    public <T extends JcrNode> T read(String path) {
        T object = null;
        if(path != null && path.startsWith("/content/girlscouts-vtk/")){
            if(libraryCache.contains(path)){
                log.debug("Reading node at: " + path+ " from cache.");
                object = libraryCache.read(path);
            }
        }
        if(object == null){
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                log.debug("Reading node at: " + path);
                object = (T) ocm.getObject(path);
                if(path != null && path.startsWith("/content/girlscouts-vtk/")){
                    libraryCache.write(path,  object);
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
        }
        return object;
    }

    @Override
    public <T extends JcrNode> boolean delete(T object) {
        log.error("CONVERTED OBJECT path={} object={}", object.getPath(), new Gson().toJson(object));
        if (object != null && object.getPath() != null && !object.getPath().startsWith("/content/girlscouts-vtk")) {
            ResourceResolver rr = null;
            boolean isRemoved = false;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                log.debug("Removing node at: " + object.getPath());
                ocm.remove(object.getPath());
                ocm.save();
                isRemoved = true;
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
            return isRemoved;
        } else {
            return false;
        }
    }

    @Override
    public <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz) {
        List<T> results = findObjects(path, params, clazz);
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz) {
        List<T> results = null;
        String key = path+":"+clazz.getName();
        if(path != null && path.startsWith("/content/girlscouts-vtk")){
            if(libraryCache.containsList(key)){
                log.debug("Loading "+clazz.getName()+" results for "+path+" from cache");
                results = libraryCache.readList(key);
            }
        }
        if(results == null){
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                if (session.itemExists(path)) {
                    ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                    String query = getJCRSQL2Query(path, params, clazz.getName(), session);
                    log.debug("Looking up OCM Objects :" + query);
                    results =  (List<T>) ocm.getObjects(query, javax.jcr.query.Query.JCR_SQL2);
                    if(path != null && path.startsWith("/content/girlscouts-vtk")){
                        libraryCache.writeList(key, results);
                    }
                } else {
                    log.debug("Path does not exist: " + path);
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
        }
        return results;
    }

    @Override
    public <T extends JcrNode> List<T> findObjectsCustomQuery(String query, Class<T> clazz) {
        List<T> results = null;
        String key = query;
        if(query != null && query.contains("ISDESCENDANTNODE([/content/girlscouts-vtk/")){
            if(libraryCache.containsList(key)){
                log.debug("Loading "+clazz.getName()+" results for "+query+" from cache");
                results = libraryCache.readList(key);
            }
        }
        if(results == null){
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                log.debug("Looking up OCM Objects :" + query);
                results =  (List<T>) ocm.getObjects(query, javax.jcr.query.Query.JCR_SQL2);
                if(query != null && query.contains("ISDESCENDANTNODE([/content/girlscouts-vtk/")){
                    libraryCache.writeList(key, results);
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
        }
        return results;
    }

    private String getJCRSQL2Query(String path, Map<String, String> params, String ocm_classname, Session session) {
        try {
            String sql = "SELECT s.* FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([" + path + "]) AND s.[ocm_classname] = '" + ocm_classname + "'";
            final QueryManager queryManager = session.getWorkspace().getQueryManager();
            if (params != null && !params.isEmpty()) {
                for (String paramName : params.keySet()) {
                    sql += " AND s.[" + paramName + "] = '" + params.get(paramName) + "'";
                }
            }
            Query query = queryManager.createQuery(sql, Query.JCR_SQL2);
            return query.getStatement();
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return null;
    }
}
