package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.models.CouncilRptBean;


import org.boon.core.Function;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import  org.boon.primitive.Int;

import org.boon.Lists;
import org.boon.Str;
//import org.boon.criteria.ObjectFilter;
//import org.boon.template.BoonTemplate;
//import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//import static org.boon.Boon.fromJson;
import static org.boon.Boon.puts;
//import static org.boon.Boon.toJson;
import static org.boon.Lists.copy;
//import static org.boon.Lists.lazyAdd;
import static org.boon.Lists.list;
import static org.boon.Maps.map;
import static org.boon.core.reflection.BeanUtils.atIndex;
//import static org.boon.criteria.ObjectFilter.*;
//import static org.boon.criteria.Selector.selectAs;
//import static org.boon.criteria.Selector.selectAsTemplate;
import static org.boon.criteria.Selector.selects;
//import static org.boon.template.BoonTemplate.jstl;
//import static org.boon.template.BoonTemplate.template;

@Component
@Service(value = CouncilRpt.class)
public class CouncilRpt {

	@Reference
	private SessionFactory sessionFactory;

	@Activate
	void activate() {
	}

	public java.util.List<String> getActivityRpt(String sfCouncil) {

		javax.jcr.Session s = null;
		java.util.List<String> activities = new java.util.ArrayList<String>();
		String sql1 = "select jcr:path "
				+ " from nt:base "
				+ " where jcr:path like '/vtk/"
				+ sfCouncil
				+ "/troops/%' and ocm_classname='org.girlscouts.vtk.models.Activity'";

		try {
			s = sessionFactory.getSession();

			javax.jcr.query.QueryManager qm = s.getWorkspace()
					.getQueryManager();

			javax.jcr.query.Query q = qm.createQuery(sql1,
					javax.jcr.query.Query.SQL);

			System.err.println("tatay: activ start " + new java.util.Date());

			javax.jcr.query.QueryResult result = q.execute();

			System.err.println("tatay: activ end " + new java.util.Date());

			System.err.println("tatay: start travers activ: "
					+ new java.util.Date());

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

	public java.util.List <CouncilRptBean> getRpt(String sfCouncil){

		java.util.List <CouncilRptBean> container = new java.util.ArrayList<CouncilRptBean>();
		javax.jcr.Session s = null;
		java.util.List <org.girlscouts.vtk.models.YearPlanRpt> yprs = new java.util.ArrayList<org.girlscouts.vtk.models.YearPlanRpt>();
		String sql="select  name, altered, refId,jcr:path,excerpt(.) "+		
		" from nt:base "+
		" where jcr:path like '/vtk/"+sfCouncil+"/troops/%' and ocm_classname='org.girlscouts.vtk.models.YearPlan'";
		
		try{
			s = sessionFactory.getSession();

			javax.jcr.query.QueryManager qm = s.getWorkspace()
					.getQueryManager();

			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);

			System.err.println("tatay: activ start " + new java.util.Date());
			javax.jcr.query.QueryResult result = q.execute();
			System.err.println("tatay: activ end " + new java.util.Date());
			System.err.println("tatay: start travers activ: "
					+ new java.util.Date());
		
			for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			
				String yearPlanName= "", libPath="", ageGroup="";
				boolean isAltered = false;				
				javax.jcr.query.Row r = it.nextRow();
				
				String path = r.getValue("jcr:path").getString() ;
				
				try{ isAltered= r.getValue("altered").getBoolean(); }catch(Exception e){}
				
				try{ yearPlanName= r.getValue("name").getString();}catch(Exception e){}
				
				try{ libPath= r.getValue("refId").getString();}catch(Exception e){}
				
				if( libPath.contains("brownie")) 
				
								ageGroup= "brownie";
				
				else if( libPath.contains("daisy")) 
				
				                ageGroup= "daisy";
				
				else if( libPath.contains("junior")) 
				
				                ageGroup= "junior";
				
				CouncilRptBean crb = new CouncilRptBean();
				crb.setYearPlanName(yearPlanName);
				crb.setAltered(isAltered);
				crb.setLibPath(libPath);
				crb.setAgeGroup(ageGroup);
				container.add( crb);
				
			
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
	
	public void fmtRpt(java.util.List<CouncilRptBean> results){
		
		//Repo<Integer,CouncilRptBean> rptRepo;
		java.util.List yearPlanNames;
		
		yearPlanNames = (List<CouncilRptBean>) atIndex(results, "yearPlanName");
		System.err.println(yearPlanNames.size());
	}
}
