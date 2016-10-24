package org.girlscouts.vtk.impl.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.helpers.DataImportTimestamper;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;

@Component
@Service
public class DateImporterTimestamperImpl implements DataImportTimestamper {
	private static final String TIMESTAMP_PATH = "/"+VtkUtil.getYearPlanBase(null, null)+"/last-import-timestamp";
	private static final String TIMESTAMP_PROP = "timstamp";

	private static final Logger log = LoggerFactory
			.getLogger(DateImporterTimestamperImpl.class);

	private Date timestamp;

	@Reference
	private SessionFactory pool;

	@Reference
	private SlingRepository repository;

	@Activate
	private void loadFromJcr() {
		Session session = null;
		try {
			session = pool.getSession();
			timestamp = session
					.getProperty(TIMESTAMP_PATH + "/" + TIMESTAMP_PROP)
					.getDate().getTime();
		} catch (RepositoryException e) {
			log.warn("Error reading timestamp from repository. Using now as the timestamp.");
			setTimestamp();
		} finally {
			try {
				if (session != null)
					pool.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void saveToJcr(Date date) {
		Session session = null;
		try {
			session = pool.getSession();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			JcrUtil.createPath(TIMESTAMP_PATH, "nt:unstructured", session);
			session.getNode(TIMESTAMP_PATH).setProperty(TIMESTAMP_PROP, cal);
			session.save();
		} catch (RepositoryException e) {
			log.error("Error writing timestamp to repository.");
		} finally {
			try {
				if (session != null)
					pool.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * private Session getSession() throws RepositoryException {
	 * 
	 * if (session == null) { if (repository == null) {
	 * log.error("Cannot get reference to Sling repository"); throw new
	 * RepositoryException(); } session = repository.loginAdministrative(null);
	 * } } return session; }
	 */
	public Date getTimestamp() {
		if (timestamp == null) {
			loadFromJcr();
		}
		return timestamp;
	}

	public void setTimestamp() {
		setTimestamp(new Date());
	}

	public void setTimestamp(Date date) {
		timestamp = date;
		saveToJcr(date);
	}
}
