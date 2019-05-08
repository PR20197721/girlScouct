package org.girlscouts.vtk.osgi.service.impl;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.ocm.*;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import java.util.*;

@Component(service = {GirlScoutsOCMRepository.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsOCMRepositoryImpl")
public class GirlScoutsOCMRepositoryImpl implements GirlScoutsOCMRepository {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMRepositoryImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    private Mapper mapper;

    @Activate
    private void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        List<Class> ocmClasses = new ArrayList<Class>();
        ocmClasses.add(TroopNode.class);
        ocmClasses.add(YearPlanNode.class);
        ocmClasses.add(MeetingENode.class);
        ocmClasses.add(NoteNode.class);
        ocmClasses.add(MeetingCanceledNode.class);
        ocmClasses.add(ActivityNode.class);
        ocmClasses.add(LocationNode.class);
        ocmClasses.add(AssetNode.class);
        ocmClasses.add(CalNode.class);
        ocmClasses.add(MilestoneNode.class);
        ocmClasses.add(SentEmailNode.class);
        ocmClasses.add(JcrCollectionHoldStringNode.class);
        ocmClasses.add(AttendanceNode.class);
        ocmClasses.add(AchievementNode.class);
        ocmClasses.add(JcrNode.class);
        mapper = new AnnotationMapperImpl(ocmClasses);
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public <T extends JcrNode> T create(T object){
        //TODO need to check if path to object exists if not then create it
        /*if (!session.itemExists(troop.getPath() + "/lib/meetings/")) {
            ocm.insert(new JcrNode(troop.getPath() + "/lib"));
            ocm.insert(new JcrNode(troop.getPath() + "/lib/meetings"));
            ocm.save();
        }*/
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if(ocm.objectExists(object.getPath())){
                return update(object);
            }else{
                ocm.insert(object);
                ocm.save();
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
        return read(object.getPath());
    }

    @Override
    public <T extends JcrNode> T update(T object){
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
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
        return read(object.getPath());
    }

    @Override
    public Object read(String path){
        ResourceResolver rr = null;
        T object = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            object = (T)ocm.getObject(path);
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
        return object;
    }

    @Override
    public <T extends JcrNode> boolean delete(T object){
        ResourceResolver rr = null;
        boolean isRemoved = false;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            ocm.remove(object.getPath());
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
    }

    @Override
    public <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            Query query = getQuery(path, params, clazz, ocm);
            return (T) ocm.getObject(query);
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
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            Query query = getQuery(path, params, clazz, ocm);
            return (List<T>) ocm.getObjects(query);
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

    private Query getQuery(String path, Map<String, String> params, Class clazz, ObjectContentManager ocm) {
        QueryManager queryManager = ocm.getQueryManager();
        Filter filter = queryManager.createFilter(clazz);
        filter.setScope(path);
        if(params != null){
            for(String paramName:params.keySet()){
                filter.addEqualTo(paramName,params.get(paramName));
            }
        }
        return queryManager.createQuery(filter);
    }

}
