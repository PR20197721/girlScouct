package org.girlscouts.vtk.auth.dao;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.ConnectionFactory;
import org.girlscouts.vtk.ejb.SessionFactory;
//import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;

@Component(metatype = true, immediate = true)
@Properties ({
	@Property(name="label", value="Girl Scouts VTK Salesforce DAO Factory"),
	@Property(name="description", value="Girl Scouts VTK Salesforce DAO Factory")
})
@Service(value = SalesforceDAOFactory.class)
public class SalesforceDAOFactory implements ConfigListener {
	@Reference
	ConfigManager configManager;

	@Reference
	TroopDAO troopDAO;

	@Reference
	private ConnectionFactory connectionFactory;
	
	@Reference
	private SessionFactory sessionFactory;

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
		SalesforceDAO dao = new SalesforceDAO(troopDAO, connectionFactory, sessionFactory);
		dao.clientId = clientId;
		dao.clientSecret = clientSecret;
		dao.OAuthUrl = OAuthUrl;
		dao.callbackUrl = callbackUrl;

		return dao;
	}
}
