package org.girlscouts.cq.livecopy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;

@SuppressWarnings("deprecation")
@Component(metatype=false)
@Service
public class GirlScoutsNotificationActionFactory implements LiveActionFactory<LiveAction> {

	@Reference
	private static MessageGatewayService messageGatewayService;

	//	@Property(name = "message", label = "email message", description = "controller.")
	//	private String emlTemplate;

	@Property(value="gsNotification")
	static final String actionname = LiveActionFactory.LIVE_ACTION_NAME;


	public LiveAction createAction(Resource resource) throws WCMException {
		ValueMap config;
		if (resource == null || resource.adaptTo(ValueMap.class) == null) {
			config = null;
		} else {
			config = resource.adaptTo(ValueMap.class);
		}

		return new GirlScoutsNotificationAction(config,actionname);
	}

	public String createsAction() {
		return actionname;
	}

	private static class GirlScoutsNotificationAction implements LiveAction {
		private String name;
		private ValueMap configs;
		private static final Logger log = LoggerFactory.getLogger(GirlScoutsNotificationAction.class);
		public GirlScoutsNotificationAction(ValueMap config, String actionname){
			name=actionname;
			configs = config;
		}
		public void execute(Resource source, Resource target,
				LiveRelationship relation, boolean autosave, boolean isResetRollout)
						throws WCMException {
			log.error("*** Executing GirlScoutsNotificationAction *** ");
			if (source == null) {
				log.info("Source is null. Quit");
				return;
			}
//			if (target == null) {
//				log.info("Target is null. Quit");
//				return;
//			}
//						Node sourceNode = (Node)source.adaptTo(Node.class);
//						Node targetNode = (Node)target.adaptTo(Node.class);
//			if (sourceNode == null) {
//				log.error("Cannot access source node: " + source + ". Quit.");
//				return;
//			}
//			if (targetNode == null) {
//				log.error("Cannot access target node: " + target + ". Quit.");
//				return;
//			}

//			String sourcePath = source.getPath();
//			String targetPath = target.getPath();



			if(relation.getStatus().isCancelled()){
				if(configs !=null)
				send((String)configs.get("emlTemplate"));
			}

		}

		public void send(String html) {

			try {
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService
						.getGateway(HtmlEmail.class);

				HtmlEmail email = new HtmlEmail();
				ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();

				emailRecipients.add(new InternetAddress("cwu@northpointdigital.com"));


				email.setSubject("GSUSA rollout notification");
				String msg =html;
				ResourceResolver resourceResolver = null;

				//				try {
				//					resourceResolver = resourceResolverFactory.getResourceResolver(null);
				//				} catch (Exception e) {
				//					log.error(e.getMessage());
				//				}
				//
				//				Node msgNode = (Node)resourceResolver.resolve("/etc/msm/rolloutconfigs/gsdefault/jcr:content/gsReferencesUpdate").adaptTo(Node.class);
				//				msg = msgNode.getProperty("msg").getString();
				email.setHtmlMsg(html);
				if(!emailRecipients.isEmpty()){
					email.setTo(emailRecipients);
					messageGateway.send(email);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		//		private static final Pattern BRANCH_PATTERN = Pattern.compile("^(/content/[^/]+)/?");
		//		private String getBranch(String path) throws WCMException {
		//			Matcher matcher = BRANCH_PATTERN.matcher(path);
		//			if (matcher.find()) {
		//				return matcher.group();
		//			} else {
		//				throw new WCMException("Cannot get level " + BRANCH_LEVEL + " branch: " + path);
		//			}
		//		}

		public String getName() {
			return name;
		} 
		@Deprecated
        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave, boolean isResetRollout)
                throws WCMException {
        }

        @Deprecated
        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave) throws WCMException {
        }

        @Deprecated
        public String getParameterName() {
            return null;
        }

        @Deprecated
        public String[] getPropertiesNames() {
            return null;
        }

        @Deprecated
        public int getRank() {
            return 0;
        }

        @Deprecated
        public String getTitle() {
            return null;
        }

        @Deprecated
        public void write(JSONWriter writer) throws JSONException {
        }

	}
}
