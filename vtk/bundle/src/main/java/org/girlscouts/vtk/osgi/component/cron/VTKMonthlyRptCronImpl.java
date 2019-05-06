package org.girlscouts.vtk.osgi.component.cron;

import org.apache.felix.scr.annotations.*;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, immediate = true, label = "GS Monthly Report ", description = "Gs monthly rpt")
@Service(value = {Runnable.class})
@Properties({@Property(name = "service.description", value = "Girl Scouts VTK monthly report job", propertyPrivate = true), @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = true), @Property(name = "scheduler.expression", label = "scheduler.expression", value = "4 50 4 1 1  ?", description = "cron expression"), @Property(name = "scheduler.concurrent", boolValue = false, propertyPrivate = true), @Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)})
public class VTKMonthlyRptCronImpl implements Runnable {
    private static Logger log = LoggerFactory.getLogger(VTKMonthlyRptCronImpl.class);
    @Reference
    CouncilDAO councilDAO;

    public void run() {
        log.debug("Executing VTK monthly report cron job");
        log.debug("Generating report 'monthly' part 1 of 2 ....");
        try {
            councilDAO.GSMonthlyRpt();
        } catch (Exception e) {
            log.error("Error occurred:", e);
        }
        log.debug("Generating report 'detailed' part 2 of 2 ....");
        try {
            councilDAO.GSMonthlyDetailedRpt(VtkUtil.getYearPlanBase(null, null));
        } catch (Exception e) {
            log.error("Error occurred:", e);
        }
        log.debug("Done generating reports");
    }
}



