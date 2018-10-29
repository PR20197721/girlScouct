package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.vtk.dao.SearchDAO;

@Component
@Service(value = SearchDAO.class)
public class Search implements SearchDAO {

	@Reference
	private SessionFactory sessionFactory;

	@Activate
	void activate() {
	}

	public List<String> getData(String query) {
		Session session = null;
		ResourceResolver rr= null;
		List<String> matched = new ArrayList<String>();
		try {
			rr = sessionFactory.getResourceResolver();
			session = rr.adaptTo(Session.class);
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query q = qm
					.createQuery(
							"select jcr:path, excerpt(.) from nt:resource    where isdescendantnode( '/content/dam/' ) and  contains(., '"
									+ query + "')", Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("rep:excerpt(.)");
				matched.add(excerpt.getString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if( rr!=null )
					sessionFactory.closeResourceResolver( rr );
				if (session != null)
					session.logout();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

}
