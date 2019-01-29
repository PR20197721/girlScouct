package org.girlscouts.common.encryption;


import org.girlscouts.common.exception.GirlScoutsException;

public interface FormEncryption {

    public String encrypt(String plainText) throws GirlScoutsException;
    public String decrypt(String secret) throws GirlScoutsException;
    
}
