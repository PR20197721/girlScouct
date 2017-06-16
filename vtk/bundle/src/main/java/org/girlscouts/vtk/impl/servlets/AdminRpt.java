package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import java.rmi.ServerException;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.ejb.CouncilRpt;
import org.girlscouts.vtk.ejb.YearPlanUtil;

@Component(metatype = true, immediate = true)
@Properties({
	@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "csv"),
	@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "adminreport"),
	@Property(name="label", value="Girl Scouts VTK Admin Report"),
	@Property(name="description", value="Girl Scouts VTK Admin Report Download"),
	@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET")
})
@Service
public class AdminRpt extends SlingSafeMethodsServlet {

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
			String outputResult = "\"AGE GROUP\", \"Year Plan\", \"# of Troops Adopted\", \"# of Plans Customized\", \"Plans with Added Activities\"";
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

				java.util.List<String> ageGroups = new java.util.ArrayList<String>();
				ageGroups.add("brownie");
				ageGroups.add("daisy");
				ageGroups.add("junior");
				ageGroups.add("cadette");
				ageGroups.add("senior");
				ageGroups.add("ambassador");
				if (request.getParameter("cid") != null) {
					cid = (String) request.getParameter("cid");
				}
				java.util.List<CouncilRptBean> container = councilRpt
						.getRpt(cid);
				int count = 0;
				for (String ageGroup : ageGroups) {
					java.util.List<CouncilRptBean> brownies = councilRpt
							.getCollection_byAgeGroup(container, ageGroup);
					Map<String, String> yearPlanNames = councilRpt
							.getDistinctPlanNamesPath(brownies);
					count++;
					int y = 0;
					java.util.Iterator itr = yearPlanNames.keySet().iterator();
					while (itr.hasNext()) {
						String yearPlanPath = (String) itr.next();
						String yearPlanName = yearPlanNames.get(yearPlanPath);
						java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt
								.getCollection_byYearPlanPath(brownies,
										yearPlanPath);
						int countAltered = councilRpt
								.countAltered(yearPlanNameBeans);
						int countActivity = councilRpt
								.countActivity(yearPlanNameBeans);
						y++;
						outputResult += "\n\"" + ageGroup + "\", \""
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
