package org.girlscouts.vtk.impl.helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.girlscouts.vtk.salesforce.Troop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class TroopHashGeneratorImpl implements TroopHashGenerator {
    private static final Logger log = LoggerFactory.getLogger(TroopHashGeneratorImpl.class);
    private static final String BASE = "/vtk-data/";
    private static final String SALT = "\u90B9\u6B23\u822A\u2308\u2208%^%@2H3^&8\u0008\u1308\u0021\u0223\u0046";

    public String hash(String troopId) {
        byte[] origBytes;
        try {
            origBytes = (SALT + troopId).getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digestBytes = md5.digest(origBytes);
            StringBuffer sb = new StringBuffer();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }

            String hash = sb.toString();
            // prepend last seven characters of troopId
            int length = troopId.length();
            String prepend = length >= 7 ? troopId.substring(length - 7) : troopId;
            hash = prepend + "_" + hash;
            log.debug("Hash generated: " + hash);
            return hash;
        } catch (UnsupportedEncodingException e) {
            log.warn("Cannot generate hash from: " + troopId + ". UnsupportedEncodingException.");
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.warn("Cannot generate hash from: " + troopId + ". NoSuchAlgorithmException.");
            return null;
        }
    }

    public String hash(Troop troop) {
        return hash(troop.getTroopId());
    }

    public String getPath(String troopId) {
        String hash = hash(troopId);
        if (hash != null) {
            return BASE + hash;
        } else {
            log.warn("Could not generate hash, use basic obfuscation instead.");
            String reversed = new StringBuilder(troopId).reverse().toString();
            if (reversed.length() > 3) {
                return BASE + reversed.substring(3, reversed.length()) + reversed.substring(0, 3);
            } else {
                return BASE + reversed;
            }
        }
    }

    public String getPath(Troop troop) {
        return getPath(troop.getTroopId());
    }
    
    public String getBase() {
        return BASE;
    }
    
}
