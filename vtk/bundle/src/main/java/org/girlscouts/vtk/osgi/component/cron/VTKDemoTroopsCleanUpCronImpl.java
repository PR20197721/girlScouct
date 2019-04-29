package org.girlscouts.vtk.osgi.component.cron;

import org.apache.felix.scr.annotations.*;
import org.girlscouts.vtk.dao.TroopDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        metatype = true,
        immediate = true,
        label = "AEM Demo Cron Job for VTK DEMO",
        description = "rm demo temp nodes from db"
)
@Service(value = {Runnable.class})
@Properties({
        @Property(name = "service.description", value = "Girl Scouts VTK demo troops clean up job", propertyPrivate = true),
        @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = true),
        @Property(name = "scheduler.expression", label = "scheduler.expression", value = "4 40 4 1 1 ?", description = "cron expression"),
        @Property(name = "scheduler.concurrent", boolValue = false, propertyPrivate = true),
        @Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)
})

public class VTKDemoTroopsCleanUpCronImpl implements Runnable {

    private static Logger log = LoggerFactory.getLogger(VTKDemoTroopsCleanUpCronImpl.class);

    @Reference
    TroopDAO troopDAO;

    @Override
    public void run() {
        log.debug("Executing VTK Demo troops clean up cron job");
        troopDAO.removeDemoTroops();
    }
}



