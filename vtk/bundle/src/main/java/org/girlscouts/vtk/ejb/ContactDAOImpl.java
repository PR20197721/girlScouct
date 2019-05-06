package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.dao.ContactDAO;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(value = ContactDAO.class)
public class ContactDAOImpl implements ContactDAO {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public void save(User user, Troop troop, Contact contact) throws IllegalStateException, IllegalAccessException {
        // TODO PERMISSIONS HERE
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Contact.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (session.itemExists(contact.getPath())) {
                ocm.update(contact);
            } else {
                JcrUtils.getOrCreateByPath(contact.getPath().substring(0, contact.getPath().lastIndexOf("/")), "nt:unstructured", session);
                ocm.insert(contact);
            }
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
    }

    public Contact retreive(User user, Troop troop, String contactId) throws IllegalStateException, IllegalAccessException {
        // TODO PERMISSIONS HERE
        Session session = null;
        Contact contact = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Contact.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Contact.class);
            contact = (Contact) ocm.getObject(VtkUtil.getYearPlanBase(user, troop) + troop.getSfCouncil() + "/troops/" + troop.getId() + "/contacts/" + contactId);
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
        return contact;
    }

}
