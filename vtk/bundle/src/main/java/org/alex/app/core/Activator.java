package org.alex.app.core;

import java.util.ArrayList;
import java.util.List;
 
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
 /*
import com.cognifide.slice.api.injector.InjectorRunner;
import com.cognifide.slice.commons.SliceModulesFactory;
import com.cognifide.slice.cq.module.CQModulesFactory;
 
import com.google.inject.Module;
 */
public class Activator implements BundleActivator {
 
    public static final String INJECTOR_NAME = "app";
 
    private static final String BUNDLE_NAME_FILTER = "app-.*";
 
    private static final String BASE_PACKAGE = "org.alex.app"; //"com.cognifide.app";
 
    //@Override
    public void start(final BundleContext bundleContext) {
 /*
        final InjectorRunner injectorRunner = new InjectorRunner(bundleContext, INJECTOR_NAME,
                BUNDLE_NAME_FILTER, BASE_PACKAGE);
 
        final List<Module> sliceModules = SliceModulesFactory.createModules(bundleContext);
        // CQModulesFactory is a class coming from Slice Addons https://cognifide.atlassian.net/wiki/display/SLICE/Slice+CQ+Addons+-+4.0
        final List<Module> cqModules = CQModulesFactory.createModules();
        final List<Module> customModules = createCustomModules();
 
        injectorRunner.installModules(sliceModules);
        injectorRunner.installModules(cqModules);
        injectorRunner.installModules(customModules);
 
        injectorRunner.start();
  
    }
    private List<Module> createCustomModules() {
        List<Module> applicationModules = new ArrayList<Module>();
        //populate the list with your modules
        return applicationModules;
        */
    }
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
