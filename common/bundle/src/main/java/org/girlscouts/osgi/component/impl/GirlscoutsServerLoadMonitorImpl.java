package org.girlscouts.osgi.component.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.osgi.component.GirlscoutsServerLoadMonitor;
import org.girlscouts.osgi.configuration.GirlscoutsServerLoadMonitorConfiguration;
import org.girlscouts.web.service.email.GSEmailService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { GirlscoutsServerLoadMonitor.class,
		Runnable.class }, immediate = true, name = "org.girlscouts.osgi.component.impl.GirlscoutsServerLoadMonitorImpl")
@Designate(ocd = GirlscoutsServerLoadMonitorConfiguration.class)
public class GirlscoutsServerLoadMonitorImpl implements Runnable,GirlscoutsServerLoadMonitor {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsServerLoadMonitor.class);

	private boolean author;

	private GirlscoutsServerLoadMonitorConfiguration config;

	@Reference
	protected SlingSettingsService settingsService;

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Reference
	protected GSEmailService gsEmailService;

	private Map<String, Object> serviceParams;

	private boolean isMaintenanceActive = false;
	private boolean isNotificationSent = false;
	private boolean isRecovered = true;
	private String host = "";
	List<String> emails = new ArrayList<String>();
	OperatingSystemMXBean operatingSystemMXBean;

	@Activate
	private void activate(GirlscoutsServerLoadMonitorConfiguration config) {
		this.config = config;
		operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		author = settingsService.getRunModes().contains("author");
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "gs-server-load-monitor");
		emails.addAll(Arrays.asList(config.emailAddresses()));
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			this.host = addr.getHostName();
		} catch (UnknownHostException e) {
			log.error("Girl Scouts Server Load Monitor encountered error: ", e);
		}
		log.info("Girl Scouts Server Load Monitor Activated.");
	}

	@Override
	public void run() {
		if (author) {
			try {
				double loadAvg = operatingSystemMXBean.getSystemLoadAverage();
				log.info("current load = {}, max load={}, alert load={}, recover load={}", loadAvg,
						config.maxLoad(), config.alertLoad(), config.recoverLoad());
				if (loadAvg <= config.recoverLoad()) {
					if (!isRecovered) {
						log.info("Server recovered, current load = {}", loadAvg);
						if (isMaintenanceActive) {
							log.info("Deactivating maintenance page");
							setMaintenance("", "");
						}
						if (isNotificationSent) {
							sendRecoveryEmail(loadAvg);
						}
						resetFlags();
					}
				} else {
					if (loadAvg >= config.alertLoad()) {
						if (loadAvg >= config.maxLoad()) {
							isRecovered = false;
							if (!isMaintenanceActive) {
								log.info("Activating maintenance page");
								setMaintenance(config.maintenancePage(), config.regex());
								sendMaintenanceEmail(loadAvg);
							}
						} else {
							if (!isNotificationSent) {
								sendAlertEmail(loadAvg);
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("Girl Scouts Server Load Monitor encountered error: ", e);
			}
		}
	}

	private void resetFlags() {
		isRecovered = true;
		isNotificationSent = false;
		isMaintenanceActive = false;
	}

	private void sendMaintenanceEmail(double loadAvg) {
		try {
			if (config.sendNotifications()) {
				log.info("Sending Maintenance Notification");
				String html = "Current system load average is : " + loadAvg
						+ ". Redirect (" + config.regex() + ") to maintenance page (" + config.maintenancePage()
						+ ") is activated.";
				String subject = "Alert: Server " + host + " experiencing high load";
				gsEmailService.sendEmail(subject, emails, html);
				isNotificationSent = true;
				log.info("Maintenance Notification email sent for {}", host);
			}
		} catch (Exception e) {
			log.error("Girl Scouts Server Load Monitor encountered error: ", e);
		}
	}

	private void sendAlertEmail(double loadAvg) {
		try {
			if (config.sendNotifications()) {
				log.info("Sending warning email");
				String html = "Current system load average is : " + loadAvg;
				String subject = "Warning: Server " + host + " experiencing increased load";
				gsEmailService.sendEmail(subject, emails, html);
				isNotificationSent = true;
				log.info("Warning email sent for {}", host);
			}
		} catch (Exception e) {
			log.error("Girl Scouts Server Load Monitor encountered error: ", e);
		}
	}

	private void sendRecoveryEmail(double loadAvg) {
		try {
			if (config.sendNotifications()) {
				log.info("Sending recovery email");
				String html = "Current system load average is : " + loadAvg
						+ ". Redirect to maintenance page is deactivated.";
				String subject = "Alert: Server " + host + " recovered from high load";
				gsEmailService.sendEmail(subject, emails, html);
				log.info("Recovery email sent for {}", host);
			}
		} catch (Exception e) {
			log.error("Girl Scouts Server Load Monitor encountered error: ", e);
		}
	}

	private void setMaintenance(String page, String regex) {
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			Resource mapping = rr.resolve(config.mappingPath());
			if (!ResourceUtil.isNonExistingResource(mapping)) {
				ModifiableValueMap map = mapping.adaptTo(ModifiableValueMap.class);
				map.put("sling:internalRedirect", page);
				map.put("sling:match", regex);
				rr.commit();
				isMaintenanceActive = !StringUtils.isBlank(regex);
				log.info("Set maintenance mapping to:  sling:internalRedirect = {}, sling:match={}", page, regex);
			}
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
