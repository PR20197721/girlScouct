package org.girlscouts.vtk.osgi.component.cron;

import org.apache.felix.scr.annotations.*;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.utils.VtkUtil;

@Component(
        metatype = true,
        immediate = true,
        label = "GS Monthly Report ",
        description = "Gs monthly rpt"
)

@Service(value = {Runnable.class})
@Properties({
        @Property(name = "service.description", value = "GS Monthly Report", propertyPrivate = true),
        @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = true),
        @Property(name = "scheduler.expression", label = "scheduler.expression", value = "4 50 4 1 1  ?", description = "cron expression"),
        @Property(name = "scheduler.concurrent", boolValue = false, propertyPrivate = true),
        @Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)
})

public class GSMonthlyRptImpl implements Runnable {

    @Reference
    CouncilDAO councilDAO;

    public void run() {
        if (true) {//slingSettings.getRunModes().contains("prod") ){ GSVTK-1341
            System.err.println("Generating report 'monthly' part 1 of 2 ....");
            try {
                councilDAO.GSMonthlyRpt();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.err.println("Generating report 'detailed' part 2 of 2 ....");
            try {
                councilDAO.GSMonthlyDetailedRpt(VtkUtil.getYearPlanBase(null, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.err.println("Done generating reports");
        }

    }
}



