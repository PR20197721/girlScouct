package org.girlscouts.vtk.ejb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.utils.VtkUtil;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value = CouncilRpt.class)
public class CouncilRpt {

	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	ConfigManager configManager;
	
	@Activate
	void activate() {
	}

	public java.util.List<String> getActivityRpt(String sfCouncil) {

		javax.jcr.Session s = null;
		java.util.List<String> activities = new java.util.ArrayList<String>();
		String sql1 = "select jcr:path "
				+ " from nt:base "
				+ " where jcr:path like '/vtk"
				+ VtkUtil.getCurrentGSYear()
				+ "/"
				+ sfCouncil
				+ "/troops/%' and ocm_classname='org.girlscouts.vtk.models.Activity'";
		try {
			s = sessionFactory.getSession();
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
				if (s != null)
					sessionFactory.closeSession(s);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return activities;
	}

	public java.util.List<CouncilRptBean> getRpt(String sfCouncil) {

		java.util.List<CouncilRptBean> container = new java.util.ArrayList<CouncilRptBean>();
		javax.jcr.Session s = null;
		java.util.List<org.girlscouts.vtk.models.YearPlanRpt> yprs = new java.util.ArrayList<org.girlscouts.vtk.models.YearPlanRpt>();
		String sql = "select  name, altered, refId,jcr:path,excerpt(.) "
				+ " from nt:base "
				+ " where jcr:path like '"
				+ VtkUtil.getYearPlanBase(null, null)
				+ sfCouncil
				+ "/troops/%' and ocm_classname='org.girlscouts.vtk.models.YearPlan'";

		java.util.List<String> activities = getActivityRpt(sfCouncil);
		javax.jcr.query.QueryResult result = null;
		try {
			s = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = s.getWorkspace()
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
				if (libPath == null
						|| libPath.equals("")
						|| (yearPlanName != null && yearPlanName.trim()
								.toLowerCase().equals("custom year plan"))) {
					try {
						Node troop = r.getNode().getParent();
						libPath = troop.getProperty("sfTroopAge").getString()
								.toLowerCase().substring(2);
						troopName = troop.getProperty("sfTroopName")
								.getString();
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
				if (s != null)
					sessionFactory.closeSession(s);
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
		String sql = "select parent.sfTroopId, parent.sfTroopName from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) "
				+ " where (isdescendantnode (parent, ["
				+ VtkUtil.getYearPlanBase(null, null)
				+ councilId
				+ "/troops/]))  and "
				+ " parent.ocm_classname='org.girlscouts.vtk.models.Troop' and child.refId like '"
				+ yearPlanPath + "%'";
		javax.jcr.query.QueryResult result = null;
		try {
			s = sessionFactory.getSession();
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
				if (s != null)
					sessionFactory.closeSession(s);

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

		if (true)
			return container;

		// get latest year plan names from lib
		javax.jcr.Session s = null;
		try {
			s = sessionFactory.getSession();
			java.util.Iterator itr = container.keySet().iterator();
			while (itr.hasNext()) {
				try {
					String path = (String) itr.next();
					if (path == null || path.trim().equals("")
							|| !path.contains("/"))
						continue;
					Node libYear = s.getNode(path);
					if (libYear != null) {
						String yearPlanName = libYear.getProperty("name")
								.getString();
						if (yearPlanName != null
								&& !yearPlanName.trim().equals("")) {
							container.put(path, yearPlanName);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null)
					sessionFactory.closeSession(s);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
		try {
			session = sessionFactory.getSession();
			
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
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return rptId;
	}
	
	public void emailRpt(String msg){
		try {
	
			MessageGateway<MimeMessage> messageGateway = messageGatewayService.getGateway(MimeMessage.class);
			java.util.List<InternetAddress> toAddresses = new java.util.ArrayList();
			toAddresses.add( new InternetAddress("Dimitry.Nemirovsky@ey.com" ) );
			toAddresses.add( new InternetAddress("alex.yakobovich@ey.com") );
			
			/*
			HtmlEmail email = new HtmlEmail();
			
			email.setTo(toAddresses);		
    		email.setSubject("GS Monthly Report");		
			email.setTextMsg("Report as of  "+  new java.util.Date() + ":" + msg);
			messageGateway.send(email);
			*/

			/*
			MultiPartEmail email = new MultiPartEmail();
			email.setTo(toAddresses);		
    		email.setSubject("GS Monthly Report");		
			email.setMsg("Report as of  "+  new java.util.Date() + ":" + msg);
			*/
			
			 /*
			 // add the attachment
			 EmailAttachment attachment = new EmailAttachment();
			 attachment.setDisposition(EmailAttachment.ATTACHMENT);
			 attachment.setDescription("GS report");
			 attachment.setName("report.xls");
			 email.attach(attachment);
			 */
			
			
			/*
			MimeBodyPart mbp = new MimeBodyPart();
			String data = "any ASCII data";
			DataSource ds = new ByteArrayDataSource(data, "application/x-any");
			email.setDataHandler( new DataHandler(ds) );
		    */
			
			 // Get system properties
	        Properties properties = System.getProperties();
	 
	        // Setup mail server (Depending upon your
	        // Mail server - you may need more props here
	        properties.setProperty("mail.smtp.host", "localhost");
	        //properties.setProperty("mail.smtp.user", "<valid SMTP user>");
	 
	        // Get the default Session object.
	        javax.mail.Session mailSession = null;//session.getDefaultInstance(properties);
			
			MimeMessage message = new MimeMessage( mailSession );
			Multipart mp = new MimeMultipart();
			 
			    MimeBodyPart attachment = new MimeBodyPart();
			    attachment.setFileName("report.csv");
			    attachment.setContent(msg, "text/comma-separated-values");
			    mp.addBodyPart(attachment);

			    message.setContent( mp );
			
			messageGateway.send(message);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
