package org.girlscouts.vtk.ejb;

import javax.jcr.Session;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

@Component
@Service(value = SessionFactoryX.class) // why is this here?
public class SessionFactoryX extends BasePoolableObjectFactory {

	private SlingRepository repository;

	public SessionFactoryX() {
	}

	public SessionFactoryX(SlingRepository repository) {
		this.repository = repository;
	}

	public Session makeObject() throws Exception {
		return repository.loginAdministrative(null);
	}

	public void destroyObject(Session obj) throws Exception {
		if (obj != null)
			obj.logout();
		obj = null;

	}

	@Activate
	void activate() {

	}

}
