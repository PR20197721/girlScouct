package org.girlscouts.vtk.osgi.service;

public interface SSOConfigurationService {

    public String getApiKey();

    public String getSPName();

    public String getLogInPath();

    public String getLogOutPath();

    public String getScreenSet();

    public Integer getSessionExpiration();

}
