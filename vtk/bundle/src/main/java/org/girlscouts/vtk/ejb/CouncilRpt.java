package org.girlscouts.vtk.ejb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataSource;
import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.utils.VtkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;


@Component(service = { CouncilRpt.class}, immediate = true, name = "org.girlscouts.vtk.ejb.CouncilRpt")
@Designate(ocd = CouncilRptConfiguration.class)
public class CouncilRpt {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private CouncilRptConfiguration config;
	
	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	ConfigManager configManager;
	
	@Reference
	private SlingSettingsService slingSettings;
	
	@Activate
	void activate() {
	}

	public java.util.List<String> getActivityRpt(String sfCouncil) {
		ResourceResolver rr= null;
		javax.jcr.Session s = null;
		java.util.List<String> activities = new java.util.ArrayList<String>();
		String sql1 = "select jcr:path "
				+ " from nt:base "
				+ " where isdescendantnode( '/vtk"
				+ VtkUtil.getCurrentGSYear()
				+ "/"
				+ sfCouncil
				+ "/troops/') and ocm_classname='org.girlscouts.vtk.models.Activity'";
		try {
			rr = sessionFactory.getResourceResolver();
			s = rr.adaptTo(Session.class);
			javax.jcr.query.QueryManager qm = s.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql1,
					javax.jcr.query.Query.SQL);
			javax.jcr.query.QueryResult result = q.execute();
			for (javax.jcr.query.RowIterator it = result.getRows(); it
					.hasNext();) {
				javax.jcr.query.Row r = it.nextRow();
				
				
				
				String path = r.getValue("jcr:path").getString();
				if (path.indexOf("/yearPlan") != -1) {
					String yp = path
							.substring(0, path.indexOf("/yearPlan") + 9);
					if (!activities.contains(yp))
						activities.add(yp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if( rr!=null )
					sessionFactory.closeResourceResolver( rr );
				if (s != null)
					s.logout();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return activities;
	}

	public java.util.List<CouncilRptBean> getRpt(String sfCouncil) {

		java.util.List<CouncilRptBean> container = new java.util.ArrayList<CouncilRptBean>();
		javax.jcr.Session session = null;
		ResourceResolver rr= null;
		java.util.List<org.girlscouts.vtk.models.YearPlanRpt> yprs = new java.util.ArrayList<org.girlscouts.vtk.models.YearPlanRpt>();
		String sql = "select  name, altered, refId,jcr:path,excerpt(.) "
				+ " from nt:base "
				+ " where isdescendantnode( '"
				+ VtkUtil.getYearPlanBase(null, null)
				+ sfCouncil
				+ "/troops/') and ocm_classname='org.girlscouts.vtk.models.YearPlan'";

		java.util.List<String> activities = getActivityRpt(sfCouncil);
		javax.jcr.query.QueryResult result = null;
		try {
			rr = sessionFactory.getResourceResolver();
			session = rr.adaptTo(Session.class);
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			result = q.execute();
			for (javax.jcr.query.RowIterator it = result.getRows(); it
					.hasNext();) {
				String yearPlanName = "", libPath = "", ageGroup = "";
				boolean isAltered = false;
				javax.jcr.query.Row r = it.nextRow();
				String path = r.getValue("jcr:path").getString();
				try {
					isAltered = r.getValue("altered").getBoolean();
				} catch (Exception e) {
				}
				try {
					yearPlanName = r.getValue("name").getString();
				} catch (Exception e) {
				}
				try {
					libPath = r.getValue("refId").getString();
				} catch (Exception e) {
				}

				String troopName = "";
				Node troop = null;
				try {
					troop = r.getNode().getParent();
					troopName = troop.getProperty("sfTroopName")
							.getString();
				}catch(Exception e){
					logger.warn("Unable to resolve troop name for troop : " + r.getPath(), e);
				}

				if (troop != null && (libPath == null
						|| libPath.equals("")
						|| (yearPlanName != null && yearPlanName.trim()
								.toLowerCase().equals("custom year plan")))) {
					try {
						libPath = troop.getProperty("sfTroopAge").getString()
								.toLowerCase().substring(2);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (libPath.contains("brownie"))
					ageGroup = "brownie";
				else if (libPath.contains("daisy"))
					ageGroup = "daisy";
				else if (libPath.contains("junior"))
					ageGroup = "junior";

				else if (libPath.contains("senior"))
					ageGroup = "senior";
				else if (libPath.contains("cadette"))
					ageGroup = "cadette";
				else if (libPath.contains("ambassador"))
					ageGroup = "ambassador";
				else if (libPath.contains("multi-level"))
					ageGroup = "multi-level";
				CouncilRptBean crb = new CouncilRptBean();
				crb.setYearPlanName(yearPlanName);
				crb.setAltered(isAltered);
				crb.setLibPath(libPath);
				crb.setAgeGroup(ageGroup);
				crb.setYearPlanPath(path);
				crb.setTroopName(troopName);
				try {
					crb.setTroopId(path.split("/")[4]);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (activities.contains(path))
					crb.setActivity(true);
				container.add(crb);
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
		return container;
	}

	public void fmtRpt(java.util.List<CouncilRptBean> results) {
	}

	public Set<String> getDistinctPlanNames(
			java.util.List<CouncilRptBean> results) {
		Set<String> container = new HashSet<String>();
		for (CouncilRptBean bean : results) {
			container.add(bean.getYearPlanName());
		}
		return container;
	}

	public java.util.List<CouncilRptBean> getCollection_byAgeGroup(
			java.util.List<CouncilRptBean> results, final String ageGroup) {

		java.util.List<CouncilRptBean> container = (java.util.List<CouncilRptBean>) CollectionUtils
				.select(results, new Predicate<CouncilRptBean>() {
					public boolean evaluate(CouncilRptBean o) {
						return o.getAgeGroup().equals(ageGroup);
					}
				});
		return container;
	}

	public int countAltered(java.util.List<CouncilRptBean> results) {
		int countAltered = CollectionUtils.countMatches(results,
				new Predicate() {
					public boolean evaluate(Object o) {
						return ((CouncilRptBean) o).isAltered() == true;
					}
				});
		return countAltered;
	}

	public java.util.List<CouncilRptBean> getCollection_byYearPlanName(
			java.util.List<CouncilRptBean> results, final String ageGroup) {

		Collection<CouncilRptBean> container = CollectionUtils.select(results,
				new Predicate<CouncilRptBean>() {
					public boolean evaluate(CouncilRptBean o) {
						return o.getYearPlanName().equals(ageGroup);
					}
				});
		return (java.util.List<CouncilRptBean>) container;
	}

	public int countActivity(java.util.List<CouncilRptBean> results) {
		int countActivity = CollectionUtils.countMatches(results,
				new Predicate() {
					public boolean evaluate(Object o) {
						return ((CouncilRptBean) o).isActivity() == true;
					}
				});
		return countActivity;
	}

	public java.util.Map<String, String> getTroopNames(String councilId,
			String yearPlanPath) {
		java.util.Map<String, String> container = new java.util.TreeMap<String, String>();
		javax.jcr.Session s = null;
		ResourceResolver rr= null;
		String sql = "select parent.sfTroopId, parent.sfTroopName from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) "
				+ " where (isdescendantnode (parent, ["
				+ VtkUtil.getYearPlanBase(null, null)
				+ councilId
				+ "/troops/]))  and "
				+ " parent.ocm_classname='org.girlscouts.vtk.models.Troop' and child.refId like '"
				+ yearPlanPath + "%'";
		javax.jcr.query.QueryResult result = null;
		try {
			rr = sessionFactory.getResourceResolver();
			s = rr.adaptTo(Session.class);
			javax.jcr.query.QueryManager qm = s.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			result = q.execute();
			for (javax.jcr.query.RowIterator it = result.getRows(); it
					.hasNext();) {
				javax.jcr.query.Row r = it.nextRow();
				String troopId = r.getValue("parent.sfTroopId").getString();
				String troopName = r.getValue("parent.sfTroopName").getString();
				container.put(troopId, troopName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if( rr!=null )
					sessionFactory.closeResourceResolver( rr );
				if (s != null)
					s.logout();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return container;
	}

	public Map<String, String> getDistinctPlanNamesPath(
			java.util.List<CouncilRptBean> results) {
		Map<String, String> container = new TreeMap<String, String>();
		for (CouncilRptBean bean : results) {
			container.put(bean.getLibPath(), bean.getYearPlanName());
		}
		return container;

	}

	public java.util.List<CouncilRptBean> getCollection_byYearPlanPath(
			java.util.List<CouncilRptBean> results, final String yearPlanPath) {

		Collection<CouncilRptBean> container = CollectionUtils.select(results,
				new Predicate<CouncilRptBean>() {
					public boolean evaluate(CouncilRptBean o) {
						return o.getLibPath().equals(yearPlanPath);
					}
				});
		return (java.util.List<CouncilRptBean>) container;
	}

	public Map<String, String> getDistinctPlanByName(
			java.util.List<CouncilRptBean> results) {
		Map<String, String> container = new TreeMap<String, String>();
		for (CouncilRptBean bean : results) {
			container.put(bean.getYearPlanName(), bean.getLibPath());
		}
		return container;
	}
	
	public String saveRpt( StringBuffer sb ){
		
		String rptId= "RPT"+new java.util.Date().getTime();
		int currYear= VtkUtil.getCurrentGSYear();
		javax.jcr.Node node = null;
		Session session = null;
		ResourceResolver rr= null;
		try {
			rr = sessionFactory.getResourceResolver();
			session = rr.adaptTo(Session.class);
			
			Node rootNode  = session.getNode("/vtk"+ currYear);
			Node rptNode= null;
			if( !rootNode.hasNode("rpt") )
				 rptNode = rootNode.addNode("rpt", "nt:unstructured");
			else
				 rptNode= session.getNode("/vtk"+ currYear +"/rpt/");

			Node thisRpt = rptNode.addNode(rptId, "nt:unstructured");
            thisRpt.setProperty("rpt", sb.toString() );
            thisRpt.setProperty("id", rptId);
            session.save();
			
            
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
		return rptId;
	}
	
	public void emailRpt(String msg, String subject){
		try {

			logger.info("VTK Monthly Report Email Attempt Begin.");
			
			MessageGateway<MultiPartEmail> messageGateway = messageGatewayService.getGateway(MultiPartEmail.class);
			
			// create the mail
			MultiPartEmail email = new MultiPartEmail();
			for(String address:config.toEmailAddresses()){
				email.addTo(address);
			}			
			email.setFrom(config.fromEmailAddress());
			
			email.setSubject(subject +" (ENV:"+slingSettings.getRunModes()+")");
			email.setMsg("Please find attached GS Report attached as of "+ new java.util.Date());

			DataSource source = new ByteArrayDataSource(msg, "application/text");  

			// add the attachment
			email.attach(source, "rpt.csv", "rpt");

			messageGateway.send(email);

			logger.info("VTK Monthly Report Email Success!");
		}  catch (Throwable e) {
			logger.error("VTK Monthly Report Email Error");
			logger.error("VTK Monthly Report Email Error: ", e);
		} finally{
			logger.info("VTK Monthly Report Email Attempt End.");
		}
	}
	
}
