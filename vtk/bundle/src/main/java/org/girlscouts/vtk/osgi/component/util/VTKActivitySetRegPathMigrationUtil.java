package org.girlscouts.vtk.osgi.component.util;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {VTKActivitySetRegPathMigrationUtil.class}, immediate = true, name = "org.girlscouts.vtk.osgi.component.util.VTKActivitySetRegPathMigrationUtil")
@Designate(ocd = VTKActivitySetRegPathMigrationUtil.Config.class)
public class VTKActivitySetRegPathMigrationUtil {

    @ObjectClassDefinition(name = "Girl Scouts VTK VS 2.0 Activity Registration URL Migration Configuration")
    public @interface Config {
        @AttributeDefinition(name = "New Registration URL", type = AttributeType.STRING) String tempRegUrl() default "";
        @AttributeDefinition(name = "OLD Registration URL pattern", type = AttributeType.STRING) String oldRegUrlPattern() default "gsmembers.force.com";
    }

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String tempRegUrl = "";
    private String oldRegUrlPattern = "";

    @Activate
    void activate(Config config) {
        this.tempRegUrl = config.tempRegUrl();
        this.oldRegUrlPattern = config.oldRegUrlPattern();
        log.info("Girl Scouts VTK VS 2.0 Data Migration activated.");
    }

    public String tempRegUrl(){
        return this.tempRegUrl;
    }
    public String oldRegUrlPattern(){
        return this.oldRegUrlPattern;
    }
}