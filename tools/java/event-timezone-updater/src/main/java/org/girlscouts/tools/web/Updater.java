package org.girlscouts.tools.web;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: server username password
// server example: http://localhost:4503/crx/server/
public class Updater 
{
    public static void main(String[] args)
    {
        if (args.length < 3) {
            System.out.println("VTK meeting updater");
            System.out.println("Params: server username password");
            System.out.println("Server example: http://localhost:4503/crx/server");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];

        try {
            Updater updater = new Updater(server, username, password);
            updater.doUpdate();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    
    private static Map<String, String> MEETING_MAP;
    static {
        MEETING_MAP = new HashMap<String, String>();
    }
    
    private String server;
    private String username;
    private String password;
    private Session session;
    private int allCount = 0;
    private int updatedCount = 0;
    
    public Updater(String server, String username, String password) throws RepositoryException {
        this.server = server;
        this.username = username;
        this.password = password;
        getSession();
    }
    
    private void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }
    
    private void doUpdate() throws RepositoryException {
    }
}