package org.girlscouts.web.cq.workflow;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component
@Service
public class CustomProcessStep implements WorkflowProcess {
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

	if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
	    String path = workflowData.getPayload().toString();
	    try {
		Session jcrSession = session.getSession();

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

			Map propertyMap = getPropertyMap(node);

			StrSubstitutor sub = new StrSubstitutor(propertyMap);

			message = sub.replace(unrefinedMessage);

			log.error(message);

			try {

			    if (messageGatewayService != null) {
				MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);
				HtmlEmail email = new HtmlEmail();
				ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
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

    private Map getPropertyMap(Node node) {
	Map map = new HashMap();

	try {
	    PropertyIterator iter = node.getProperties();
	    while (iter.hasNext()) {
		javax.jcr.Property prop = (javax.jcr.Property) iter.next();
		map.put(prop.getName(), prop.getValue().getString());
	    }
	} catch (RepositoryException e) {

	    log.error(e.getMessage());
	}

	return map;
    }

}
