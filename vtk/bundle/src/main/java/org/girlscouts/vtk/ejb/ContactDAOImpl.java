package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Session;
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
import org.girlscouts.vtk.dao.ContactDAO;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;

@Component
@Service(value = ContactDAO.class)
public class ContactDAOImpl implements ContactDAO {

	@Reference
	private SessionFactory sessionFactory;

	public void save(User user, Troop troop, Contact contact)
			throws IllegalStateException, IllegalAccessException {

		// TODO PERMISSIONS HERE
		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Contact.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			if (mySession.itemExists(contact.getPath())) {
				ocm.update(contact);
			} else {
				JcrUtils.getOrCreateByPath(
						contact.getPath().substring(0,
								contact.getPath().lastIndexOf("/")),
						"nt:unstructured", mySession);
				ocm.insert(contact);
			}
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

	}

	public Contact retreive(User user, Troop troop, String contactId)
			throws IllegalStateException, IllegalAccessException {
		// TODO PERMISSIONS HERE

		Session mySession = null;
		Contact contact = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Contact.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Contact.class);

			contact = (Contact) ocm.getObject(VtkUtil.getYearPlanBase(user,
					troop)
					+ troop.getSfCouncil()
					+ "/troops/"
					+ troop.getId()
					+ "/contacts/" + contactId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sessionFactory != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		return contact;
	}

}
