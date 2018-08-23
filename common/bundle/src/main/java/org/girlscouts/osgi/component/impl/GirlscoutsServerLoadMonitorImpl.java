package org.girlscouts.osgi.component.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.osgi.component.GirlscoutsServerLoadMonitor;
import org.girlscouts.osgi.configuration.GirlscoutsServerLoadMonitorConfiguration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { GirlscoutsServerLoadMonitor.class,
		Runnable.class }, immediate = true, name = "Girl Scouts Server Load Monitor")
@Designate(ocd = GirlscoutsServerLoadMonitorConfiguration.class)
public class GirlscoutsServerLoadMonitorImpl implements Runnable,GirlscoutsServerLoadMonitor {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsServerLoadMonitor.class);

	private final String MAINTENANCE_MAPPING_NODE = "/etc/map.author/maintenance";

	private final String MAINTENANCE_MAPPING_REGEX = ".*/content/.*";

	private boolean author;

	@Reference
	protected SlingSettingsService settingsService;

	@Reference
	protected ResourceResolverFactory resolverFactory;

	private Map<String, Object> serviceParams;

	@Activate
	private void activate(ComponentContext context) {
		author = settingsService.getRunModes().contains("author");
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "gs-server-load-monitor");
		log.info("Girl Scouts Server Load Monitor Activated.");
	}

	public void run() {
		log.info("Running Girl Scouts Server Load Monitor");
		if (author) {
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
