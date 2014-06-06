package org.girlscouts.cq.workflow;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;

@Component
@Service
public class CustomSendEmailProcess implements WorkflowProcess {
	private static final String TYPE_JCR_PATH = "JCR_PATH";
    protected MailService mailService;

    private static final Logger log = LoggerFactory
	    .getLogger(CustomProcessStep.class);

    @Reference
    private MessageGatewayService messageGatewayService;

    @Property(value = "Custom Email Process")
    static final String LABEL = "process.label";

    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args)
	    throws WorkflowException {
	WorkflowData workflowData = item.getWorkflowData();
	Workflow workflow = item.getWorkflow();
	log.error("######## workItemPath:" + workflow.getId());

	if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
	    String path = workflowData.getPayload().toString();
		log.error("######## path:" + path);

	    try {
		Session jcrSession = session.adaptTo(Session.class);

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

			Map propertyMap = getPropertyMap(workflowData, workflow, jcrSession);
			log.error("#*&@^#*&##*($&@#" + propertyMap.entrySet().toString());
			StrSubstitutor sub = new StrSubstitutor(propertyMap);

			message = sub.replace(unrefinedMessage);

			log.error(message);

			try {

			    if (messageGatewayService != null) {
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);
				HtmlEmail email = new HtmlEmail();
				ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
				email.setSubject(sub.replace(workflow.getWorkflowModel().getTitle()));
				try {
				    emailRecipients.add(new InternetAddress(
					    address));
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

    private Map getPropertyMap(WorkflowData data, Workflow item, Session session) {
	Map map = new HashMap();

	try {
	    /*PropertyIterator iter = node.getProperties();
	    while (iter.hasNext()) {
		javax.jcr.Property prop = (javax.jcr.Property) iter.next();
		map.put(prop.getName(), prop.getValue().getString());
	    */
		
	    map.put("model.title", item.getWorkflowModel().getTitle());
	    map.put("model.description", item.getWorkflowModel().getDescription());
	    map.put("model.id", item.getWorkflowModel().getId());
	    map.put("model.version", item.getWorkflowModel().getVersion());
	    map.put("initiator.id", item.getInitiator());
        map.put("payload.path", data.getPayload().toString());
        map.put("payload.type", data.getPayloadType());
        map.put("comment", getComment(item, session));
	} catch (Exception e) {

	    log.error(e.getMessage());
	}

	return map;
    }
    
    private String getComment(Workflow item, Session session){
		String comment = null;
    	try{
    	
    	String commentPath = item.getId();
	    Node content1 = session.getNode(commentPath + "/history");
	    NodeIterator iter = content1.getNodes();
	    iter.nextNode();
	    Node content2 = iter.nextNode();
	    content2 = session.getNode(content2.getPath() + "/workItem/metaData/");
	    comment = content2.getProperty("comment").getValue().getString();

		}
		catch (Exception e){
			e.printStackTrace();
		}
	    return comment;
}
}
