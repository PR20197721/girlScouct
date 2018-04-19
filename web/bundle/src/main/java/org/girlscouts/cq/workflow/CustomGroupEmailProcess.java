package org.girlscouts.cq.workflow;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.mailer.MailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.Group;
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

	@Property(value = "Custom Group Email Process")
	static final String LABEL = "process.label";

	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args)
			throws WorkflowException {
		ResourceResolver resolver = null;
		try {
			resolver = this.resourceResolverFactory
					.getResourceResolver(Collections.singletonMap(
							"user.jcr.session", (Object) session.getSession()));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		WorkflowData workflowData = item.getWorkflowData();
		Workflow workflow = item.getWorkflow();

		if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
			String path = workflowData.getPayload().toString();

			Page page = (Page) resolver.resolve(
					workflowData.getPayload().toString()).adaptTo(Page.class);

			String councilGroup = page.getAbsoluteParent(1).getName();
			String reviewerGroup = councilGroup + "-reviewers";

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
					to = to.trim();

					if (to.equalsIgnoreCase("to")) {
						String message = "";

						Map propertyMap = getPropertyMap(workflowData,
								workflow, item, jcrSession, resolver);
						StrSubstitutor sub = new StrSubstitutor(propertyMap);

						message = sub.replace(unrefinedMessage);
						log.error(message);

						try {

							if (messageGatewayService != null) {
								MessageGateway<HtmlEmail> messageGateway = messageGatewayService
										.getGateway(HtmlEmail.class);
								HtmlEmail email = new HtmlEmail();
								ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
								email.setSubject(sub.replace(workflow
										.getWorkflowModel().getTitle()));
								try {
									int missingEmails = 0;
									List<String> emails = getGroupEmails(
											resolver, reviewerGroup,
											councilGroup);

									for (int i = 0; i < emails.size(); i++) {
										if (emails.get(i) != null) {
											emailRecipients
													.add(new InternetAddress(
															emails.get(i)));

										} else {

											missingEmails++;
											log.error(missingEmails
													+ " email Addresses missing");
										}
									}

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
						} catch (Exception e) {
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

	private Map getPropertyMap(WorkflowData data, Workflow flow, WorkItem item,
			Session session, ResourceResolver resolver) {
		Map map = new HashMap();

		try {
			map.put("preview.prefix", getPreviewPrefix(resolver));
			map.put("publish.prefix", getPublishPrefix(resolver));
			map.put("author.prefix", getAuthorPrefix(resolver));
			map.put("model.title", flow.getWorkflowModel().getTitle());
			map.put("model.description", flow.getWorkflowModel()
					.getDescription());
			map.put("model.id", flow.getWorkflowModel().getId());
			map.put("model.version", flow.getWorkflowModel().getVersion());
			map.put("initiator.id", flow.getInitiator());
			map.put("payload.path", data.getPayload().toString());
			map.put("payload.type", data.getPayloadType());
			map.put("comment",
					flow.getMetaDataMap().get("startComment", String.class));
			map.put("workflow.title",
					flow.getMetaDataMap().get("workflowTitle", String.class));

		} catch (Exception e) {

			log.error(e.getMessage());
		}

		return map;
	}

	private List<String> getGroupEmails(ResourceResolver resolver,
			String reviewerGroup, String councilGroup) {
		List<String> emailAddresses = new ArrayList<String>();
		try {
			UserManager manager = (UserManager) resolver
					.adaptTo(UserManager.class);
			Group group = (Group) manager.getAuthorizable(reviewerGroup);
			log.error("/home/groups/" + councilGroup + "/" + reviewerGroup);
			if (group == null) {
				log.error("Reviewer group does not exist under correct naming conventions."
						+ " Cannot send emails to reviewers");
			} else {
				Iterator<Authorizable> iter = group.getMembers();
				while (iter.hasNext()) {
					User user = (User) iter.next();
					emailAddresses.add(user.getProperty("profile/email")[0].getString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailAddresses;
	}

	private String getPreviewPrefix(ResourceResolver resolver) {
		String hostPrefix = null;
		Externalizer externalizer = (Externalizer) resolver
				.adaptTo(Externalizer.class);
		String externalizerHost = externalizer.externalLink(resolver,
				"preview", "");
		if ((externalizerHost != null) && (externalizerHost.endsWith("/"))) {
			hostPrefix = externalizerHost.substring(0,
					externalizerHost.length() - 1);

		}
		return hostPrefix;
	}

	private String getPublishPrefix(ResourceResolver resolver) {
		String hostPrefix = null;
		Externalizer externalizer = (Externalizer) resolver
				.adaptTo(Externalizer.class);
		String externalizerHost = externalizer.externalLink(resolver,
				"publish", "");
		if ((externalizerHost != null) && (externalizerHost.endsWith("/"))) {
			hostPrefix = externalizerHost.substring(0,
					externalizerHost.length() - 1);

		}
		return hostPrefix;
	}

	private String getAuthorPrefix(ResourceResolver resolver) {
		String hostPrefix = null;
		Externalizer externalizer = (Externalizer) resolver
				.adaptTo(Externalizer.class);
		String externalizerHost = externalizer.externalLink(resolver, "author",
				"");
		if ((externalizerHost != null) && (externalizerHost.endsWith("/"))) {
			hostPrefix = externalizerHost.substring(0,
					externalizerHost.length() - 1);

		}
		return hostPrefix;
	}
}