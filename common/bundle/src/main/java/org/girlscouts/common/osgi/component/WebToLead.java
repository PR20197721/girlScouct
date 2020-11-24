package org.girlscouts.common.osgi.component;

import java.util.Set;

public interface WebToLead {

    public String getOID();
    public String getApiURL();
    public Set<String> getExpectedParams();

}
