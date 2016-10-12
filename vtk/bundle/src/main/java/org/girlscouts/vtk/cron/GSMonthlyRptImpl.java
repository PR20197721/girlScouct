package org.girlscouts.vtk.cron;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.runmode.RunMode;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.SessionFactory;

@Component(
metatype = true, 
immediate = true,
label = "GS Monthly Report ", 
description = "Gs monthly rpt" 
)

@Service(value = {Runnable.class, AemCron.class})
@Properties({
@Property(name = "service.description", value = "GS Monthly Report",propertyPrivate=true),
@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
@Property( name = "scheduler.expression", label="scheduler.expression", value = "4 54 1 1 *  ?",description="cron expression"),
@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
@Property(name="scheduler.runOn", value="SINGLE",propertyPrivate=true)
})

public class GSMonthlyRptImpl implements Runnable, AemCron  {
	
	@Reference
	CouncilDAO councilDAO;

	@Reference
	private SlingSettingsService slingSettings;

	
	public void run() {
		
		
		if( slingSettings.getRunModes().contains("prod") ){ 
		
			councilDAO.GSMonthlyRpt();
		}
		
	}
}



