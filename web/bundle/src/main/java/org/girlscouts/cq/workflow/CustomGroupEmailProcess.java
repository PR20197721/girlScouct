package org.girlscouts.cq.workflow;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.day.cq.mailer.MailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;


@Component
@Service
public class CustomGroupEmailProcess implements WorkflowProcess {
	private static final String TYPE_JCR_PATH = "JCR_PATH";
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	protected MailService mailService;

    private static final Logger log = LoggerFactory
	    .getLogger(CustomGroupEmailProcess.class);

    @Reference
    private MessageGatewayService messageGatewayService;
	private String initiatorEmail;

	  @Reference

    @Property(value = "Custom Group Email Process")
    static final String LABEL = "process.label";

    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args)
	    throws WorkflowException {
    	ResourceResolver resolver = null;
    	try {
    	resolver = this.resourceResolverFactory.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object)session.getSession()));
        }
        catch (Exception e){
        	log.error(e.getMessage());
        }
    WorkflowData workflowData = item.getWorkflowData();
	Workflow workflow = item.getWorkflow();
	log.error("######## workItemPath:" + args.keySet());
	log.error("######## workItemPath:" + item.getMetaDataMap());
	


	if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
	    String path = workflowData.getPayload().toString();
		//log.error("######## path:" + "/content/girlscouts-usa/en/about-our-council");

		
		Page page = (Page) resolver.resolve("/content/girlscouts-usa/en/about-our-council").adaptTo(Page.class);
    	//Page page = (Page)resolver.adaptTo(Page.class);
log.debug("################################");
log.error("######## rootPATH" + page.getAbsoluteParent(2).getPath());
	    try {
		Session jcrSession = session.getSession();
		//UserManager userManager = this.userManagerFactory.createUserManager(session.getSession());
   /* 	Group groupMgr = (Group)resolver.adaptTo(Group.class);
log.error("#@*&$@(#)*6387^98273652987356" + groupMgr.getMembers().next().toString());*/
		// Get the jcr node that represents the form submission
		Node node = (Node) jcrSession.getItem(path);
		
		if (node != null) {

		    String emailTemplate = "";
		    String templatePath = "";
		    
		    // Attempt to get the email template
		    emailTemplate = args.get("template", String.class);
		    // If template text is not found try to get template from
		    // path
		    if (emailTemplate == null || emailTemplate.equals("")) {
			templatePath = args.get("templatePath", String.class);
			
			log.error("######## templatePath:" + templatePath);

			try {
			    Node content = jcrSession.getNode(templatePath
				    + "/" + "jcr:content");
			    InputStream is = content.getProperty("jcr:data")
				    .getBinary().getStream();
			    InputStreamReader r = new InputStreamReader(is,
				    "utf-8");
			    StringWriter w = new StringWriter();			   
			    IOUtils.copy(r, w);
			    emailTemplate = w.toString();
			    
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }

		    // If emailTemplate is still not found or is empty log an
		    // error
		    if (emailTemplate == null || emailTemplate.equals("")) {
			log.error("Unable to locate email template OR Template is empty");
		    } else {

		    }
		    
		    // Dissect the email template
		    String firstLine = emailTemplate.substring(0,
			    emailTemplate.indexOf('\n'));
		    String unrefinedMessage = emailTemplate
			    .substring(emailTemplate.indexOf("\n") + 1);
		    String to = firstLine.substring(0, firstLine.indexOf(':'));
		    String address = firstLine
			    .substring(firstLine.indexOf(':') + 1);
		    address = address.trim();
		    to = to.trim();

		    if (to.equalsIgnoreCase("to")) {
			String message = "";
			
			Map propertyMap = getPropertyMap(workflowData, workflow, item, jcrSession, resolver);
			log.error("#*&@^#*&##*($&@#" + propertyMap.entrySet().toString());
			StrSubstitutor sub = new StrSubstitutor(propertyMap);

			message = sub.replace(unrefinedMessage);

			log.error(message);
			log.error("Email Address" + sub.replace(address));
			try {

			    if (messageGatewayService != null) {
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);
				HtmlEmail email = new HtmlEmail();
				ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
				email.setSubject(sub.replace(workflow.getWorkflowModel().getTitle()));
				try {

				    emailRecipients.add(new InternetAddress(
				    		sub.replace(address)));
				} catch (AddressException e) {

				    log.error(e.getMessage());
				}	
				email.setTo(emailRecipients);
				email.setHtmlMsg(message);

				messageGateway.send(email);

			    } else {
				log.error("messageGatewayService is null");
			    }

			} catch (EmailException e) {

			    e.printStackTrace();
			}

		    } else {
			log.error("Email template is invalid first line does not inlcude To:");
		    }

		}
	    } catch (RepositoryException e) {
		throw new WorkflowException(e.getMessage(), e);
	    }
	}
    }

    private Map getPropertyMap(WorkflowData data, Workflow flow, WorkItem item, Session session, ResourceResolver resolver) {
	Map map = new HashMap();

	try {
		
	    map.put("model.title", flow.getWorkflowModel().getTitle());
	    map.put("model.description", flow.getWorkflowModel().getDescription());
	    map.put("model.id", flow.getWorkflowModel().getId());
	    map.put("model.version", flow.getWorkflowModel().getVersion());
	    map.put("initiator.id", flow.getInitiator());
        map.put("payload.path", data.getPayload().toString());
        map.put("payload.type", data.getPayloadType());
        map.put("comment", item.getMetaDataMap().get("comment", String.class));
        map.put("initiator.email", getInitiatorEmail(resolver, flow));
	
	} catch (Exception e) {

	    log.error(e.getMessage());
	}

	return map;
    }
    private String getInitiatorEmail(ResourceResolver resolver, Workflow workflow){
	    try{  
	    	/*UserPropertiesManager upMgr = (UserPropertiesManager)resolver.adaptTo(UserPropertiesManager.class);
	    	UserProperties initiator = null;
	    	String initiatorId = workflow.getInitiator();
	    	initiator = upMgr.getUserProperties(initiatorId, "profile");
	    	log.error("WEOIFWOEIFHWEOIFRGORG" + initiator.getProperty("email"));
	    	initiatorEmail = initiator.getProperty("email");*/
	    	
	    	Group groupMgr = (Group)resolver.adaptTo(Group.class);
log.error("#@*&$@(#)*6387^98273652987356" + groupMgr.getMembers().next().toString());
	    	}
	    catch(Exception e){
	    	e.printStackTrace();
	    	}	
	    return initiatorEmail;
	    }
}
