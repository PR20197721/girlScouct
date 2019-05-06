package org.girlscouts.vtk.osgi.component;

import org.girlscouts.vtk.auth.models.ApiConfig;

import java.io.OutputStream;

public interface AdminReportExcelGenerator {
    void generateReport(ApiConfig config, OutputStream outputStream);

}
