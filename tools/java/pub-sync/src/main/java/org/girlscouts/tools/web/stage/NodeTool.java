package org.girlscouts.tools.web.stage;

import java.text.ParseException;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockManager;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: src_server src_username src_password dst_server dst_username dst_password
// Synchronize src and dst repo
// server example: http://localhost:4502/crx/server/

public class NodeTool 
{
    public static void main(String[] args)
    {
        if (args.length < 4) {
            System.out.println("Stage Updater: adjust links in stage");
            System.out.println("Params: src_server src_username src_password node_path [cmd]");
            System.out.println("Available commands: checkout, unlock");
            System.out.println("Server example: http://localhost:4502/crx/server");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];
        String nodePath = args[3];
        String cmd = args.length >= 5 ? args[4] : null;

        try {
            NodeTool synchronizer = new NodeTool(server, username, password, nodePath);
            if (cmd == null || cmd.equals("checkout")) {
                synchronizer.checkout();
            } else if (cmd.equals("unlock")){
                synchronizer.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
    
    private void checkout() throws RepositoryException, ParseException {
        VersionManager manager = session.getWorkspace().getVersionManager();
        System.out.println("Trying to checkout node: " + nodePath);
        manager.checkout(nodePath);
        session.save();
    }
    
    private void unlock() throws RepositoryException, ParseException {
        LockManager manager = session.getWorkspace().getLockManager();
        System.out.println("Trying to unlock node: " + nodePath);
        manager.unlock(nodePath);
        session.save();
    }
}