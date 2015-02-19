package org.girlscouts.web.encryption;


import org.girlscouts.web.exception.GirlScoutsException;

public interface FormEncryption {

    public String encrypt(String plainText) throws GirlScoutsException;
    public String decrypt(String secret) throws GirlScoutsException;
    
}
