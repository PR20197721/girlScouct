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
import org.girlscouts.vtk.dao.SearchDAO;

@Component
@Service(value = SearchDAO.class)
public class Search implements SearchDAO {

	@Reference
	private SessionFactory sessonFactory;

	@Activate
	void activate() {
	}

	public List<String> getData(String query) {
		Session session = null;
		List<String> matched = new ArrayList<String>();
		try {
			session = sessonFactory.getSession();
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query q = qm
					.createQuery(
							"select jcr:path, excerpt(.) from nt:resource    where jcr:path like '/content/dam/%' and  contains(., '"
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
				if (session != null)
					sessonFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

}
