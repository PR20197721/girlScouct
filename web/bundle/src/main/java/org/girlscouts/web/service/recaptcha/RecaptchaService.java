package org.girlscouts.web.service.recaptcha;

public interface RecaptchaService {
	
	public boolean captchaSuccess(String secret, String response);
	
}
