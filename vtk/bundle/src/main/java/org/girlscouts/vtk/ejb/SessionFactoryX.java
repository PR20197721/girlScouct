package org.girlscouts.vtk.ejb;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Session;

@Component
@Service(value = SessionFactoryX.class)
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
