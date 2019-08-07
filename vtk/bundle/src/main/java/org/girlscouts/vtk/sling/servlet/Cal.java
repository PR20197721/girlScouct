package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.component.util.YearPlanUtil;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Component(metatype = true, immediate = true)
@Service
@Properties({@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"), @Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "ics"), @Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET"), @Property(name = "label", value = "Girl Scouts VTK Upload Servlet"), @Property(name = "description", value = "Girl Scouts VTK Upload Servlet")})
public class Cal extends SlingSafeMethodsServlet {
    @Reference
    YearPlanUtil yearPlanUtil;
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        User user = ((org.girlscouts.vtk.models.User) request.getSession().getAttribute(org.girlscouts.vtk.models.User.class.getName()));
        Troop troop = (Troop) request.getSession().getAttribute("VTK_troop");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + troop.getYearPlan().getName() + ".ics\"");
        response.setContentType("text/calendar");
        try {
            net.fortuna.ical4j.model.Calendar calendar = yearPlanUtil.yearPlanCal(user, troop);
            ServletOutputStream fout = response.getOutputStream();
            net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
            outputter.output(calendar, fout);
            fout.flush();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
