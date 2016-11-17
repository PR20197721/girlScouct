package org.girlscouts.web.councilrollout.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveStatus;
import com.day.cq.mailer.MailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value = GirlScoutsNotificationAction.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.girlscoutsnotificationaction", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts Content Rollout Notification Service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class GirlScoutsNotificationActionImpl implements GirlScoutsNotificationAction{
	
	@Reference
	public MessageGatewayService messageGatewayService;

	private final Logger log = LoggerFactory.getLogger(GirlScoutsNotificationActionImpl.class);

	public void execute(Resource source, Resource target, String subject, String message, LiveRelationship relation, ResourceResolver rr)
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
	public void send(String nationalPage, String councilPage,String email1, String email2, ValueMap vm, String subject, String message, ResourceResolver rr) 
			throws WCMException{
		try {

			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			
			if(email1!=null && !email1.isEmpty()){//primary addr
				emailRecipients.add(new InternetAddress(email1));
			}
			if(email2!=null && !email2.isEmpty())
				emailRecipients.add(new InternetAddress(email2));

			email.setSubject(subject);
			String html = message
					.replaceAll("<%template-page%>", getURL(nationalPage)).replaceAll("&lt;%template-page%&gt;", getURL(nationalPage))
					.replaceAll("<%council-page%>",getRealUrl(councilPage, vm)).replaceAll("&lt;%council-page%&gt;", getRealUrl(councilPage, vm))
					.replaceAll("<%council-author-page%>", getURL(councilPage)).replaceAll("&lt;%council-author-page%&gt;", "https://authornew.girlscouts.org" + getURL(councilPage))
					.replaceAll("<%a", "<a").replaceAll("<%/a>","</a>").replaceAll("&lt;%a", "<a").replaceAll("&lt;%/a&gt;", "</a>");
			html = html.replaceAll("&lt;","<").replaceAll("&gt;", ">");
			html = parseHtml(html, email, rr);
			email.setHtmlMsg(html);
			if(!emailRecipients.isEmpty()){
				email.setTo(emailRecipients);
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
				messageGateway.send(email);
			}else{
				log.error("No email address found for council :" + getBranch(councilPage));
			}

		} catch (Exception e) {
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
 
    public String parseHtml(String html, HtmlEmail email, ResourceResolver rr){
    	
    	//Find images and replace them with embeds, embed the image file in the email
    	final Pattern imgPattern = Pattern.compile("<img src=\"(.*?)\"");
    	final Matcher imgMatcher = imgPattern.matcher(html);
    	final StringBuffer imgSb = new StringBuffer();
    	while(imgMatcher.find()){
    		byte[] result = null;
    		try {
    			String renditionPath = getRenditionPath(imgMatcher.group(1));
        		Resource imgRes = rr.resolve(renditionPath);
        		if(ResourceUtil.isNonExistingResource(imgRes)) {
        			imgRes = rr.resolve(renditionPath.replaceAll("%20"," "));
        			if(ResourceUtil.isNonExistingResource(imgRes)){
        				throw(new Exception("Cannot find resource: " + renditionPath));
        			}
        		}
        		Node ntFileNode = imgRes.adaptTo(Node.class);
        		Node ntResourceNode = ntFileNode.getNode("jcr:content");
        		InputStream is = ntResourceNode.getProperty("jcr:data").getBinary().getStream();
        		BufferedInputStream bin = new BufferedInputStream(is);
        		result = IOUtils.toByteArray(bin);
        		bin.close();
        		is.close();
			} catch (Exception e) {
				log.error("Input Stream Failed");
				System.out.println("Input Stream Failed");
				e.printStackTrace();
			}
    		try {
    			String fileName = imgMatcher.group(1).substring(imgMatcher.group(1).lastIndexOf('/') + 1);
    			File imgFile = new File(fileName);
    			FileUtils.writeByteArrayToFile(imgFile,result);
				imgMatcher.appendReplacement(imgSb, "<img src=cid:" + (email.embed(imgFile,fileName)));
		    	imgMatcher.appendTail(imgSb);
		    	html = imgSb.toString();
			} catch (Exception e) {
				log.error("Failed to embed image");
				e.printStackTrace();
			}
    	}
    	
    	return html;
    }
    
    public String getRenditionPath(String imgPath){
    	final Pattern pattern = Pattern.compile("/jcr:content/renditions/");
    	final Matcher matcher = pattern.matcher(imgPath);
    	if(matcher.find()){
    		return imgPath;
    	}else{
    		return imgPath + "/jcr:content/renditions/original";
    	}
    }
}
