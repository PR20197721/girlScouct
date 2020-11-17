package org.girlscouts.common.osgi.component.impl;

import com.adobe.granite.asset.api.Asset;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.osgi.component.CouncilCodeToPathMapper;
import org.girlscouts.common.osgi.component.WebToCase;
import org.girlscouts.common.osgi.service.GSEmailService;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Component(service = {
		WebToCase.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToCaseImpl")
@Designate(ocd = WebToCaseImpl.Config.class)
public class WebToCaseImpl implements WebToCase {

	private static final Logger log = LoggerFactory.getLogger(WebToCaseImpl.class);

	private String oid;
	private String apiURL;
	private String caseSource;
	private String tier;
	private String recordType;

	private boolean sendEmail;
	private Map<String, String> recaptchaMap = new HashMap<>();
	private final Set<String> expectedParams = new HashSet<>();

	private Map<String, String> councilEmailMap = new HashMap<>();

	private Map<String, Object> resolverParams = new HashMap<>();

	@Reference
	private ResourceResolverFactory resolverFactory;

    @Reference
    private GSEmailService gsEmailService;

	@Reference
	protected SlingSettingsService slingSettings;

    @Reference
    private CouncilCodeToPathMapper councilCodeToPathMapper;

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
		this.caseSource = config.caseSource();
		this.tier = config.tier();
		this.recordType = config.recordType();
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
    @Override
    public void sendEmail(SlingHttpServletRequest request, List<NameValuePair> data, List<String> errors, boolean debug) {
	    log.debug("Sending Email instead of Post to Salesforce");
        String councilCode = getCouncilCode(request, councilCodeToPathMapper);
        String mailTo = getMailToValue(councilCode);
        if(StringUtils.isNotBlank(councilCode) && StringUtils.isNotBlank(mailTo)){
            List<String> mailToAddresses = new ArrayList<>();
            mailToAddresses.add(mailTo);
            final StringBuilder emailBodyBuilder = new StringBuilder();
            emailBodyBuilder.append(request.getScheme());
            emailBodyBuilder.append("://");
            emailBodyBuilder.append(request.getServerName());
            if ((request.getScheme().equals("https") && request.getServerPort() != 443)
                    || (request.getScheme().equals("http") && request.getServerPort() != 80)) {
                emailBodyBuilder.append(':');
                emailBodyBuilder.append(request.getServerPort());
            }
            emailBodyBuilder.append(request.getRequestURI());
            emailBodyBuilder.append("</br>"+"\n\nValues:\n\n"+"</br>");
            String subject = "Form Data:";
            for(NameValuePair nameValuePair:data){
                if("subject".equalsIgnoreCase(nameValuePair.getName()) && StringUtils.isNotBlank(nameValuePair.getValue())){
                    subject = nameValuePair.getValue();
                }
                if(!nameValuePair.getName().contains("debug") && !nameValuePair.getName().contains("captcha")){
                	if(nameValuePair.getName().equals("00N5A00000M7IGz")) {
                		emailBodyBuilder.append("Preferred Method of Contact: "+nameValuePair.getValue()+"</br>"+"\r\n");
                		log.debug("Preferred Method of Contact: "+nameValuePair.getValue());
                    } else if(nameValuePair.getName().equals("00N5A00000M7IGy")) {
                    	emailBodyBuilder.append("Best Time To Call: "+nameValuePair.getValue()+"</br>"+"\r\n");
                    	log.debug("Best Time To Call: "+nameValuePair.getValue());
                    } else if(nameValuePair.getName().equals("00N5A00000M7IH0")) {
                    	emailBodyBuilder.append("Zip Code: "+nameValuePair.getValue()+"</br>"+"\r\n");
                    	log.debug("Zip Code:"+nameValuePair.getValue());
                    }else {
                    emailBodyBuilder.append(nameValuePair.getName()+": "+nl2br(nameValuePair.getValue())+"</br>"+"\r\n");
                    log.debug(nameValuePair.getName()+": "+nameValuePair.getValue());
                    }
                	emailBodyBuilder.append("</br>");
                }
            }
            try {
                log.debug("Sending email: subject:{}, to:{}, body:{}",subject, mailToAddresses, emailBodyBuilder);
                gsEmailService.sendEmail(subject, mailToAddresses, emailBodyBuilder.toString(), (String) null);
            }catch (Exception e){
                errors.add("Unable to send email");
                log.error("Unable to send email ", e);
            }
        }
    }

    private static String nl2br(String text) {
        return text.replace("\n\n", "<p>").replace("\n", "<br>");
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
    @Override
    public String getCaseSource() {
        return this.caseSource;
    }

    @Override
    public String getTier() {
        return tier;
    }

    @Override
    public String getRecordType() {
        return recordType;
    }



    @ObjectClassDefinition(name = "Girl Scouts Web To Case Configuration Service")
	public @interface Config {
		@AttributeDefinition(name = "Send Email")
		boolean sendEmail()

		default false;

		@AttributeDefinition(name = "Path to email mapping file")
		String emailMappings()

		default "/content/dam/girlscouts-shared/Council_Email_Mapping.csv";

		@AttributeDefinition(name = "Organization ID")
		String oid()

		default "00D0n00000011wp";

        @AttributeDefinition(name = "Case Source Param Name")
        String caseSource() default "00N0n000001oowC";

        @AttributeDefinition(name = "Tier Param Name")
        String tier() default "00N0n000001oowl";

        @AttributeDefinition(name = "Record Type Value")
        String recordType() default "0120n0000001Q7L";

		@AttributeDefinition(name = "Form submit path")
		String apiURL()

		default "https://gsendtoend1--uat.my.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";

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
		String[] expectedParams() default { "orgid", "00N0n000001oowC", "00N0n000001oowl", "00N0n000001oowo",
				"00N0n000001oown", "00N0n000001aKZ8", "CouncilCode", "origin", "status", "name", "email", "phone",
				"type", "subject", "description", "g-recaptcha-response", "captcha_settings", "debug", "debugEmail" };
	}

}
