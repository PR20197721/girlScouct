package org.girlscouts.web.councilrollout.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.girlscouts.web.service.email.GSEmailService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveStatus;

@Component
@Service(value = GirlScoutsNotificationAction.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.girlscoutsnotificationaction", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts Content Rollout Notification Service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class GirlScoutsNotificationActionImpl
		implements GirlScoutsNotificationAction, PageActivationConstants, PageActivationConstants.Email {
	
	@Reference
	public GSEmailService gsEmailService;

	@Reference
	private SlingSettingsService settingsService;

	@Reference
	private ResourceResolverFactory resolverFactory;

	private final Logger log = LoggerFactory.getLogger(GirlScoutsNotificationActionImpl.class);

	private ResourceResolver rr;
	// configuration fields

	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyCouncils(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// 30 second remorse wait time in case roll-out needs to
			// be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		processCouncilNotifications(path, rr.adaptTo(Session.class));
	}

	@Override
	public void notifyGSUSA(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// 30 second remorse wait time in case roll-out needs to
			// be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		processGSUSANotifications(path, rr.adaptTo(Session.class));
	}

	private void processCouncilNotifications(String path, Session session) {
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			Set<String> pages = null;
			String subject = DEFAULT_NOTIFICATION_SUBJECT;
			String message = "", templatePath = "", srcPath = "";
			Boolean notify = false, useTemplate = false;
			try {
				notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			} catch (Exception e) {
			}
			if (notify) {
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
				}
				try {
					if (useTemplate) {
						subject = PageActivationUtil.getTemplateSubject(templatePath, rr);
					} else {
						subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
					}
				} catch (Exception e) {
				}
				try {
					if (useTemplate) {
						message = PageActivationUtil.getTemplateMessage(templatePath, rr);
					} else {
						message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
					}
				} catch (Exception e) {
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
				}
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
				}
				try {
					pages = PageActivationUtil.getPages(dateRolloutNode);
				} catch (Exception e) {
					log.error("GS Page Activator - failed to get initial page count");
					e.printStackTrace();
					return;
				}
				Resource source = rr.resolve(srcPath);
				if (!source.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					if (pages != null && !pages.isEmpty()) {
						for (String targetPath : pages) {
							Resource target = rr.resolve(targetPath);
							if (!target.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
								if (message != null && message.trim().length() > 0) {
									try {
										String branch = getBranch(targetPath);
										ResourceResolver resourceResolver = source.getResourceResolver();
										// get the email addresses configured in
										// page
										// properties of the council's homepage
										Page homepage = resourceResolver.resolve(branch + "/en").adaptTo(Page.class);
										ValueMap valuemap = homepage.getProperties();
										String email1 = (String) valuemap.get("email1");
										String email2 = (String) valuemap.get("email2");
										log.info("**** GirlScoutsNotificationAction: sending email to "
												+ branch.substring(9) + " email1:" + email1 + ", email2:" + email2
												+ " *****");
										System.err.println("**** GirlScoutsNotificationAction: sending email to "
												+ branch.substring(9) + " email1:" + email1 + ", email2:" + email2
												+ " *****");
										send(srcPath, targetPath, email1, email2, valuemap, subject, message, rr);
										dateRolloutNode.setProperty(PARAM_NOTIFICATIONS_SENT, Boolean.TRUE);
									} catch (WCMException | RepositoryException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void processGSUSANotifications(String path, Session session) {
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			Set<String> pages = null;
			String subject = DEFAULT_NOTIFICATION_SUBJECT;
			StringBuffer html = new StringBuffer();
			html.append(DEFAULT_REPORT_HEAD);
			html.append("<body>");
			html.append("<p>" + DEFAULT_REPORT_SUBJECT + "</p>");
			html.append("<p>" + DEFAULT_REPORT_GREETING + "</p>");
			html.append("<p>" + DEFAULT_REPORT_INTRO + "</p>");
			Date runtime = new Date();
			html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
			String message = "", templatePath = "", srcPath = "";
			Boolean notify = false, useTemplate = false, delay = false, crawl = false, activate = true;
			try {
				notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			} catch (Exception e) {
			}
			try {
				crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
			} catch (Exception e) {
			}
			try {
				activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
			} catch (Exception e) {
			}
			try {
				delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
			} catch (Exception e) {
			}
			try {
				useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
			} catch (Exception e) {
			}
			try {
				if (useTemplate) {
					subject = PageActivationUtil.getTemplateSubject(templatePath, rr);
				} else {
					subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
				}
			} catch (Exception e) {
			}
			try {
				if (useTemplate) {
					message = PageActivationUtil.getTemplateMessage(templatePath, rr);
				} else {
					message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
				}
			} catch (Exception e) {
			}
			try {
				templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
			} catch (Exception e) {
			}
			try {
				srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
			} catch (Exception e) {
			}
			try {
				pages = PageActivationUtil.getCouncils(dateRolloutNode);
			} catch (Exception e) {
				log.error("GS Page Activator - failed to get initial page count");
				e.printStackTrace();
				return;
			}
			html.append("<p>This workflow will " + (notify ? "" : "not ") + "send emails to councils.</p>");
			html.append("<p>This workflow will " + (activate ? "" : "not ") + "activate pages upon completion.</p>");
			if (activate) {
				html.append("<p>This workflow will " + (delay ? "" : "not ")
						+ "delay the page activations until tonight.</p>");
			}
			if (useTemplate) {
				html.append("<p>An email template is in use. The path to the template is " + templatePath + "</p>");
			}
			html.append("<p>The email subject is " + subject + "</p>");
			html.append("<p>The email message is: " + message + "</p>");
			html.append("<p>The following councils have been selected:</p>");
			for (String page : pages) {
				html.append("<p>" + page + "</p>");
			}
			html.append("<p><b>Processing:</b><br>Source Page:" + srcPath + "</p>");
		}
	}

	public void execute(Resource source, Resource target, String subject, String message, LiveRelationship relation, ResourceResolver rr)
					throws WCMException {
		if (source == null) {
			System.err.println("Source is null. Quit");
			return;
		}
		if (target == null) {
			System.err.println("Target is null. Quit");
			return;
		}
		Node sourceNode = (Node)source.adaptTo(Node.class);
		Node targetNode = (Node)target.adaptTo(Node.class);
		if (sourceNode == null) {
			System.err.println("Cannot access source node: " + source + ". Quit.");
			return;
		}
		if (targetNode == null) {
			System.err.println("Cannot access target node: " + target + ". Quit.");
			return;
		}
		LiveStatus status = relation.getStatus();
		//one or more child components were unlocked on that page
		if(status!=null && status.isPage()){
			String sourcePath = source.getPath();
			String targetPath = target.getPath();
			if(!"".equals(message)){
				String branch = getBranch(targetPath);
				log.info("**** GirlScoutsNotificationAction: sending email to "+branch.substring(9)+" *****");
				ResourceResolver resourceResolver = source.getResourceResolver();
				//get the email addresses configured in page properties of the council's homepage
				Page homepage = resourceResolver.resolve(branch+"/en").adaptTo(Page.class);
				ValueMap valuemap = homepage.getProperties();
				String email1=(String)valuemap.get("email1");
				String email2=(String)valuemap.get("email2");
				send(sourcePath,targetPath,email1,email2,valuemap, subject, message, rr);
			}else{
				throw new WCMException("Rollout Notification Error - Unable to resolve email addresses");
			}
		
		}else{
			System.err.println("relation status issue");
		}
	}
	
	// send email using cq email service
	public void send(String nationalPage, String councilPage,String email1, String email2, ValueMap vm, String subject, String message, ResourceResolver rr) 
			throws WCMException{
		List<String> toAddresses = new ArrayList<String>();
		String html = "";
		try {
			toAddresses.add(email1);
			toAddresses.add(email2);
			html = message
					.replaceAll("<%template-page%>", getURL(nationalPage)).replaceAll("&lt;%template-page%&gt;", getURL(nationalPage))
					.replaceAll("<%council-page%>",getRealUrl(councilPage, vm)).replaceAll("&lt;%council-page%&gt;", getRealUrl(councilPage, vm))
					.replaceAll("<%council-author-page%>", getURL(councilPage)).replaceAll("&lt;%council-author-page%&gt;", "https://authornew.girlscouts.org" + getURL(councilPage))
					.replaceAll("<%a", "<a").replaceAll("<%/a>","</a>").replaceAll("&lt;%a", "<a").replaceAll("&lt;%/a&gt;", "</a>");
			html = html.replaceAll("&lt;","<").replaceAll("&gt;", ">");
			gsEmailService.sendEmail(subject, toAddresses, html);
		} catch (Exception e) {
			log.info("Failed to send email message subject:%s, toAddresses:%s, body:%s", subject, toAddresses.toArray(),
					html);
			System.err.println("Failed to send email message subject:" + subject + ", toAddresses:"
					+ toAddresses.toArray()
					+ ", body:" + html);
            e.printStackTrace();
		}

	}
	public String getURL(String path){
		if (path.endsWith("/jcr:content")) {
            path = path.substring(0, path.lastIndexOf('/'));
        }
		return path+".html";
	}
	
	public String getRealUrl(String path, ValueMap vm){
		if (path.endsWith("/jcr:content")) {
            path = path.substring(0, path.lastIndexOf('/'));
		}
		if(vm.containsKey("domain") && !vm.get("domain").equals("")){
			try{
				String pagePath = path.substring(path.indexOf("/", path.indexOf("/", path.indexOf("/") + 1) +1), path.length());
				return vm.get("domain") + pagePath + ".html";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return (path+".html").replaceFirst("/content/([^/]+)","https://www.$1.org");
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	private String getBranch(String path) throws WCMException {
		Matcher matcher = BRANCH_PATTERN.matcher(path);
		if (matcher.find()) {
			return matcher.group();
		} else {
			throw new WCMException("Cannot get branch: " + path);
		}
	}

	private List<String> getGsusaEmails() {
		List<String> toAddresses = new ArrayList<String>();
		Resource addressesRes = rr.resolve("/etc/msm/rolloutreports");
		ValueMap vm = ResourceUtil.getValueMap(addressesRes);
		try {
			String[] addresses = vm.get("emails", String[].class);
			if (addresses != null) {
				for (String address : addresses) {
					toAddresses.add(address);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toAddresses;
	}

}
