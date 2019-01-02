package org.girlscouts.common.encryption.impl;

import java.io.PrintStream;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.common.dataimport.impl.DataImporterFactoryImpl;
import org.girlscouts.common.encryption.FormEncryption;
import org.girlscouts.common.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@Service(value = FormEncryption.class)
@Properties({
		@Property(name = "service.pid", value = "org.girlscouts.common.encryption.FormEncryption", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts Form Encryption Service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class FormEncryptionImpl implements FormEncryption {
	private static Logger log = LoggerFactory
		    .getLogger(DataImporterFactoryImpl.class);
	public static final char SEPARATOR = (char)30;
	public String encrypt(String plainText) throws GirlScoutsException {
		try {
            AESCipher aes =AESCipher.getInstance();
            log.error("===========form encrypting===========");
            Secret secret = aes.encrypt(plainText);
            return secret.toString();
            
        } catch (Exception e) {
        	log.error("Encryption Exception");
        	log.error(e.toString());
            throw new GirlScoutsException(e,"Encryption Exception");
        }
	}

	public String decrypt(String secret) throws GirlScoutsException {
		try {
            AESCipher aes =AESCipher.getInstance();
            log.error("===========form decrypting===========");
            String[] tmpStrings=secret.split(String.valueOf(SEPARATOR));
            Secret crypt=new Secret(tmpStrings[0], tmpStrings[1], Secret.ITERATIONS);
            aes.decrypt(crypt);
            return crypt.getValue();
        } catch (Exception e) {
        	log.error("Decryption Exception");
        	log.error(e.toString());
            throw new GirlScoutsException(e,"Decryption Exception");
        }
	}



}
