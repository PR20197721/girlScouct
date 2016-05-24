package org.girlscouts.vtk.cron;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.runmode.RunMode;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.SessionFactory;


@Component(
metatype = true, 
immediate = true,
label = "AEM Demo Cron Job for VTK DEMO", 
description = "rm demo temp nodes from db" 
)

@Service(value = {Runnable.class, AemCron.class})
@Properties({
@Property(name = "service.description", value = "Girl Scouts DEM Demo Service",propertyPrivate=true),
@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
@Property( name = "scheduler.expression", label="scheduler.expression", value = "0 4 * * * ?",description="cron expression"),
//@Property(name = "scheduler.period", longValue=60),
@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
@Property(name="scheduler.runOn", value="SINGLE",propertyPrivate=true)
})


public class AemCronImpl implements Runnable, AemCron  {
	
	@Reference
	TroopDAO troopDAO;

	@Reference
	private SlingSettingsService slingSettings;

	
	public void run() {
		
		
System.err.println("test runmodels: "+ slingSettings.getRunModes());
		if(slingSettings.getRunModes().contains("preview")){ 
		
			//demo site remove temo "SHARED_"
			troopDAO.removeDemoTroops();
		}
		
	}
}



