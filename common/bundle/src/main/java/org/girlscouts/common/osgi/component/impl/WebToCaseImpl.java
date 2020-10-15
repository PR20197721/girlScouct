package org.girlscouts.common.osgi.component.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.girlscouts.common.osgi.component.WebToCase;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.asset.api.Asset;
import com.day.cq.mailer.MailService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;

@Component(service = {
		WebToCase.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToCaseImpl")
@Designate(ocd = WebToCaseImpl.Config.class)
public class WebToCaseImpl implements WebToCase {

	private static final Logger log = LoggerFactory.getLogger(WebToCaseImpl.class);

	private String oid;
	private String apiURL;
	private boolean sendEmail;
	private Map<String, String> recaptchaMap = new HashMap<>();
	private final Set<String> expectedParams = new HashSet<>();

	private Map<String, String> councilEmailMap = new HashMap<>();

	private Map<String, Object> resolverParams = new HashMap<>();

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	protected MailService localService;

	@Reference
	protected SlingSettingsService slingSettings;

	@Reference
	private Replicator replicator;
	protected static final String TEMPLATE_PATH_PROPERTY = "templatePath";
	protected static final String CONFIRM_MAILTO_PROPERTY = "confirmationmailto";
	protected static final String CONFIRMATION_SUBJECT_PROPERTY = "confirmationSubject";
	protected static final String CONFIRMATION_FROM_PROPERTY = "confirmationFrom";

	@Activate
	private void activate(Config config) {
		this.oid = config.oid();
		this.apiURL = config.apiURL();
		this.sendEmail = config.sendEmail();
		String[] recaptchaArr = config.recaptchaMap();
		String emailMappingsPath = config.emailMappings();
		this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		ResourceResolver resourceResolver = null;

		if (recaptchaArr != null) {
			for (String mapping : recaptchaArr) {
				if (mapping != null) {
					String[] keyValPair = mapping.split("::");
					if (keyValPair.length == 2) {
						recaptchaMap.put(keyValPair[0], keyValPair[1]);
					}
				}
			}
		}
		String[] expectedParamsArr = config.expectedParams();
		if (expectedParamsArr != null) {
			for (String paramName : expectedParamsArr) {
				expectedParams.add(paramName);
			}
		}
		try {
			resourceResolver = resolverFactory.getServiceResourceResolver(resolverParams);
			Resource councilMapResource = resourceResolver.getResource(emailMappingsPath);
			councilEmailMap = getCouncilEmailMap(councilMapResource);

		} catch (Exception e) {
			log.error("Error Occurred: ", e);
		} finally {
			try {
				if (resourceResolver != null) {
					resourceResolver.close();
				}
			} catch (Exception e) {
				log.error("Exception is thrown closing resource resolver: ", e);
			}
		}

		log.info("Activated.");
	}

	private Map<String, String> getCouncilEmailMap(Resource councilMapResource) {
		Map<String, String> councilMap = new HashMap<>();
		if (null != councilMapResource) {
			try {
				Asset asset = councilMapResource.adaptTo(Asset.class);
				if (null != asset) {
					String line = "";
					BufferedReader br = new BufferedReader(
							new InputStreamReader(asset.getRendition("original").getStream()));
					while ((line = br.readLine()) != null) {
						String csvData[] = line.split(",");
						councilMap.put(csvData[0], csvData[1]);
					}

				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
		return councilMap;
	}

	public int sendEmail(SlingHttpServletRequest request, Dictionary<String, Object> properties,
			CouncilCodeToPathMapper councilCodeToPathMapper) {
		int status = 200;
		final ResourceBundle resBundle = request.getResourceBundle(null);
		final ValueMap values = ResourceUtil.getValueMap(request.getResource());
		String councilCode = getCouncilCode(request, councilCodeToPathMapper);
		final String mailTo = getMailToValue(councilCode);
		if (null != mailTo && null != localService) {
			status = 200;
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

				final List<String> contentNamesList = new ArrayList<>();
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
						log.debug("skipping form element {} from mail content because it's not in the request", name);
					} else if (rp.isFormField()) {
						buffer.append(name);
						buffer.append(" : \n");
						final String[] pValues = request.getParameterValues(name);
						for (final String v : pValues) {
							if (null == formFields.get(name)) {
								List<String> formField = new ArrayList<>();
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
				email.addTo(mailTo);
				final String[] ccRecs = values.get("cc", String[].class);
				if (ccRecs != null) {
					for (final String rec : ccRecs) {
						email.addCc(rec);
					}
				}

				final String[] bccRecs = values.get("bcc", String[].class);
				if (bccRecs != null) {
					for (final String rec : bccRecs) {
						email.addBcc(rec);
					}
				}
				final String subject = values.get("subject", resBundle.getString("Form Mail"));
				email.setSubject(subject);
				final String fromAddress = values.get("from", "");
				if (fromAddress.length() > 0) {
					email.addReplyTo(fromAddress);
				}

				if (this.log.isDebugEnabled()) {
					this.log.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
							new Object[] { fromAddress, mailTo, subject, buffer });
				}
				localService.sendEmail(email);

				boolean storeContent = values.get("storeContent", false);

				ResourceResolver rr = request.getResourceResolver();
				if (storeContent) {
					String contentPath = values.get("action", null);
					if (contentPath != null && !contentPath.isEmpty()) {
						Node contentBaseNode = null;
						Resource contentResource = rr.getResource(contentPath);
						if (contentResource == null) {
							contentBaseNode = getFormStorageNode(
									rr.getResource("/content/usergenerated").adaptTo(Node.class), contentPath);
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
							log.error("################PARAM NAME IS: " + n);

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
						log.error("Submission Node path is: " + submissionNode.getPath());

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
					String confBody = getTemplate(request, values, formFields, confEmail, rr, attachments);
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
						final String confFromAddress = values.get(CONFIRMATION_FROM_PROPERTY, values.get("from", ""));
						if (confFromAddress.length() > 0) {
							// confEmail.setFrom(confFromAddress);
							confEmail.addReplyTo(confFromAddress);
						}
						if (this.log.isDebugEnabled()) {
							this.log.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
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
						log.debug("Email body null for " + request.getResource().getPath());
					}
				}

			} catch (Exception e) {
				log.error("Error sending email: " + e.getMessage(), e);
				status = 500;
			}
		} else {
			log.error("The mailto configuration is missing in the form begin at " + request.getResource().getPath());
			status = 500;
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
			log.error("GSMailServlet temnplate Path is:  " + templatePath);
			String parsed = "";
			Resource templateResource = rr.getResource(templatePath);
			log.error("GSMailServlet got template path ");
			if (templateResource != null) {
				Resource dataResource = templateResource.getChild("jcr:content/data");
				log.error("Data resource path is: " + dataResource.getPath());
				ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
				log.error("GSMailServlet template content is:  " + templateProps.get("content", ""));
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
			log.error("No valid template found for " + request.getResource().getPath());
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

	private String getMailToValue(String councilCode) {
		String emailId = null;
		for (Map.Entry<String, String> map : councilEmailMap.entrySet()) {
			if (null != councilCode && map.getKey().equals(councilCode)) {
				emailId = map.getValue();
				break;
			}
		}
		return emailId;
	}

	private String getCouncilCode(SlingHttpServletRequest request, CouncilCodeToPathMapper councilCodeToPathMapper) {
		String councilCode = null;
		Resource resource = request.getResource();
		PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
		Page currentPage = pageManager.getContainingPage(resource);
		Page homepage = currentPage.getAbsoluteParent(2);
		Page site = homepage.getParent();
		councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
		return councilCode;
	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Deactivated.");
	}

	@Override
	public String getOID() {
		return this.oid;
	}

	@Override
	public String getApiURL() {
		return this.apiURL;
	}

	@Override
	public Map<String, String> getRecaptchaMap() {
		return this.recaptchaMap;
	}

	@Override
	public boolean isSendEmail() {
		return this.sendEmail;
	}

	@Override
	public Set<String> getExpectedParams() {
		return this.expectedParams;
	}

	@ObjectClassDefinition(name = "Girl Scouts Web To Lead Configuration Service")
	public @interface Config {
		@AttributeDefinition(name = "Send Email")
		boolean sendEmail()

		default false;

		@AttributeDefinition(name = "Path to email mapping file")
		String emailMappings()

		default "/content/dam/girlscouts-shared/Council_Email_Mapping.csv";

		@AttributeDefinition(name = "Organization ID")
		String oid()

		default "00D220000004chr";

		@AttributeDefinition(name = "Form submit path")
		String apiURL()

		default "https://gsdev1--dev1.my.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";

		@AttributeDefinition(name = "Recaptcha Key Map")
		String[] recaptchaMap() default { "6LcuqJgUAAAAAInWzpQHvo-uWPfnVcR7bHPQw9S8::GoogleReCaptchaKeyPair1",
				"6Lddq5gUAAAAAAl_cYrCis6i0ONWhC_5ClYkPFRA::GoogleReCaptchaKeyPair2",
				"6LcDrJgUAAAAAFk6lqMCRX0jckqvll_oO2Z5Cq0c::GoogleReCaptchaKeyPair3",
				"6LdbspgUAAAAAF304uYUmiskh-ju5IYpXI5jWBYL::GoogleReCaptchaKeyPair4",
				"6Ldls5gUAAAAABX4ucAK1NLXKaw9lf7ktxSUdKUI::GoogleReCaptchaKeyPair5",
				"6Le5s5gUAAAAALqs02AZW3iieMcL5XepoIKxlSpN::GoogleReCaptchaKeyPair6",
				"6LcPtJgUAAAAAGwg6r5eVtDnziu3VbTQIv5Tz4mo::GoogleReCaptchaKeyPair7",
				"6LfEtJgUAAAAAB4btse4kjSKN6fBFqy4U1M15dna::GoogleReCaptchaKeyPair8" };

		@AttributeDefinition(name = "Expected Fields")
		String[] expectedParams() default { "orgid", "00N22000000ltnH", "00N22000000ltnp", "00N22000000ltns",
				"00N22000000ltnr", "00N22000000ltnt", "CouncilCode", "origin", "status", "name", "email", "phone",
				"type", "subject", "description", "g-recaptcha-response", "captcha_settings", "debug", "debugEmail" };
	}

}
