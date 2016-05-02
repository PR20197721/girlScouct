package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import java.rmi.ServerException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.ejb.YearPlanUtil;

@Component(metatype = true, immediate = true)
@Service
@Properties({
	@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "ics"),
	@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET"),
        @Property(name="label", value="Girl Scouts VTK Upload Servlet"),
        @Property(name="description", value="Girl Scouts VTK Upload Servlet")
})
public class Cal extends SlingSafeMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	YearPlanUtil yearPlanUtil;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
		User user = ((org.girlscouts.vtk.models.User) request.getSession()
				.getAttribute(org.girlscouts.vtk.models.User.class.getName()));
		Troop troop = (Troop) request.getSession().getValue("VTK_troop");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ troop.getYearPlan().getName() + ".ics\"");
		response.setContentType("text/calendar");

		try {
/*
In Koo comment out for AEM 6.1 upgrade
			net.fortuna.ical4j.model.Calendar calendar = yearPlanUtil
					.yearPlanCal(user, troop);
			ServletOutputStream fout = response.getOutputStream();
			net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
			outputter.output(calendar, fout);
			fout.flush();
*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
