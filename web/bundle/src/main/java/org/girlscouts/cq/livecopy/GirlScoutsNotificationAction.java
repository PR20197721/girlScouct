package org.girlscouts.cq.livecopy;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
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

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveStatus;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@SuppressWarnings("deprecation")
@Component(metatype=false)
@Service
public class GirlScoutsNotificationAction {
	
	@Reference
	public MessageGatewayService messageGatewayService;

	private Resource source, target;
	Boolean dontSend;
	String message;
	private final Logger log = LoggerFactory.getLogger(GirlScoutsNotificationAction.class);
	public GirlScoutsNotificationAction(Resource source, Resource target, Boolean dontSend, String message){
		source = this.source;
		target = this.target;
		dontSend = this.dontSend;
		message = this.message;
	}
	
	public void execute()
					throws WCMException {
		if (source == null) {
			log.info("Source is null. Quit");
			return;
		}
		if (target == null) {
			log.info("Target is null. Quit");
			return;
		}

		Node sourceNode = (Node)source.adaptTo(Node.class);
		Node targetNode = (Node)target.adaptTo(Node.class);
		if (sourceNode == null) {
			log.error("Cannot access source node: " + source + ". Quit.");
			return;
		}
		if (targetNode == null) {
			log.error("Cannot access target node: " + target + ". Quit.");
			return;
		}
		LiveStatus status = relation.getStatus();
		//one or more child components were unlocked on that page
		if(status!=null && status.isPage() && 
				status.getAdvancedStatus("msm:isTargetCancelledChild")!=null 
				&& status.getAdvancedStatus("msm:isTargetCancelledChild")){
			String sourcePath = source.getPath();
			String targetPath = target.getPath();
			if(configs !=null && configs.get("emlTemplate")!=null){
				String branch = getBranch(targetPath);
				log.info("**** GirlScoutsNotificationAction: sending email to "+branch.substring(9)+" *****");
				ResourceResolver resourceResolver = source.getResourceResolver();
				//get the email addresses configured in page properties of the council's homepage
				Page homepage = resourceResolver.resolve(branch+"/en").adaptTo(Page.class);
				ValueMap valuemap = homepage.getProperties();
				String email1=(String)valuemap.get("email1");
				String email2=(String)valuemap.get("email2");
				send(sourcePath,targetPath,email1,email2,valuemap);
			}else{
				throw new WCMException("Email template configuration was not found under /etc/msm/rolloutconfigs/gsdefault/jcr:content/gsNotification");
			}
		
		}

	}
	
	private final Pattern BRANCH_PATTERN = Pattern.compile("^(/content/[^/]+)/?");
    private String getBranch(String path) throws WCMException {
        Matcher matcher = BRANCH_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new WCMException("Cannot get branch: " + path);
        }
    }
    //send email using cq email service
	public void send(String nationalPage, String councilPage,String email1, String email2, ValueMap vm) 
			throws WCMException{
		try {
			
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);

			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			
			if(email1!=null && !email1.isEmpty()){//primary addr
				emailRecipients.add(new InternetAddress(email1));
			}else {//send to email address for testing
				String testAddr = (String)configs.get("testEmail");
				if(testAddr!=null && !testAddr.isEmpty()){ 
					emailRecipients.add(new InternetAddress(testAddr));
				}
			}//backup addr
			if(email2!=null && !email2.isEmpty())
				emailRecipients.add(new InternetAddress(email2));

			email.setSubject("GSUSA Rollout Notification");
			String html = (String)configs.get("emlTemplate");
			//html+="<p><b>National page URL:</b> <a href='http://girlscouts-prod.adobecqms.net"+getURL(nationalPage) + "'>http://girlscouts-prod.adobecqms.net"+getURL(nationalPage) +" </a></p>";
			html+="<p><b>National page URL:</b> http://girlscouts-prod-aem61.adobecqms.net"+getURL(nationalPage) +"</p>";
			html+="<p><b>Your page URL:</b> "+getRealUrl(councilPage,vm)+"</p>";
			html+="<p>Click <a href='http://authornew.girlscouts.org"+getURL(councilPage)+"'>here</a> to edit your page.</p>";
			email.setHtmlMsg(html);
			if(!emailRecipients.isEmpty()){
				email.setTo(emailRecipients);
				//messageGateway.send(email);
			}else{
				log.error("No email address found for council :" + getBranch(councilPage));
			}

		} catch (Exception e) {
            throw new WCMException(e.getMessage(), e);
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

	public String getName() {
		return name;
	} 
}
