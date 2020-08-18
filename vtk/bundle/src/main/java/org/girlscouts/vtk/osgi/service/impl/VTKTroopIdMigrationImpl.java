package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.osgi.conf.VTKTroopIdMigrationConfig;
import org.girlscouts.vtk.osgi.service.VTKTroopIdMigration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {VTKTroopIdMigration.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.VTKTroopIdMigrationImpl")
@Designate(ocd = VTKTroopIdMigrationConfig.class)
public class VTKTroopIdMigrationImpl extends BasicGirlScoutsService implements VTKTroopIdMigration {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private String path;
    private boolean isActive;
    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.path = getConfig("path");
        this.isActive = getBooleanConfig("active");
        log.info(this.getClass().getName() + " activated.");
    }
    @Override
    public void migrateInPath(String path) {
        if(isActive){
            log.debug("Started Migrating VTK Troop Id's in "+path);
            try{

            }catch(Exception e){
                log.error("Exception occured:",e);
            }
            log.debug("Completed Migrating VTK Troop Id's in "+path);
        }
    }
}
