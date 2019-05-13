package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.ocm.*;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.utils.VtkException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
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
    public <T extends JcrNode> T create(T object){
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if(!ocm.objectExists(object.getPath())){
                object.setCreatedDate(new GregorianCalendar());
                String path = object.getPath();
                String parentPath = path.substring(0,path.lastIndexOf("/"));
                if(!session.itemExists(parentPath)) {
                    JcrUtil.createPath(parentPath, NodeType.NT_UNSTRUCTURED, session);
                }
                log.debug("Inserting node at: "+object.getPath());
                ocm.insert(object);
                ocm.save();
            }else{
                throw new VtkException("Node at: "+object.getPath()+" already exists!");
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
    }

    @Override
    public <T extends JcrNode> T update(T object){
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            object.setLastModifiedDate(new GregorianCalendar());
            log.debug("Updating node at: "+object.getPath());
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
    }

    @Override
    public Object read(String path){
        ResourceResolver rr = null;
        Object object = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            log.debug("Reading node at: "+path);
            object = ocm.getObject(path);
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
            log.debug("Removing node at: "+object.getPath());
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
            if(session.itemExists(path)) {
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                Query query = getQuery(path, params, clazz, ocm);
                log.debug("Looking up Object of type" + clazz.getName() + " at: " + path);
                return (T) ocm.getObject(query);
            }else{
                log.debug("Path does not exist: "+path);
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
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            if(session.itemExists(path)) {
                ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
                Query query = getQuery(path, params, clazz, ocm);
                log.debug("Looking up Objects of type" +clazz.getName()+" at: "+path);
                return (List<T>) ocm.getObjects(query);
            }else{
                log.debug("Path does not exist: "+path);
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

    private Query getQuery(String path, Map<String, String> params, Class clazz, ObjectContentManager ocm) {
        QueryManager queryManager = ocm.getQueryManager();
        Filter filter = queryManager.createFilter(clazz);
        filter.setScope(path+"/");
        if(params != null){
            for(String paramName:params.keySet()){
                filter.addEqualTo(paramName,params.get(paramName));
            }
        }
        return queryManager.createQuery(filter);
    }

}
