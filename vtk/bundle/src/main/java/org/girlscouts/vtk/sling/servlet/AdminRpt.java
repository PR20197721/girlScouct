package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.component.AdminReportExcelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.ServerException;

@Component(metatype = true, immediate = true)
@Properties({
        @Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
        @Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "xls"),
        @Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "admin_reports_downloadable"),
        @Property(name = "label", value = "Girl Scouts VTK Admin Report"),
        @Property(name = "description", value = "Girl Scouts VTK Admin Report Download"),
        @Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET")
})
@Service
public class AdminRpt extends SlingSafeMethodsServlet {

    private final Logger log = LoggerFactory.getLogger("vtk");

    @Reference
    AdminReportExcelGenerator reportGenerator;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws
            IOException {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"admin-report.xls\"");
        try {
            ApiConfig config = null;
            OutputStream outputStream = response.getOutputStream();
            try {
                config = (ApiConfig) request.getSession().getAttribute(ApiConfig.class.getName());
            } catch (Exception e) {
                log.error("Unable to get Api Config from session : ", e);
            }
            if (config != null) {
                reportGenerator.generateReport(config, outputStream);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("Error occurred:", e);
        }

    }
}
