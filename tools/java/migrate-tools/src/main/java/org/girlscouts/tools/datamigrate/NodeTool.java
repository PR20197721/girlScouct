package org.girlscouts.tools.datamigrate;

import java.text.ParseException;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockManager;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.commons.JcrUtils;

public class NodeTool 
{
    private String nodePath;
    private Session session;
    
    public NodeTool(String server, String username, String password, String nodePath) throws RepositoryException {
        this.nodePath = nodePath;
        this.session = getSession(server, username, password);
    }
    
    private Session getSession(String server, String username, String password) throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        return session;
    }
    
    void checkout() throws RepositoryException, ParseException {
        VersionManager manager = session.getWorkspace().getVersionManager();
        System.out.println("Trying to checkout node: " + nodePath);
        manager.checkout(nodePath);
        session.save();
    }
    
    void unlock() throws RepositoryException, ParseException {
        LockManager manager = session.getWorkspace().getLockManager();
        System.out.println("Trying to unlock node: " + nodePath);
        manager.unlock(nodePath);
        session.save();
    }
}