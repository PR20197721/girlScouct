package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import java.rmi.ServerException;
import java.util.*;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.ejb.CouncilRpt;
import org.girlscouts.vtk.ejb.YearPlanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, immediate = true)
@Properties({
	@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "csv"),
	@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "admin_reports_downloadable"),
	@Property(name="label", value="Girl Scouts VTK Admin Report"),
	@Property(name="description", value="Girl Scouts VTK Admin Report Download"),
	@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET")
})
@Service
public class AdminRpt extends SlingSafeMethodsServlet {

    private final Logger log = LoggerFactory.getLogger("vtk");

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	CouncilRpt councilRpt;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"adminreport.csv\"");
		try {
			OutputStream outputStream = response.getOutputStream();
			String outputResult = "\"User name\",\"User email\",\"AGE GROUP\", \"Year Plan\", \"# of Troops Adopted\", \"# of Plans Customized\", \"Plans with Added Activities\"";
			HttpSession session = request.getSession();
			User user = ((org.girlscouts.vtk.models.User) session
					.getAttribute(org.girlscouts.vtk.models.User.class
							.getName()));
			String cid = user.getApiConfig().getUser().getAdminCouncilId() + "";
			if (!(user.getApiConfig().getUser().isAdmin() && user
					.getApiConfig().getUser().getAdminCouncilId() > 0)) {

				try {
					outputStream.close();
				} catch (Exception eee) {
					eee.printStackTrace();
				}
				return;
			} else {

				List<String> ageGroups = new ArrayList<String>();
				ageGroups.add("brownie");
				ageGroups.add("daisy");
				ageGroups.add("junior");
				ageGroups.add("cadette");
				ageGroups.add("senior");
				ageGroups.add("ambassador");
				ageGroups.add("multi-level");
				if (request.getParameter("cid") != null) {
					cid = (String) request.getParameter("cid");
				}
                ApiConfig config = null;
                try {
                    config = (ApiConfig) request.getSession().getAttribute(ApiConfig.class.getName());
                } catch (Exception e) {
                    log.error("Unable to get Api Config from session : ", e);
                }
				List<CouncilRptBean> container = councilRpt.getRpt(cid, config);
				try {
                    container.sort((CouncilRptBean o1, CouncilRptBean o2) -> String.valueOf(o1.getAgeGroup()).toLowerCase().compareTo(String.valueOf(o2.getAgeGroup()).toLowerCase()));
                }catch(Exception e){
                    log.error("Failed to sort troops by age for admin report:",e);
                }
                for(CouncilRptBean bean:container) {
                    try {
                        List<Contact> troopLeaders = bean.getTroopLeaders();
                        StringBuilder troopLeadersInfo = new StringBuilder();
                        for(Contact troopLeader:troopLeaders){
                            String userName = troopLeader.getFirstName()+" "+troopLeader.getLastName();
                            String userEmail = troopLeader.getEmail();
                            troopLeadersInfo.append(userName+"("+userEmail+"):");
                        }

                        String ageGroup = bean.getAgeGroup();
                        String yearPlanName = bean.getYearPlanName();
                        outputResult += "\n\"" + troopLeadersInfo + "\",\"" + bean.getAgeGroup() + "\", \""
                                + bean.getYearPlanName().replaceAll(",", "\\,") + "\",\""
                                + bean.getYearPlanPath() + "\",\""
                                + bean.isAltered() + "\",\"" + bean.isActivity() + "\",";
                    }catch(Exception e){
                        log.error("Failed to add troop "+" to admin report:",e);
                    }
                }
				int count = 0;
				for (String ageGroup : ageGroups) {
					List<CouncilRptBean> brownies = councilRpt
							.getCollection_byAgeGroup(container, ageGroup);
					Map<String, String> yearPlanNames = councilRpt
							.getDistinctPlanNamesPath(brownies);
					count++;
					int y = 0;
					Iterator itr = yearPlanNames.keySet().iterator();
					while (itr.hasNext()) {
						String yearPlanPath = (String) itr.next();
						String yearPlanName = yearPlanNames.get(yearPlanPath);
						String userName = "";
						String userEmail = "";

						List<CouncilRptBean> yearPlanNameBeans = councilRpt
								.getCollection_byYearPlanPath(brownies,
										yearPlanPath);
						int countAltered = councilRpt
								.countAltered(yearPlanNameBeans);
						int countActivity = councilRpt
								.countActivity(yearPlanNameBeans);
						y++;
						outputResult += "\n\""+userName+"\",\""+userEmail+"\",\"" + ageGroup + "\", \""
								+ yearPlanName.replaceAll(",", "") + "\",\""
								+ yearPlanNameBeans.size() + "\",\""
								+ countAltered + "\",\"" + countActivity
								+ "\",";
					}// edn for
				}
			}
			outputStream.write(outputResult.getBytes());
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
