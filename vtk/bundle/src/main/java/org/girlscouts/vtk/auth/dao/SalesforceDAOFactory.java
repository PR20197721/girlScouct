package org.girlscouts.vtk.auth.dao;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.ConnectionFactory;
//import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;

@Component(label = "Girl Scouts VTK Salesforce DAO Factory", description = "Girl Scouts VTK Salesforce DAO Factory", metatype = true, immediate = true)
@Service(value = SalesforceDAOFactory.class)
public class SalesforceDAOFactory implements ConfigListener {
	@Reference
	ConfigManager configManager;

	@Reference
	TroopDAO troopDAO;

	@Reference
	private ConnectionFactory connectionFactory;

	private String clientId;
	private String clientSecret;
	private String OAuthUrl;
	private String callbackUrl;

	@SuppressWarnings("rawtypes")
	public void updateConfig(Dictionary configs) {
		clientId = (String) configs.get("clientId");
		clientSecret = (String) configs.get("clientSecret");
		OAuthUrl = (String) configs.get("OAuthUrl");
		callbackUrl = (String) configs.get("callbackUrl");

	}

	@Activate
	public void init() {
		configManager.register(this);
	}

	public SalesforceDAO getInstance() {
		SalesforceDAO dao = new SalesforceDAO(troopDAO, connectionFactory);
		dao.clientId = clientId;
		dao.clientSecret = clientSecret;
		dao.OAuthUrl = OAuthUrl;
		dao.callbackUrl = callbackUrl;

		return dao;
	}
}
