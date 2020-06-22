package org.girlscouts.vtk.scaffolding;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@SlingServlet(paths = {"/bin/vtk/v1/scaffoldingdata"})
public class VtkScaffoldingDataServlet extends SlingAllMethodsServlet {
    @Reference
    private org.apache.sling.api.resource.ResourceResolverFactory resolverFactory;
    private static final long serialVersionUID = -1570812480911363489L;
    private static Logger log = LoggerFactory.getLogger(VtkScaffoldingDataServlet.class);

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            StringBuilder builder = new StringBuilder();
            final String YEAR_PLAN_BASE = "/content/girlscouts-vtk/yearPlanTemplates/library";
            final String DATA_ROOT = "{\"yearplan\":[DATA_TEMPLATE]}";
            final String DATA_TEMPLATE = "{\"title\":\"mytitle\", \"data\":\"mydata\"}";
            System.err.println(" yearPlanPath is " + YEAR_PLAN_BASE);
            String finalJson = DATA_ROOT;
            ResourceResolver resourceResolver = null;
            try {
                Map<String, Object> serviceParams = new HashMap<String, Object>();
                serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
                resourceResolver = resolverFactory.getServiceResourceResolver(serviceParams);
                Resource baseResources = resourceResolver.getResource(YEAR_PLAN_BASE);
                if (baseResources == null) {
                    return;
                }
                String yearPlanData = "";
                Iterator<Resource> baseResource = baseResources.listChildren();
                while (baseResource.hasNext()) {
                    //e.g. daisy
                    Resource trackResource = baseResource.next();
                    Iterator<Resource> ypResources = trackResource.listChildren();
                    while (ypResources.hasNext()) {
                        //e.g. yearplan1
                        Resource ypResource = ypResources.next();
                        org.apache.sling.api.resource.ModifiableValueMap xproperties = ypResource.adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class);
                        String name = (String) xproperties.get("name");
                        String ypPath = ypResource.getPath();
                        yearPlanData = yearPlanData + DATA_TEMPLATE;
                        yearPlanData = yearPlanData.replaceAll("mytitle", name).replaceAll("mydata", ypPath + "###" + name);
                        yearPlanData = yearPlanData + ",";

                    }//while

                }// while trackResources, example Daisy
                // finalJson = finalJson.replaceAll("YEAR-PLAN-PLACEHOLDER",
                // year);
                yearPlanData = yearPlanData.substring(0, (yearPlanData.length() - 1));
                finalJson = finalJson.replaceAll("DATA_TEMPLATE", yearPlanData);
                log.error(" finalJson data " + finalJson);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    resourceResolver.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            builder.append(finalJson);
            response.getWriter().write(builder.toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}