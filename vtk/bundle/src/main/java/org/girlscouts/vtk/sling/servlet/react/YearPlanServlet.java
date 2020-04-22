package org.girlscouts.vtk.sling.servlet.react;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.mapper.vtk.CollectionModelToEntityMapper;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.component.util.YearPlanUtil;
import org.girlscouts.vtk.sling.servlet.react.internal.VTKReactConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.Date;
import java.util.Map;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts VTK Year Plan Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.extensions=html",
        "sling.servlet.resourceTypes=girlscouts/vtk/react/yearplan"})
public class YearPlanServlet extends SlingAllMethodsServlet implements OptingServlet, VTKReactConstants {
    private static final Logger log = LoggerFactory.getLogger(YearPlanServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = -1341523521748738122L;

    @Reference
    private VtkUtil vtkUtil;

    @Reference
    private YearPlanUtil yearPlanUtil;

    private Gson gson;

    @Activate
    private void activate() {
        log.debug("Girl Scouts VTK Year Plan Servlet activated");
        gson = new GsonBuilder()
                .setDateFormat(jsonDateFormat)
                .enableComplexMapKeySerialization()
                .create();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "must-revalidate");
            response.addHeader("pragma", "no-cache");
            response.addHeader("expires", "0");
            JsonObject json = new JsonObject();
            User user = ((User) request.getSession().getAttribute(User.class.getName()));
            if(user != null) {
                boolean isViewingArchived = !user.getCurrentYear().equals(String.valueOf(vtkUtil.getCurrentGSYear()));
                Troop troop = (Troop) request.getSession().getAttribute("VTK_troop");
                if (isViewingArchived) {
                    troop = (Troop) request.getSession().getAttribute("VTK_archived_troop");
                }
                if (troop != null) {
                    if (troop.getYearPlan() == null) {
                        json.addProperty("yearPlan", "NYP");
                    } else {
                        Map<Date, YearPlanComponent> sched = troop.getSchedule();
                        try {
                            if (troop.getYearPlan() != null) {
                                troop.getYearPlan().setMilestones(yearPlanUtil.getCouncilMilestones(user, troop));
                            }
                        } catch (Exception e) {
                            log.error("Exception occured:", e);
                        }
                        if (troop.getYearPlan().getMilestones() == null) {
                            troop.getYearPlan().setMilestones(new java.util.ArrayList());
                        }else {
                            for (int i = 0; i < troop.getYearPlan().getMilestones().size(); i++) {
                                if (troop.getYearPlan().getMilestones().get(i).getDate() != null && troop.getYearPlan().getMilestones().get(i).getShow()) {
                                    sched.put(troop.getYearPlan().getMilestones().get(i).getDate(), troop.getYearPlan().getMilestones().get(i));
                                }
                            }
                        }

                        String yearPlanJson = gson.toJson(CollectionModelToEntityMapper.mapYearPlanComponents(sched));
                        if (yearPlanJson != null) {
                            yearPlanJson.replaceAll("mailto:", "");
                        }
                        JsonObject jsonObject = new JsonParser().parse(yearPlanJson).getAsJsonObject();
                        json.addProperty("yearPlan", troop.getYearPlan().getName());
                        json.add("schedule", jsonObject);
                    }
                } else {
                    json.addProperty("error", "no troop selected");
                }
            }else {
                json.addProperty("error", "no user in session");
            }
            response.getWriter().write(new Gson().toJson(json));
        } catch (Exception e) {
            log.error("Error occured: ", e);
        }
    }

    /**
     * OptingServlet Acceptance Method
     **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }

}