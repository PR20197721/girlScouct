package org.girlscouts.vtk.sling.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.codehaus.jackson.map.ObjectMapper;
import org.girlscouts.vtk.mapper.vtk.ModelToRestEntityMapper;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.osgi.component.util.YearPlanUtil;

import java.io.IOException;

@Component(metatype = true, immediate = true)
@Service
@Properties({@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"), @Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "vtkyearplan"), @Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"), @Property(propertyPrivate = true, name = "sling.servlet.methods", value = {"POST", "GET"}), @Property(name = "label", value = "Girl Scouts VTK Year Plan Service"), @Property(name = "description", value = "Girl Scouts VTK year plan service")})
public class VtkYearPlan extends SlingAllMethodsServlet {
    private final String jsonDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    @Reference
    YearPlanUtil yearPlanUtil;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String yearPlanPath = request.getParameter("ypp");
        if (yearPlanPath == null || "".trim().equals(yearPlanPath)) {
            //TODO error code here
            return;
        }
        YearPlan yearPlan = yearPlanUtil.getYearPlanJson(yearPlanPath);
        Gson gson = new GsonBuilder().setDateFormat(jsonDateFormat).enableComplexMapKeySerialization().create();
        String json = gson.toJson(ModelToRestEntityMapper.INSTANCE.toEntity(yearPlan));
        ObjectMapper mapper = new ObjectMapper();
        response.setCharacterEncoding("utf8");
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    }

}

	
	
