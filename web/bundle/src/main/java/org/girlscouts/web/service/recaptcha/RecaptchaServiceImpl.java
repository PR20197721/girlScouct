package org.girlscouts.web.service.recaptcha;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.apache.felix.scr.annotations.*;
import org.girlscouts.common.osgi.component.impl.CouncilCodeToPathMapperImpl;
import org.girlscouts.web.osgi.service.WebToCaseMigration;
import org.girlscouts.web.osgi.service.impl.WebToCaseMigrationImpl;
import org.girlscouts.web.osgi.service.impl.JoinVolunteerMigrationImpl.Config;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = {RecaptchaService.class}, immediate = true, name = "Girl Scouts GS Recaptcha Service")
@Designate(ocd = RecaptchaServiceImpl.Config.class)
public class RecaptchaServiceImpl implements  RecaptchaService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String secret = "";
	
	@Activate
    private void activate(Config config) {
		secret = config.secretKey();
	}
	
	
	@Override
	public boolean captchaSuccess(String response) {
		JsonObject responseObj = validateCaptcha(secret, response);
		if (null != responseObj && null != responseObj.get("success")) {
			return responseObj.get("success").getAsBoolean();
		} else {
			return false;
		}
	}
	
	
	private JsonObject validateCaptcha(String secret, String response){
	    JsonObject jsonObject = null;
	    URLConnection connection = null;
	    InputStream inputStream = null;
	    String charset = java.nio.charset.StandardCharsets.UTF_8.name();

	    String url = "https://www.google.com/recaptcha/api/siteverify";
	    try {            
	        String query = String.format("secret=%s&response=%s", 
        					URLEncoder.encode(secret, charset), 
        					URLEncoder.encode(response, charset));

	        connection = new URL(url + "?" + query).openConnection();
	        inputStream = connection.getInputStream();
	        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
	        jsonObject = new Gson().fromJson(jsonReader, JsonObject.class);

	    } catch (Exception e) {
	    	logger.error("Exception occurred in validateCaptcha method : {}", e.getMessage());
	        logger.debug("Exception occurred in validateCaptcha method : {}", e);
	    }
	    finally {
	        if (inputStream != null) {
	            try {
	            	inputStream.close();
	            } catch (IOException e) {
	            	logger.error("IO Exception occurred in closing inputstream : {}", e.getMessage());
	    	        logger.debug("IO Exception occurred in closing inputstream : {}", e);
	            }

	        }
	    }
	    return jsonObject;
	}
	
	@ObjectClassDefinition(name = "Recaptcha configurations")
    public @interface Config {
        @AttributeDefinition(name = "Secret Key", description = "Secret key for Recaptcha") 
        String secretKey() default "6Lddq5gUAAAAAAl_cYrCis6i0ONWhC_5ClYkPFRA";
    }
	

}
