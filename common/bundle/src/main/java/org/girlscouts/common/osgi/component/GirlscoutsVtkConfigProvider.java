package org.girlscouts.common.osgi.component;

public interface GirlscoutsVtkConfigProvider {

    public String[] getCouncilMapping();
    public String getHelloUrl();
    public String getLoginUrl();
    public String getLogoutUrl() ;
    public String getRenewUrl();

}
