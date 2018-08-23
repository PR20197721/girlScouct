package org.girlscouts.osgi.component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = "Girl Scouts Server Load Monitor", description = "Component will monitor server load and activate a maintenance page once load reaches max", immediate = true, metatype = true)
@Service(value = { Runnable.class })
@Property(name = "scheduler.expression", value = "0 0 0 ? * * 2099/1")
public class GirlscoutsServerLoadMonitor implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsServerLoadMonitor.class);

	@Property(value = "Girl Scouts Server Load Monitor")
	static final String LABEL = "process.label";
	@Property(value = "Component will monitor server load and activate a maintenance page once load reaches max.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;

	@Reference
	protected SlingSettingsService settingsService;

	@Reference
	protected ResourceResolverFactory resolverFactory;

	private Map<String, Object> serviceParams;

	@Activate
	private void activate(ComponentContext context) {
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "gs-server-load-monitor");
		log.info("Girl Scouts Server Load Monitor Activated.");
	}

	public void run() {
		log.info("Running Girl Scouts Server Load Monitor");
		if (isPublisher()) {
			log.error("Publisher instance - will not execute Page Replicator");
			return;
		}
		ResourceResolver rr = null;
		try {
			OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
			double loadAvg = operatingSystemMXBean.getSystemLoadAverage();
			log.error("cpu load = {}", loadAvg);
		} catch (Exception e) {
			log.error("Girl Scouts Server Load Monitor encountered error: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e) {
				log.error("Girl Scouts Server Load Monitor encountered error while closing resource resolver: ", e);
			}
		}
	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts Server Load Monitor Deactivated.");
	}

	protected boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

}
