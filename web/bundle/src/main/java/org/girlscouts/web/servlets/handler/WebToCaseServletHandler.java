package org.girlscouts.web.servlets.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.asset.api.Asset;
import com.day.cq.mailer.MailService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;

public class WebToCaseServletHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebToCaseServletHandler.class);
	protected static final String EXTENSION = "html";
	protected static final String CC_PROPERTY = "cc";
	protected static final String BCC_PROPERTY = "bcc";
	protected static final String SUBJECT_PROPERTY = "subject";
	protected static final String FROM_PROPERTY = "from";
	protected static final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
	protected static final String DISABLE_CONFIRMATION_PROPERTY = "disableConfirmation";
	protected static final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
	protected static final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";
	protected static final String TEMPLATE_PATH_PROPERTY = "templatePath";

	@Reference(policy = ReferencePolicy.STATIC)
	private SlingSettingsService slingSettings;

	@Reference
	private Replicator replicator;

	public int sendEmail(SlingHttpServletRequest request, SlingHttpServletResponse response, String emailMappingsPath,
			Dictionary<String, Object> properties, String councilCode, MailService mailService) {

		final MailService localService = mailService;

		final ResourceBundle resBundle = request.getResourceBundle(null);
		final ValueMap values = ResourceUtil.getValueMap(request.getResource());

		final String mailTo = getMailToValue(emailMappingsPath, councilCode, request);

		int status = 200;
		if (null == mailTo) {
			logger.error("The mailto configuration is missing in the form begin at " + request.getResource().getPath());
			status = 500;
		} else if (localService == null) {
			logger.error("The mail service is currently not available! Unable to send form mail.");
			status = 500;
		} else {
			try {
				final StringBuilder builder = new StringBuilder();
				builder.append(request.getScheme());
				builder.append("://");
				builder.append(request.getServerName());
				if ((request.getScheme().equals("https") && request.getServerPort() != 443)
						|| (request.getScheme().equals("http") && request.getServerPort() != 80)) {
					builder.append(':');
					builder.append(request.getServerPort());
				}
				builder.append(request.getRequestURI());
				final StringBuilder buffer = new StringBuilder();
				String text = resBundle.getString("You've received a new form based mail from {0}.");
				text = text.replace("{0}", builder.toString());
				buffer.append(text);
				buffer.append("\n\n");
				buffer.append(resBundle.getString("Values"));
				buffer.append(":\n\n");
				// we sort the names first - we use the order of the form field and
				// append all others at the end (for compatibility)

				// let's get all parameters first and sort them alphabetically!
				final List<String> contentNamesList = new ArrayList<String>();
				final Iterator<String> names = FormsHelper.getContentRequestParameterNames(request);
				while (names.hasNext()) {
					final String name = names.next();
					contentNamesList.add(name);
				}
				Collections.sort(contentNamesList);

				List<String> confirmationEmailAddresses = new ArrayList<>();
				final List<String> namesList = new ArrayList<>();
				final Iterator<Resource> fields = FormsHelper.getFormElements(request.getResource());
				while (fields.hasNext()) {
					final Resource field = fields.next();
					final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, field);
					for (final FieldDescription desc : descs) {
						// remove from content names list
						contentNamesList.remove(desc.getName());
						if (!desc.isPrivate()) {
							namesList.add(desc.getName());
						}
						ValueMap childProperties = ResourceUtil.getValueMap(field);
						if (childProperties.get("confirmationemail", false)) {
							final String[] pValues = request.getParameterValues(desc.getName());
							for (final String v : pValues) {
								confirmationEmailAddresses.add(v);
							}
						}
					}
				}

				namesList.addAll(contentNamesList);
				final List<RequestParameter> attachments = new ArrayList<>();
				Map<String, List<String>> formFields = new HashMap<>();

				for (final String name : namesList) {
					if (name.equals("Submit") || name.equals("file-upload-max-size")) {
						continue;
					}
					final RequestParameter rp = request.getRequestParameter(name);
					if (rp == null) {
						// see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
						logger.debug("skipping form element {} from mail content because it's not in the request",
								name);
					} else if (rp.isFormField()) {
						buffer.append(name);
						buffer.append(" : \n");
						final String[] pValues = request.getParameterValues(name);
						for (final String v : pValues) {
							if (null == formFields.get(name)) {
								List<String> formField = new ArrayList<String>();
								formField.add(v);
								formFields.put(name, formField);
							} else {
								formFields.get(name).add(v);
							}
							buffer.append(v);
							buffer.append("\n");
						}
						buffer.append("\n");
					} else if (rp.getSize() > 0) {
						attachments.add(rp);

					} else {
						// ignore
					}
				}

				final Email email;
				if (attachments.size() > 0) {
					buffer.append("\n");
					buffer.append(resBundle.getString("Attachments"));
					buffer.append(":\n");
					final MultiPartEmail mpEmail = new MultiPartEmail();
					email = mpEmail;
					for (final RequestParameter rp : attachments) {
						final ByteArrayDataSource ea = new ByteArrayDataSource(rp.getInputStream(),
								rp.getContentType());
						mpEmail.attach(ea, rp.getFileName(), rp.getFileName());

						buffer.append("- ");
						buffer.append(rp.getFileName());
						buffer.append("\n");
					}
				} else {
					email = new SimpleEmail();
				}

				email.setCharset("utf-8");
				email.setMsg(buffer.toString());
				// mailto
				email.addTo(mailTo);
				// cc
				final String[] ccRecs = values.get(CC_PROPERTY, String[].class);
				if (ccRecs != null) {
					for (final String rec : ccRecs) {
						email.addCc(rec);
					}
				}
				// bcc
				final String[] bccRecs = values.get(BCC_PROPERTY, String[].class);
				if (bccRecs != null) {
					for (final String rec : bccRecs) {
						email.addBcc(rec);
					}
				}

				final String subject = values.get(SUBJECT_PROPERTY, resBundle.getString("Form Mail"));
				email.setSubject(subject);
				final String fromAddress = values.get(FROM_PROPERTY, "");
				if (fromAddress.length() > 0) {
					// email.setFrom(fromAddress);
					email.addReplyTo(fromAddress);
				}
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
							new Object[] { fromAddress, mailTo, subject, buffer });
				}
				localService.sendEmail(email);

				boolean storeContent = values.get("storeContent", false);
				ResourceResolver resourceResolver = request.getResourceResolver();
				if (storeContent) {
					String contentPath = values.get("action", null);
					if (contentPath != null && !contentPath.isEmpty()) {
						Node contentBaseNode = null;
						Resource contentResource = resourceResolver.getResource(contentPath);
						if (contentResource == null) {
							contentBaseNode = getFormStorageNode(
									resourceResolver.getResource("/content/usergenerated").adaptTo(Node.class),
									contentPath);
						} else {
							contentBaseNode = contentResource.adaptTo(Node.class);
						}
						Date now = new Date();
						Random rand = new Random();
						String nodeId = now.getTime() + "_" + rand.nextInt(50);
						Node submissionNode = contentBaseNode.addNode(nodeId, "sling:Folder");
						Enumeration<String> paramNames = request.getParameterNames();
						Map<String, List<String>> paramMap = new HashMap<>();
						while (paramNames.hasMoreElements()) {
							String n = paramNames.nextElement();
							logger.error("################PARAM NAME IS: " + n);

							if (!n.contains(":") && !n.equals("_charset_") && !n.equals("Submit")
									&& !n.equals("file-upload-max-size")) {
								for (RequestParameter param : Arrays.asList(request.getRequestParameters(n))) {
									if (param.getContentType() == null) {
										List<String> valLst = new ArrayList<>(1);
										valLst.add(param.getString());
										paramMap.merge(n, valLst, (l1, l2) -> {
											l1.addAll(l2);
											return l1;
										});
									} else {
										if (param.getSize() > 0) {
											String val = param.getFileName();
											submissionNode.setProperty(n, val);
										}
									}
								}
							}
						}
						for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
							if (entry.getValue().size() > 1) {
								Object[] objVals = entry.getValue().toArray();
								String[] strVals = new String[objVals.length];
								for (int i = 0; i < objVals.length; i++) {
									strVals[i] = Optional.ofNullable(objVals[i]).map(Object::toString).orElse(null);
								}
								submissionNode.setProperty(entry.getKey(), strVals);
							} else {
								submissionNode.setProperty(entry.getKey(), entry.getValue().get(0));
							}
						}
						logger.error("Submission Node path is: " + submissionNode.getPath());
						submissionNode.save();
						Set<String> runmodes = slingSettings.getRunModes();
						boolean isPublish = false;
						for (String mode : runmodes) {
							if (mode.equalsIgnoreCase("publish")) {
								isPublish = true;
							}
						}
						if (isPublish) {
							replicator.replicate(contentBaseNode.getSession(), ReplicationActionType.INTERNAL_POLL,
									submissionNode.getPath());
						}

					}
				}

				boolean disableConfirmations = values.get("disableConfirmationEmails", false);
				if (!disableConfirmations) {
					final HtmlEmail confEmail;
					confEmail = new HtmlEmail();
					confEmail.setCharset("utf-8");
					String confBody = getTemplate(request, values, formFields, confEmail, resourceResolver,
							attachments);
					if (!("").equals(confBody)) {
						confEmail.setHtmlMsg(confBody);
						// mailto
						for (String confEmailAddress : confirmationEmailAddresses) {
							confEmail.addTo(confEmailAddress);
						}
						final String[] confMailTo = values.get(CONFIRM_MAILTO_PROPERTY, String[].class);
						if (confMailTo != null) {
							for (final String rec : confMailTo) {
								confEmail.addBcc(rec);
							}
						}

						// subject and from address
						final String confSubject = values.get(CONFIRMATION_SUBJECT_PROPERTY,
								resBundle.getString("Form Submission Received"));
						confEmail.setSubject(confSubject);
						final String confFromAddress = values.get(CONFIRMATION_FROM_PROPERTY,
								values.get(FROM_PROPERTY, ""));
						if (confFromAddress.length() > 0) {
							// confEmail.setFrom(confFromAddress);
							confEmail.addReplyTo(confFromAddress);
						}
						if (this.logger.isDebugEnabled()) {
							this.logger.debug(
									"Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
									new Object[] { confFromAddress, confirmationEmailAddresses, confSubject,
											confBody });
						}
						if (!attachments.isEmpty()) {
							for (RequestParameter rp : attachments) {
								final ByteArrayDataSource ea = new ByteArrayDataSource(rp.getInputStream(),
										rp.getContentType());
								confEmail.attach(ea, rp.getFileName(), rp.getFileName());
							}
						}
						localService.sendEmail(confEmail);
					} else {
						logger.debug("Email body null for " + request.getResource().getPath());
					}
				}

			} catch (Exception e) {
				logger.error("Error sending email: " + e.getMessage(), e);
				status = 500;
			}
		}

		return status;
	}

	public String getTemplate(SlingHttpServletRequest request, ValueMap values, Map<String, List<String>> formFields,
			HtmlEmail confEmail, ResourceResolver rr, List<RequestParameter> attachments) {
		try {
			String templatePath = values.get(TEMPLATE_PATH_PROPERTY,
					"/content/girlscouts-template/en/email-templates/default_template");
			if (templatePath.trim().isEmpty()) {
				templatePath = "/content/girlscouts-template/en/email-templates/default_template";
			}
			logger.error("GSMailServlet temnplate Path is:  " + templatePath);
			String parsed = "";
			Resource templateResource = rr.getResource(templatePath);
			logger.error("GSMailServlet got template path ");
			if (templateResource != null) {
				Resource dataResource = templateResource.getChild("jcr:content/data");
				logger.error("Data resource path is: " + dataResource.getPath());
				ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
				logger.error("GSMailServlet template content is:  " + templateProps.get("content", ""));
				parsed = parseHtml(templateProps.get("content", ""), formFields);
			}
			if (parsed.isEmpty()) {
				parsed = "The following fields and values were submitted for form: " + request.getParameter(":formid")
						+ "<br/> \n";
				for (String key : formFields.keySet()) {
					List<String> lvalues = formFields.get(key);
					String valstring = "";
					for (String lval : lvalues) {
						valstring = valstring.concat(lval + " ");
					}
					parsed = parsed.concat(" Name: " + key + " Value: " + valstring + "<br/> \n");

				}
			}
			if (!attachments.isEmpty()) {
				parsed = parsed.concat("<br/> \n Attachments: <br/>\n");
				for (RequestParameter rp : attachments) {
					parsed = parsed.concat(rp.getFileName() + " <br/> \n");
				}
			}
			String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
					+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
					+ "<title>Girl Scouts</title></head>";
			String html = head + "<body>" + parsed + "</body></html>";
			return html;
		} catch (Exception e) {
			logger.error("No valid template found for " + request.getResource().getPath());
			e.printStackTrace();
			return "";
		}
	}

	public String parseHtml(String html, Map<String, List<String>> fields) {
		// Part 1: Insert field variables whenever %%{field_id}%% is found
		final Pattern pattern = Pattern.compile("%%(.*?)%%");
		final Matcher matcher = pattern.matcher(html);
		final StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			List<String> matched = fields.get(matcher.group(1));
			if (matched != null) {
				if (matched.size() > 1) {
					matcher.appendReplacement(sb, matched.toString());
				} else if (matched.toString().length() >= 1) {
					matcher.appendReplacement(sb, matched.toString().substring(1, matched.toString().length() - 1));
				}
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		return html;
	}

	private Node getFormStorageNode(Node node, String path) throws RepositoryException {
		Node rootNode = node;
		String relativePath = path.replaceAll("/content/usergenerated/", "");
		String[] subNames = relativePath.split("/");
		for (int i = 0; i < subNames.length; i++) {
			String temp = subNames[i];
			if (rootNode.hasNode(temp)) {
				rootNode = rootNode.getNode(temp);
			} else {
				Node tempNode = rootNode.addNode(temp, "sling:Folder");
				rootNode = tempNode;

			}
		}
		return rootNode;
	}

	private String getMailToValue(String emailMapPath, String councilCode, SlingHttpServletRequest request) {
		String emailId = null;
		Map<String, String> councilEmailMap = new HashMap<>();
		ResourceResolver resolver = request.getResourceResolver();
		Resource councilMapResource = resolver.getResource(emailMapPath);
		if (null != councilMapResource) {
			Asset asset = councilMapResource.adaptTo(Asset.class);
			if (null != asset) {
				try {
					String line = "";
					BufferedReader br = new BufferedReader(
							new InputStreamReader(asset.getRendition("original").getStream()));
					while ((line = br.readLine()) != null) {
						String csvData[] = line.split(",");
						councilEmailMap.put(csvData[0], csvData[1]);
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		for (Map.Entry<String, String> map : councilEmailMap.entrySet()) {
			if (null != councilCode && map.getKey().equals(councilCode)) {
				emailId = map.getValue();
				break;
			}
		}

		return emailId;
	}

}
